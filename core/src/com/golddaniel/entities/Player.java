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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.golddaniel.main.ControllerManager;
import com.golddaniel.main.Globals;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;
import com.golddaniel.main.XboxMapping;

/**
 *
 * @author wrksttn
 */
public class Player extends Entity
{
    TextureRegion tex;
    Vector2 velocity;
    
    
    float width;
    float height;
    
    final float ACCELERATION = Globals.WIDTH*4;
    final float MAX_SPEED = Globals.WIDTH / 4f;

    float angle;
    
    float cooldown = 0;
    float COOLDOWN_MAX = 0.125f;
    
    public Player(WorldModel model)
    {
        
        tex = new TextureRegion(new Texture("geometric/player.png"));
        velocity = new Vector2();
        
        width = 48;
        height = 48;
        
        position = new Vector2( model.WORLD_WIDTH/2f  - width  / 2f, 
                                model.WORLD_HEIGHT/2f - height /2f);
        isAlive = true;
    }

    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        float radius = width > height ? 
                            width/2 : height/2;
        
        if(ControllerManager.controller != null)
        {
            
            //MOVEMENT--------------------------------------------------
            Vector2 leftStick = new Vector2();
            leftStick.x = ControllerManager.controller.
                    getAxis(XboxMapping.L_STICK_HORIZONTAL_AXIS);
            leftStick.y = -ControllerManager.controller.
                    getAxis(XboxMapping.L_STICK_VERTICAL_AXIS);

            if(SharedLibraryLoader.isWindows)
            {
                leftStick.y = -leftStick.y;
            }
            
            if(abs(leftStick.x) < 0.2f)
            {
                leftStick.x = 0;
            }
            if(abs(leftStick.y) < 0.2f)
            {
                leftStick.y = 0;
            }

            if(leftStick.len2() > 0)
            {
                velocity.x += leftStick.x * ACCELERATION * delta;
                velocity.y += leftStick.y * ACCELERATION * delta;
                
                angle = velocity.angle();
            }
            else
            {
                velocity.x = velocity.y = 0;
            }
            
            //-----------------------------------------------------------
            
           
            
            //SHOOTING---------------------------------------------------
            Vector2 rightStick = new Vector2();
            rightStick.x = ControllerManager.controller.
                    getAxis(XboxMapping.R_STICK_HORIZONTAL_AXIS);
            rightStick.y = -ControllerManager.controller.
                    getAxis(XboxMapping.R_STICK_VERTICAL_AXIS);

            if(SharedLibraryLoader.isWindows)
            {
                rightStick.y = -rightStick.y;
            }
            
            if(abs(rightStick.x) < 0.15f) rightStick.x = 0;
            if(abs(rightStick.y) < 0.15f) rightStick.y = 0;

            if(rightStick.len2() > 0.15f*0.15f)
            {
                rightStick.nor();
                
                fireBullets(model, rightStick, radius);    
                
            }
            //-----------------------------------------------------------
            
            if(ControllerManager.controller.getButton(XboxMapping.A))
            {
                
            }

        }
        else
        {
            Vector2 inputDir = new Vector2(0, 0);
            
            if(Gdx.input.isKeyPressed(Keys.W))
            {
                inputDir.y += 1;
            }
            if(Gdx.input.isKeyPressed(Keys.S))
            {
                inputDir.y -= 1;
            }
            if(Gdx.input.isKeyPressed(Keys.A))
            {
                inputDir.x -= 1;
            }
            if(Gdx.input.isKeyPressed(Keys.D))
            {
                inputDir.x += 1;
            }
            inputDir.nor();
            
            if(inputDir.len2() > 0)
            {
                velocity.x += inputDir.x * ACCELERATION * delta;
                velocity.y += inputDir.y * ACCELERATION * delta;
                
                angle = velocity.angle();
            }
            else
            {
                velocity.x = velocity.y = 0;
            }  
            
             
            inputDir.x = inputDir.y = 0;
            if(Gdx.input.isKeyPressed(Keys.UP))
            {
                inputDir.y += 1;
            }
            if(Gdx.input.isKeyPressed(Keys.DOWN))
            {
                inputDir.y -= 1;
            }
            if(Gdx.input.isKeyPressed(Keys.LEFT))
            {
                inputDir.x -= 1;
            }
            if(Gdx.input.isKeyPressed(Keys.RIGHT))
            {
                inputDir.x += 1;
            }
            inputDir.nor();

            if(inputDir.len2() > 0)
            {   
                fireBullets(model, inputDir, radius);   
            }
            
        }
        
        if(velocity.len2() > MAX_SPEED*MAX_SPEED)
        {
            velocity.setLength(MAX_SPEED);
        }
        
        model.applyRadialForce(getMid(), 4000, 256);
        
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        
       
        if(position.x < 0) 
        {
            position.x = 0;
        }
        else if(position.x > model.WORLD_WIDTH - radius*2) 
        {
            position.x = model.WORLD_WIDTH - radius*2;
        }
        
        if(position.y < 0) 
        {
            position.y = 0;
        }
        else if(position.y > model.WORLD_HEIGHT - radius*2)
        {
            position.y = model.WORLD_HEIGHT - radius*2;
        }
        
        cooldown -= delta;
    }

    private void fireBullets(WorldModel model, Vector2 rightStick, float radius)
    {
        if(cooldown <= 0)
        {
            Vector2 bulletPos = new Vector2();

            bulletPos.x = position.x + width/2f;
            bulletPos.y = position.y + height/2f;



            bulletPos.x += rightStick.x*radius;
            bulletPos.y += rightStick.y*radius;


            Messenger.notify(Messenger.EVENT.PLAYER_FIRE);

            Vector3 pos = new Vector3(position, 1);
            pos.x += width/2;
            pos.y += height/2;

            float dif = 3f;
                
            model.createBullet(bulletPos, 
                               rightStick.angle(), 
                               Bullet.TYPE.LASER_1);
            model.createBullet(bulletPos, 
                               rightStick.angle() + dif, 
                               Bullet.TYPE.LASER_1);
            model.createBullet(bulletPos, 
                               rightStick.angle() - dif, 
                               Bullet.TYPE.LASER_1);

            cooldown = COOLDOWN_MAX;
        }
        
    }
    
    public Vector2 getMid()
    {
        return new Vector2(position.x + width/2, position.y + height/2);
    }
    
    @Override
    public void draw(SpriteBatch s)
    {   
        s.draw(tex,
                position.x, position.y,
                width / 2, height / 2,
                width, height,
                1, 1,
                angle);
    }
    
    @Override
    public void dispose()
    {
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x, position.y, width, height);
    }
    
    @Override
    public void kill(WorldModel model)
    {  
       isAlive = false;
        System.out.println("KILL PLAYER");
        int particles = 2048;
        for (int i = 0; i < particles; i++)
        {
            //particle angle
            float pAngle = (float)i/(float)particles*360f;
            model.createParticle(
                    new Vector2(
                            position.x + width/2,
                            position.y + height/2), 
                    pAngle, 
                    MathUtils.random(0.5f, 1f)*3f, 
                    MathUtils.random(0.6f, 0.8f) * model.WORLD_WIDTH*2, 
                    new Color(MathUtils.random(), 
                        MathUtils.random(), 
                        MathUtils.random(), 
                        1f),
                    new Color(MathUtils.random(), 
                        MathUtils.random(), 
                        MathUtils.random(), 
                        1f));
            
            model.killAllEntities();
        }
        model.applyRadialForce(getMid(), -64000, model.WORLD_WIDTH);   
    }
}
