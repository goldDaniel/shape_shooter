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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.AudioSystem;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Bouncer extends Entity
{
    static TextureRegion tex;
    
    Color color;
    
    //should always be normalized
    Vector3 dir;
    float speed;
    
    int prevHealth;
    int health;
    
    float width;
    float height;

    float activeTimer = 2f;

    public Bouncer(Vector3 pos, Vector3 dir, AssetManager assets)
    {
        super(assets);
        if(tex == null)
        {
            tex = new TextureRegion(assets.get("geometric/player.png", Texture.class));
        }

        width = 1f;
        height = 0.5f;
        this.position = pos;
        position.x += width/2;
        position.y += height/2;
        //normalize just in case a normal vector was not passed
        this.dir = dir.nor();
        speed = 2.5f;
        
        color = Color.YELLOW.cpy();

        health = 10;
        prevHealth = health;
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        if(activeTimer > 0)
        {
            activeTimer -= delta;
            for(int i = 0; i < 6; i++)
            {
                float angle = MathUtils.PI * activeTimer * (i + 1);
                float speed = MathUtils.random(12f, 14f);
                speed *= 0.5f*activeTimer;
                Vector3 dim = new Vector3(0.35f, 0.05f, 0.05f);

                model.createParticle(
                        position,
                        new Vector3(MathUtils.cos(angle) * speed,
                                MathUtils.sin(angle) * speed,
                                0),
                        dim,
                        MathUtils.random(0.1f, 0.25f),
                        Color.YELLOW,
                        color);
            }
        }
        else
        {
            if(prevHealth > health)
            {
                color = Color.CYAN;
            }
            else
            {
                color = Color.YELLOW;
            }
            
            position.x += dir.x*speed*delta;
            position.y += dir.y*speed*delta;
            
            prevHealth = health;
        }
        
        
        if(position.x < -model.WORLD_WIDTH / 2f)
        {
            position.x = -model.WORLD_WIDTH / 2f;
            dir.x = -dir.x;
        }
        else if(position.x > model.WORLD_WIDTH / 2f)
        {
            position.x = model.WORLD_WIDTH / 2f;
            dir.x = -dir.x;
        }
        if(position.y < -model.WORLD_HEIGHT / 2f)
        {
            position.y = -model.WORLD_HEIGHT / 2f;
            dir.y = -dir.y;
        }
        else if(position.y > model.WORLD_HEIGHT / 2f)
        {
            position.y = model.WORLD_HEIGHT / 2f;
            dir.y  = -dir.y;
        }

        model.applyRadialForce(position, 150*delta, width);
    }

    @Override
    public void draw(SpriteBatch s)
    {   
        if(activeTimer <= 0)
        { 
            color.a = 1f;
        }
        else
        {
            color.a = 0.45f;
        }
        s.setColor(color);

        s.draw(tex,
                    position.x - width / 2f, position.y - height / 2f,
                    width / 2, height / 2,
                    width, height,
                    1, 1,
                    new Vector2(dir.x, dir.y).angle());
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width / 2f, position.y - height /2f, height, height);
    }
    
    public void kill()
    {
        isAlive = false;
    }
    
    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer <= 0)
        {
            health--;
            if(health <= 0)
            {
                int particles = 32;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles*360f;

                    angle += MathUtils.random(-2.5f, 2.5f);

                    Vector3 dim = new Vector3(0.5f, 0.05f, 0.05f);

                    //reuse speed variable when we kill
                    //totally was on purpose and not accidental
                    speed = MathUtils.random(5f, 7f);

                    model.createParticle(
                            position.cpy(),
                            new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.YELLOW,
                            Color.WHITE);

                    speed = MathUtils.random(5f, 7f);

                    model.createParticle(
                            position.cpy(),
                            new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.WHITE,
                            Color.ORANGE);
                }

                isAlive = false;

                model.addScore(2);
                model.createMultipliers(position, 6);
                model.applyRadialForce(position, 20f,
                                 width * 1.5f,
                                        Color.YELLOW.cpy().fromHsv(55, 0.55f, 0.75f));

                AudioSystem.playSound(AudioSystem.SoundEffect.ENEMY_DEATH);
            }
        }
    }
    
    public boolean isActive()
    {
        return activeTimer <= 0;
    }
}
