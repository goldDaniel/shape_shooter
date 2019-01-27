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
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Player extends Entity implements ControllerListener
{
    float hue;

    TextureRegion tex;
    Vector3 velocity;
    Vector3 moveDir;
    Vector3 shootDir;

    public float width;
    public float height;

    float angle;

    //weapon cooldown, should probably move into its own module
    float cooldown = 0;

    public static class PS4
    {


        public static final int AXIS_LEFT_HORIZONTAL;
        public static final int AXIS_LEFT_VERTICAL;
        public static final int AXIS_RIGHT_HORIZONTAL;
        public static final int AXIS_RIGHT_VERTICAL;

        static
        {
            if (SharedLibraryLoader.isWindows)
            {
                AXIS_LEFT_HORIZONTAL = 3;
                AXIS_LEFT_VERTICAL = 2;

                AXIS_RIGHT_HORIZONTAL = 1;
                AXIS_RIGHT_VERTICAL = 0;
            }
            else if(SharedLibraryLoader.isLinux)
            {
                AXIS_LEFT_HORIZONTAL = 0;
                AXIS_LEFT_VERTICAL = 1;

                AXIS_RIGHT_HORIZONTAL = 3;
                AXIS_RIGHT_VERTICAL = 4;
            }
            else
            {
                AXIS_LEFT_VERTICAL = -1;
                AXIS_LEFT_HORIZONTAL = -1;
                AXIS_RIGHT_HORIZONTAL = -1;
                AXIS_RIGHT_VERTICAL = -1;
            }
        }

    }

    @Override
    public void connected(Controller controller)
    {

    }

    @Override
    public void disconnected(Controller controller)
    {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode)
    {
        System.out.println(buttonCode);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode)
    {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value)
    {



        final float DEADZONE = 0.2f;


        if (value * value > DEADZONE * DEADZONE)
        {
            System.out.println(axisCode + " :  " + value);
        }

        if (axisCode == PS4.AXIS_LEFT_HORIZONTAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                moveDir.x = value;
            } else
            {
                moveDir.x = 0;
            }
        }
        if (axisCode == PS4.AXIS_LEFT_VERTICAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                moveDir.y = -value;
            } else
            {
                moveDir.y = 0;
            }
        }

        if (axisCode == PS4.AXIS_RIGHT_HORIZONTAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                shootDir.x = value;
            } else
            {
                shootDir.x = 0;
            }
        }
        if (axisCode == PS4.AXIS_RIGHT_VERTICAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                shootDir.y = -value;
            } else
            {
                shootDir.y = 0;
            }
        }
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value)
    {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
    {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
    {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
    {
        return false;
    }





    public enum WEAPON_TYPE
    {
        RAPID,
        SPREAD,
        TWIN,
    }

    private WEAPON_TYPE weaponType;

    public Player()
    {
        tex = new TextureRegion(new Texture("geometric/player.png"));



        width = 0.5f;
        height = 0.5f;
        
        position = new Vector3(0, 0, 0);
        velocity = new Vector3();
        moveDir = new Vector3();
        shootDir = new Vector3();
        
        isAlive = true;

        weaponType = WEAPON_TYPE.SPREAD;

        Controllers.addListener(this);
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

        if(SharedLibraryLoader.isWindows || SharedLibraryLoader.isLinux)
        {
            float MAX_SPEED = 5.5f;


            if(moveDir.isZero())
            {
                velocity.scl(0.9999f);
            }
            else
            {
                velocity.add(moveDir.cpy().scl(MAX_SPEED));
            }
            velocity.limit(MAX_SPEED);

            if(shootDir.len2() > 0)
            {
                fireBullets(model, new Vector3(shootDir.x, shootDir.y, 0));
            }
        }

        angle += delta;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        position.z = 0.00f;

        velocity.scl(5 * delta);
       
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
        model.applyRadialForce(pos, 250 * delta ,width * 1.5f);

        createParticleTrail(model);
    }

    private void createParticleTrail(WorldModel model)
    {
        Color start = Color.RED.cpy().fromHsv(hue, 1f, 1f);
        Color end = Color.RED.cpy().fromHsv(hue + 180f, 1f, 1f);
        for (int i = 0; i < 10; i++)
        {
            Vector3 pos = new Vector3(position);

            Vector2 dir = new Vector2(moveDir.x, moveDir.y);

            pos.x -= MathUtils.cos(dir.angleRad()) * width / 2f;
            pos.y -= MathUtils.sin(dir.angleRad()) * height / 2f;

            float lifespan = 0.2f + MathUtils.random(-0.1f, 0.1f);

            Vector3 vel = velocity.cpy().scl(-1f);

            vel.x += MathUtils.random(-1f, 1f);
            vel.y += MathUtils.random(-1f, 1f);

            Vector3 dim = new Vector3(0.1f, 0.1f, 0.1f);

            model.createParticle(pos, vel, dim, lifespan, start, end);
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
                float speed = 19f;
                dir.x += MathUtils.random(-0.25f, 0.25f);
                dir.nor();

                model.createBullet(bulletPos,
                        speed,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                cooldown = 0.045f;
            }
            else if(weaponType == WEAPON_TYPE.SPREAD)
            {
                float speed = 17f;
                model.createBullet(bulletPos,
                        speed,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                float dif = 1.5f;
                //adds this amount of bullets to each side
                //i.e. extrabullets*2 gets added
                for (int i = 0; i < 2; i++)
                {
                    model.createBullet(bulletPos,
                            speed,
                            dir.angle() + dif*(i+1),
                            Bullet.TYPE.LASER_1);
                    model.createBullet(bulletPos,
                            speed,
                            dir.angle() - dif*(i+1),
                            Bullet.TYPE.LASER_1);
                }
                cooldown = 0.08f;
            }
            else if(weaponType == WEAPON_TYPE.TWIN)
            {
                float speed = 20f;
                bulletPos.x += width / 2f;

                model.createBullet(bulletPos,
                        speed,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                bulletPos.x -= width;

                model.createBullet(bulletPos,
                        speed,
                        dir.angle(),
                        Bullet.TYPE.LASER_1);

                cooldown = 0.15f;
            }
        }       
    }

    public void draw(SpriteBatch s)
    {
        Vector2 dir = new Vector2(moveDir.x, moveDir.y);

        s.setColor(Color.WHITE);
        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1f, 1f,
                dir.angle());
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

            Vector3 dim = new Vector3(0.01f, 0.01f, 0.01f);
//
//            model.createParticle(
//                    new Vector3(
//                            position.x + width/2,
//                            position.y + height/2,
//                            0),
//                    dim,
//                    pAngle,
//                    MathUtils.random(0.2f, 0.5f),
//                    MathUtils.random(0.6f, 0.8f) * 2,
//                    new Color(MathUtils.random(),
//                        MathUtils.random(),
//                        MathUtils.random(),
//                        1f),
//                    new Color(MathUtils.random(),
//                        MathUtils.random(),
//                        MathUtils.random(),
//                        1f)
//            );
        }
        model.killAllEntities();
    }

    public void setWeaponType(WEAPON_TYPE type)
    {
        this.weaponType = type;
    }
}
