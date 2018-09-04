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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.Globals;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Boid extends Entity
{
    static float SPEED_MAX = 300f;
    
    static ShapeRenderer debug = new ShapeRenderer();
    
    static Array<Boid> boids = new Array<Boid>();
    
    static TextureRegion tex = new TextureRegion(new Texture("geometric/player.png"));
    
    Vector2 velocity;
    Vector2 acceleration;
    
    Timer activeTimer;
    
    Color color;
    
    float width;
    float height;
    
    boolean active;
    
    public Boid(Vector2 position)
    {        
        this.position = new Vector2(position);
        
        float angle = MathUtils.random(MathUtils.PI*2);
        acceleration = new Vector2();
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle));
        
        activeTimer = new Timer();
        
        width = 32;
        height = 32f;
        
        active = false;
        isAlive = true;
        
        color = Color.CORAL.cpy();
     
        
        boids.add(this);
    }
    
    private Vector2 cohesion()
    {
        Vector2 result = new Vector2();
        int count = 0;
        float range =72;
        
        Vector2 sum = new Vector2();
        for(Boid b : boids)
        {
            float dist = position.dst(b.position);
            
            if(dist > 0 && dist < range)
            {
                sum.add(b.position);
                count++;
            }
        }
        
        if(count > 0)
        {
            sum.scl(1f/(float)count);
        }
        
        return result;
    }
    
    private Vector2 allignment()
    {
        Vector2 result = new Vector2();
        float range = 96;
        int count = 0;
        
        Vector2 sum = new Vector2();
        for(Boid b : boids)
        {
            float dist = position.dst(b.position);
            
            if(dist > 0 && dist < range)
            {
                sum.add(b.velocity);
                count++;
            }
        }
        
        if(count > 0)
        {
            sum.setLength(SPEED_MAX);
            
            result = sum.sub(velocity);
            result.limit(SPEED_MAX);
        }
        
        return result;
    }
    
    private Vector2 separation()
    {
        Vector2 result = new Vector2();
    
        int count = 0;
        
        float range = 64;
        
        Vector2 sum = new Vector2();
        for(Boid b : boids)
        {
            float dist = position.dst(b.position);
            
            if(dist > 0 && dist < range)
            {
                Vector2 diff = position.cpy().sub(b.position);
                diff.nor();
                diff.scl(1f/dist);
                sum.add(diff);
                count++;
            }
        }
        
        if(count > 0)
        {
            sum.scl(1f/(float)count);
        }
        
        if(sum.len2() > 0)
        {
            //steering = desired-velocity
            sum.nor();
            sum.scl(SPEED_MAX);
            
            result = sum.sub(velocity);
            result.limit(SPEED_MAX);
        }
        return result;
    }
    
    private Vector2 seek(Vector2 target)
    {
        Vector2 desiredVelocity = target.cpy().sub(position);
        desiredVelocity.setLength(SPEED_MAX);
        
        return desiredVelocity.sub(velocity).setLength(SPEED_MAX);
    }
    
    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    @Override
    public void update(WorldModel model, float delta)
    {
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
        
        if(active)
        {
            borderCheck(model);
            
            Vector2 separation = separation();
            Vector2 allignment = allignment();
            Vector2 cohesion   = cohesion();
            Vector2 boundary   = calculateBoundary(model.WORLD_WIDTH, model.WORLD_HEIGHT);
            Vector2 seek = new Vector2();
            
            float dist = Float.MAX_VALUE;
            float range = 512;
            if(model.getEntityType(Player.class).size > 0)
            {
                Vector2 target = model.getEntityType(Player.class).first().position;
                dist = target.dst(position);
                
                if(dist < range)
                {
                    seek.set(seek(target)).scl(dist/range);
                }
            }
            
            acceleration.add(separation);
            acceleration.add(allignment);
            acceleration.add(cohesion);
            acceleration.add(boundary);
            acceleration.add(seek);
            
            //turn faster toward the target when near
            if(dist < range)
            {
                acceleration.limit(SPEED_MAX/4f*delta);
            }
            else
            {
                acceleration.limit(SPEED_MAX/16f*delta);
            }
            velocity.add(acceleration);
            velocity.limit(SPEED_MAX*delta);
            
            position.add(velocity);
            
            acceleration.set(0, 0);
        
            
            
            model.applyRadialForce(position, 800f, 128);
        }
        
    }

    private void borderCheck(WorldModel model)
    {
        if(position.x < 0)
        {
            position.x = 0;
            
        }
        else if(position.x + width > model.WORLD_WIDTH)
        {
            position.x = model.WORLD_WIDTH - height;
            
        }
        
        if(position.y < 0)
        {
            position.y = 0;
        }
        else if(position.y + height > model.WORLD_HEIGHT)
        {
            position.y = model.WORLD_HEIGHT - height;
        }
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
            color.a = 0.55f;
        }
        s.setColor(color);
        s.draw(tex,
                    position.x, position.y,
                    width / 2, height / 2,
                    width, height,
                    1, 1,
                    velocity.angle());
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        //immune until active
        if(active)
        {
            int particles = 32;
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;
                
                angle += MathUtils.random(-2.5f, 2.5f);
                
                if(i % 2 == 0)
                {
                    model.createParticle(
                            new Vector2(
                                position.x + width/2,
                                position.y + height/2),
                            angle,
                            MathUtils.random(0.5f, 0.9f),
                            -Globals.WIDTH/2f,
                            Color.CYAN.cpy(),
                            Color.MAGENTA.cpy(),
                            Particle.TYPE.SPIN);
                }
            }

            for(int i = 0; i < 5; i++)
            {
                model.applyRadialForce(
                getMid(), 
                2000, 
                256);
            }
            Messenger.notify(Messenger.EVENT.BOUNCER_DEAD);
            isAlive = false;
        }
    }

    private Vector2 getMid()
    {
        return new Vector2(position.x + width / 2f, position.y + height / 2f);
    }
    
    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void dispose()
    {
        boids.removeValue(this, true);
    }

    private Vector2 calculateBoundary(float WORLD_WIDTH, float WORLD_HEIGHT)
    {
        Vector2 result = new Vector2();
        
        float range = 384;
        
        Vector2 wallCheck = new Vector2();
        
        //left wall
        wallCheck.x = 0;
        wallCheck.y = position.y;
        float dist = position.dst(wallCheck);
        
        if(dist < range)
        {
            result.add(SPEED_MAX*(1f - dist/range), 0);
        }
        
        //right wall
        wallCheck.x = WORLD_WIDTH;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(-SPEED_MAX*(1f - dist/range), 0);
        }
        
        //bottom wall
        wallCheck.x = position.x;
        wallCheck.y = 0;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, SPEED_MAX*(1f - dist/range));
        }
        
        //top wall
        wallCheck.y = WORLD_HEIGHT;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, -SPEED_MAX*(1f - dist/range));
        }
        
        return result;
    }
    
    public void setVelocity(Vector2 v)
    {
        velocity.set(v);
    }
    
    public boolean isActive()
    {
        return active;
    }
}
