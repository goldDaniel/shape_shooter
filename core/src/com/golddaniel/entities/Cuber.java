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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.Globals;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Cuber extends Entity
{
    int health;
    
    
    static Texture tex = new Texture("geometric/dashedSquare.png");
    
    Timer dirTimer;
    
    Vector3 dir;
    
    int width;
    int height;
    
    boolean active;
    
    public Cuber(Vector3 pos)
    {
        active = false;
        health = 9;
        isAlive = true;
        position = new Vector3(pos);
        
        dirTimer = new Timer();
        
        dir = new Vector3();
        
        width = height = 64;
        
        boolean axis = MathUtils.randomBoolean();
        
        boolean direction = MathUtils.randomBoolean();
        
        
        if(axis)//horizontal
        {
            dir.y = 0;
            dir.x = direction ? 1 : -1;
            
        }
        else//vertical
        {
            dir.x = 0;
            dir.y = direction ? 1 : -1;
        }
    }

    private Vector3 getMid()
    {
        return position.cpy().add(width/2f, height/2f, 0);
    }
    
    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    @Override
    public void update(final WorldModel world, float delta)
    {
        if(active)
        {
            if(dirTimer.isEmpty())
            {
                dirTimer.scheduleTask(
                new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        if(world.getPlayer() != null)
                        {
                            Vector3 pPos = world.getPlayer().position.cpy();

                            float xDist = pPos.x - position.x;
                            float yDist = pPos.y - position.y;

                            if(abs(xDist) > abs(yDist))
                            {
                                dir.y = 0;
                                if(xDist > 0)
                                {
                                    dir.x = 1;
                                }
                                else
                                {
                                    dir.x = -1;
                                }
                            }
                            else
                            {
                                dir.x = 0;
                                if(yDist > 0)
                                {
                                    dir.y = 1;
                                }
                                else
                                {
                                    dir.y = -1;
                                }
                            }
                        }
                        else
                        {
                            if(abs(dir.x) > 0)
                            {
                                dir.x = 0;
                                dir.y = MathUtils.randomBoolean() ? 1 : -1;
                            }
                            else
                            {
                                dir.y = 0;
                                dir.x = MathUtils.randomBoolean() ? 1 : -1;
                            }
                        }
                    }
                }, 0.75f);
            }
            
            position.add(dir.cpy().scl(400f*delta));
            
            if(position.x < 0)
            {
                position.x = 0;
                dir.x = -dir.x;
            }
            else if(position.x > world.WORLD_WIDTH - width)
            {
                position.x = world.WORLD_WIDTH - width;
                dir.x = -dir.x;
            }
            
            if(position.y < 0)
            {
                position.y = 0;
                dir.y = -dir.y;
            }
            else if(position.y > world.WORLD_HEIGHT - height)
            {
                position.y = world.WORLD_HEIGHT - height;
                dir.y = -dir.y;
            }

            world.applyRadialForce(getMid(), 1000f, 192);
        }
        else
        {
            if(dirTimer.isEmpty())
            {
                dirTimer.scheduleTask(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        active = true;
                    }
                }, 2f);
            }
        } 
    }

    @Override
    public void draw(SpriteBatch s)
    {
        Color c = Color.LIME.cpy();
        if(!active)
        {
            c.a = 0.4f;
        }
        s.setColor(c);
        
        s.draw(tex, position.x, position.y, width, height);
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(!active) return;
        health--;
        if(health <= 0)
        {
            isAlive = false;
            model.applyRadialForce(getMid(), 10000f, 256);
            
            int particles = 32;
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;

                angle += MathUtils.random(-2.5f, 2.5f);

                model.createParticle(
                        getMid(), 
                        angle, 
                        MathUtils.random(0.5f, 0.65f), 
                        1,
                        Color.LIME, 
                        Color.WHITE);
                
                model.createParticle(
                        getMid(), 
                        angle, 
                        MathUtils.random(0.55f, 0.75f), 
                        1f/2f,
                        Color.RED, 
                        Color.PINK);
            } 
        } 
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void dispose()
    {
    }

    public boolean isActive()
    {
        return active;
    }
    
}
