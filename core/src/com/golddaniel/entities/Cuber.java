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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.AudioSystem;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Cuber extends Entity
{
    int health;
    
    
    static Texture tex;
    
    Vector3 dir;
    
    float width;
    float height;
    
    float activeTimer = 2f;

    float dirTimer = 0;
    
    public Cuber(Vector3 pos, AssetManager assets)
    {
        super(assets);

        if(tex == null)
        {
            tex = assets.get("texture.png", Texture.class);
        }

        health = 9;
        isAlive = true;
        position = new Vector3(pos);

        dir = new Vector3();
        
        width = height = 0.4f;
        
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
    public void update(final WorldModel model, float delta)
    {
        if(activeTimer <= 0)
        {
            if(dirTimer <= 0)
            {
                dirTimer = 0.5f;

                if(model.getPlayer() != null)
                {
                    Vector3 pPos = model.getPlayer().position.cpy();

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
            else
            {
                dirTimer -= delta;
            }

            position.add(dir.cpy().scl(2f*delta));
            
            if(position.x < -model.WORLD_WIDTH/2f)
            {
                position.x = -model.WORLD_WIDTH /2f;
                dir.x = -dir.x;
            }
            else if(position.x > model.WORLD_WIDTH / 2f)
            {
                position.x = model.WORLD_WIDTH / 2f;
                dir.x = -dir.x;
            }
            
            if(position.y < -model.WORLD_HEIGHT /2f)
            {
                position.y = -model.WORLD_HEIGHT / 2f;
                dir.y = -dir.y;
            }
            else if(position.y > model.WORLD_HEIGHT /2f)
            {
                position.y = model.WORLD_HEIGHT / 2f;
                dir.y = -dir.y;
            }
        }
        else
        {
            Vector3 dim = new Vector3(0.35f, 0.05f, 0.05f);
            Vector3 velocity = new Vector3();
            activeTimer -= delta;
            for(int i = 0; i < 6; i++)
            {
                float angle = MathUtils.PI * activeTimer * (i + 1);
                float speed = MathUtils.random(12f, 14f);
                speed *= 0.5f*activeTimer;


                model.createParticle(
                        position,
                        velocity.set(MathUtils.cos(angle) * speed,
                                MathUtils.sin(angle) * speed,
                                0),
                        dim,
                        MathUtils.random(0.1f, 0.25f),
                        Color.RED,
                        Color.WHITE);
            }
        } 
    }

    @Override
    public void draw(SpriteBatch s)
    {
        Color c = Color.RED.cpy();
        if(activeTimer > 0)
        {
            c.a = 0.4f;
        }
        s.setColor(c);
        
        s.draw(tex, position.x - width / 2f, position.y - height / 2f, width, height);
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer > 0) return;
        health--;
        if(health <= 0)
        {
            AudioSystem.playSound(AudioSystem.SoundEffect.ENEMY_DEATH);
            isAlive = false;
            model.applyRadialForce(getMid(), 40f, width *3, Color.RED);

            model.addScore(10);
            model.createMultipliers(position, 5);

            int particles = 32;
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;
                angle += MathUtils.random(-2.5f, 2.5f);


                float speed = 5f + MathUtils.random(-2f, 2f);

                Vector3 dim = new Vector3(0.5f, 0.075f, 0.075f);


                Vector3 vel = new Vector3(
                                MathUtils.cosDeg(angle)*speed,
                                MathUtils.sinDeg(angle)*speed,
                                0);

                model.createParticle(
                        position,
                        vel,
                        dim,
                        MathUtils.random(0.5f, 0.65f),
                        Color.RED,
                        Color.WHITE);
            } 
        } 
    }

    @Override
    public Rectangle getBoundingBox()
    {
    return new Rectangle(position.x - width / 2f, position.y - width / 2f, width, height);
    }

    @Override
    public void dispose()
    {
    }

    public boolean isActive()
    {
        return activeTimer <= 0;
    }
    
}
