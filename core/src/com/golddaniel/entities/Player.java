/*
 * Copyright 2018 .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.golddaniel.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Player extends Entity
{
    float hue;
    
    TextureRegion tex;
    Vector3 velocity;

    
    public float width;
    public float height;

    float angle;
    
    //weapon cooldown, should probably move into its own module
    float cooldown = 0;

    public enum WEAPON_TYPE
    {
        RAPID,
        SPREAD,
        BEAM,
        TWIN,
    }

    private WEAPON_TYPE weaponType;

    public Player()
    {
        tex = new TextureRegion(new Texture("geometric/player.png"));
        velocity = new Vector3();
        
        width = 0.05f;
        height = 0.05f;
        
        position = new Vector3(0, 0, 0);

        
        isAlive = true;

        weaponType = WEAPON_TYPE.SPREAD;
    }


    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }


    public void update(WorldModel model, float delta)
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.U))
        {
            weaponType = WEAPON_TYPE.SPREAD;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.Y))
        {
            weaponType = WEAPON_TYPE.RAPID;
        }
        if(!isAlive) return;

        angle += delta;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        position.z = 0.00f;
       
        //bound inside world rect
        if(position.x < -model.WORLD_WIDTH/2f)
        {
            position.x = -model.WORLD_WIDTH/2f;
        }
        else if(position.x > model.WORLD_WIDTH/2)
        {
            position.x = model.WORLD_WIDTH/2f;
        }
        
        if(position.y < -model.WORLD_HEIGHT/2f)
        {
            position.y = -model.WORLD_HEIGHT/2f;
        }
        else if(position.y > model.WORLD_HEIGHT/2f)
        {
            position.y = model.WORLD_HEIGHT/2f;
        }
        
        
        hue += 90f*delta;
        hue %= 360f;
        
        cooldown -= delta;

        Vector3 pos = position.cpy();
        pos.z = -0.01f;
        model.applyRadialForce(pos, 45f * delta ,0.1f);

        createParticleTrail(model);
        fireBullets(model, new Vector3(0, 1, 0));
    }

    private void createParticleTrail(WorldModel model)
    {
        Color start = Color.RED.cpy().fromHsv(hue, 1f, 1f);
        Color end = Color.RED.cpy().fromHsv(hue + 180f, 1f, 1f);
        for (int i = 0; i < 10; i++)
        {
            Vector3 pos = new Vector3(position);
            pos.x += MathUtils.cosDeg(-100f) * width / 2f;
            pos.y += MathUtils.sinDeg(-100f) * height / 2f;

            float dir = -90f + MathUtils.random(-15f, 15f);
            float lifespan = 0.7f + MathUtils.random(-0.6f, 0.1f);
            float speed = 0.4f + MathUtils.random(-0.1f, 0.1f);

            model.createParticle(pos, dir, lifespan, speed, start, end);

            pos.set(position);
            pos.x += MathUtils.cosDeg(-80f) * width / 2f;
            pos.y += MathUtils.sinDeg(-80f) * height / 2f;

            dir = -90f + MathUtils.random(-15f, 15f);
            lifespan = 0.7f + MathUtils.random(-0.6f, 0.1f);
            speed = 0.4f + MathUtils.random(-0.2f, 0.2f);

            model.createParticle(pos, dir, lifespan, speed, start, end);
        }
    }
    
    private void fireBullets(WorldModel model, Vector3 direction)
    {
        if(cooldown <= 0)
        {
            Vector2 dir = new Vector2(direction.x, direction.y);

            Vector3 bulletPos = new Vector3();
            bulletPos.x = position.x + 0.005f;
            bulletPos.y = position.y + height /2f;

            if(weaponType == WEAPON_TYPE.RAPID)
            {
                dir.x += MathUtils.random(-0.25f, 0.25f);
                dir.nor();

                model.createBullet(bulletPos,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                cooldown = 0.045f;
            }
            else if(weaponType == WEAPON_TYPE.SPREAD)
            {
                model.createBullet(bulletPos,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                float dif = 6.5f;
                //adds this amount of bullets to each side
                //i.e. extrabullets*2 gets added
                for (int i = 0; i < 2; i++)
                {
                    model.createBullet(bulletPos,
                            dir.angle() + dif*(i+1),
                            Bullet.TYPE.LASER_1);
                    model.createBullet(bulletPos,
                            dir.angle() - dif*(i+1),
                            Bullet.TYPE.LASER_1);
                }
                cooldown = 0.25f;
            }
            else if(weaponType == WEAPON_TYPE.TWIN)
            {
                bulletPos.x += width / 2f;

                model.createBullet(bulletPos,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                bulletPos.x -= width;

                model.createBullet(bulletPos,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                cooldown = 0.15f;
            }
        }       
    }

    public void draw(SpriteBatch s)
    {   
        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1f, 1f,
                90f);
    }

    public void dispose()
    {
    }

    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width /2f, position.y - height /2f,
                             width, height);
    }

    public void kill(WorldModel model)
    {
        if(model != null) return;
        isAlive = false;
        int particles = 2048*4;
        for (int i = 0; i < particles; i++)
        {
            //particle angle
            float pAngle = (float)i/(float)particles*360f;
            model.createParticle(
                    new Vector3(
                            position.x + width/2,
                            position.y + height/2,
                            0),
                    pAngle, 
                    MathUtils.random(0.2f, 0.5f),
                    MathUtils.random(0.6f, 0.8f) * 2,
                    new Color(MathUtils.random(),
                        MathUtils.random(), 
                        MathUtils.random(), 
                        1f),
                    new Color(MathUtils.random(), 
                        MathUtils.random(), 
                        MathUtils.random(), 
                        1f)
            );
        }
        model.killAllEntities();
    }

    public void setWeaponType(WEAPON_TYPE type)
    {
        this.weaponType = type;
    }
}
