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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.Globals;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Bouncer extends Entity
{

    static TextureRegion tex = new TextureRegion(new Texture("geometric/player.png"));
    
    Color color;
    
    //should always be normalized
    Vector3 dir;
    float speed;
    
    int prevHealth;
    int health;
    
    float width;
    float height;
    
    boolean active;
   
    Timer activeTimer;
    
    public Bouncer(Vector3 pos, Vector3 dir)
    {
        width = 128;
        height = 32;
        this.position = pos;
        position.x += width/2;
        position.y += height/2;
        //normalize just in case a normal vector was not passed
        this.dir = dir.nor();
        speed = 1/2f;
        
        color = Color.YELLOW.cpy();
        
        active = false;
        
        isAlive = true;
        
        health = 7;
        prevHealth = health;
        
        activeTimer = new Timer();
    }
    
    private Vector3 getMid()
    {
        return new Vector3(position.x + width/2, position.y + height/2, 0);
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        
        //start acting afer 5s
        if(!active)
        {
            if(activeTimer.isEmpty())
            {
                activeTimer.scheduleTask(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        active = true;
                    }
                }, 2);
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
        
        
        if(position.x <= 0)
        {
            position.x = 1;
            dir.x = -dir.x;
        }
        else if(position.x >= model.WORLD_WIDTH - width)
        {
            position.x = model.WORLD_WIDTH - width - 1;
            dir.x = -dir.x;
        }
        if(position.y <= 0)
        {
            position.y = 1;
            dir.y = -dir.y;
        }
        else if(position.y >= model.WORLD_HEIGHT - height)
        {
            position.y = model.WORLD_HEIGHT - height -1;
            dir.y  = -dir.y;
        }

        model.applyRadialForce(getMid(), 2000, 96);
    }

    @Override
    public void draw(SpriteBatch s)
    {   
        if(active)
        { 
            color.a = 1f;
        }
        else
        {
            color.a = 0.45f;
        }
        s.setColor(color);



        s.draw(tex,
                    position.x, position.y,
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
        float radius = width > height ? width : height;
        
        radius /= 2f;
        
        return new Rectangle(position.x, position.y, radius, radius);
    }
    
    public void kill()
    {
        isAlive = false;
    }
    
    @Override
    public void kill(WorldModel model)
    {
        if(active)
        {
            health--;
            if(health <= 0)
            {
                int particles = 128;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles*360f;

                    angle += MathUtils.random(-2.5f, 2.5f);
                    
                    
                    model.createParticle(
                            new Vector3(
                                position.x + width/2,
                                position.y + height/2,
                                0),
                            angle, 
                            MathUtils.random(0.5f, 0.65f), 
                            1f/4,
                            Color.WHITE.cpy(), 
                            Color.GREEN.cpy());

                    model.createParticle(
                        new Vector3(
                                position.x + width/2,
                                position.y + height/2,
                                0),
                        angle, 
                        MathUtils.random(0.6f, 0.7f), 
                        2f,
                        Color.WHITE.cpy(), 
                        Color.YELLOW.cpy());
                }


                isAlive = false;
            }
        }
    }
    
    public boolean isActive()
    {
        return active;
    }
}
