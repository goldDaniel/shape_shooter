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
        width = 1.5f;
        height = 0.75f;
        this.position = pos;
        position.x += width/2;
        position.y += height/2;
        //normalize just in case a normal vector was not passed
        this.dir = dir.nor();
        speed = 4f;
        
        color = Color.YELLOW.cpy();
        
        active = false;
        
        isAlive = true;
        
        health = 20;
        prevHealth = health;
        
        activeTimer = new Timer();
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

        model.applyRadialForce(position, 50*delta, width);
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
                int particles = 512;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles*360f;

                    angle += MathUtils.random(-2.5f, 2.5f);

                    Vector3 dim = new Vector3(0.5f, 0.1f, 0.1f);

                    model.createParticle(
                            position.cpy(),
                            new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.YELLOW,
                            Color.CYAN);

                    speed = MathUtils.random(5f, 7f);

                    model.createParticle(
                            position.cpy(),
                            new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.YELLOW,
                            Color.WHITE);
                }


                isAlive = false;

                model.applyRadialForce(position, 15f, width * 2);
            }
        }
    }
    
    public boolean isActive()
    {
        return active;
    }
}
