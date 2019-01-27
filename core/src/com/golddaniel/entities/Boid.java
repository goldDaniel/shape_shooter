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
 *
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.main.Globals;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Boid extends Entity
{
    static float SPEED_MAX = 2.25f;
   
    static Array<Boid> boids = new Array<Boid>();
    
    static TextureRegion tex = new TextureRegion(new Texture("geometric/player.png"));
    
    Vector3 velocity;
    Vector3 acceleration;
    
    Timer activeTimer;
    
    Color color;
    
    float width;
    float height;
    
    int health = 5;
    
    boolean active;
    
    public Boid(Vector3 position)
    {        
        this.position = new Vector3(position);
        
        float angle = MathUtils.random(MathUtils.PI*2);
        acceleration = new Vector3();
        velocity = new Vector3(MathUtils.cos(angle), MathUtils.sin(angle), 0).scl(SPEED_MAX);
        
        activeTimer = new Timer();
        
        width = 0.5f;
        height = 0.5f;
        
        active = true;
        isAlive = true;
        
        color = Color.CORAL.cpy();
     
        
        boids.add(this);
    }
    
    /**
     *  Returns direction vector based on position of all boids in range
     * 
     * @return 
     */
    private Vector3 cohesion()
    {
        Vector3 result = new Vector3();
        int count = 0;
        float range = 2f;
        
        Vector3 sum = new Vector3();
        for(Boid b : boids)
        {
            float dist = position.dst(b.position);
            
            /**
             * comment out if we currently want average of all boids
             * to pull them together 
             */
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
    
    /**
     *  Returns direction vector based on velocity of all boids in range
     * 
     * @return
     */
    private Vector3 allignment()
    {
        Vector3 result = new Vector3();
        float range = 2.5f;
        int count = 0;
        
        Vector3 sum = new Vector3();
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
    
    /**
     *  Returns direction vector based on separation of all boids in range.
     * 
     * if a boid is in range, add a vector to the sum and give the opposite direction
     * 
     * 
     * @return 
     */
    private Vector3 separation()
    {
        Vector3 result = new Vector3();
    
        int count = 0;
        
        float range = 1.15f;
        
        Vector3 sum = new Vector3();
        for(Boid b : boids)
        {
            float dist = position.dst(b.position);
            
            if(dist > 0 && dist < range)
            {
                Vector3 diff = position.cpy().sub(b.position);
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
    
    private Vector3 seek(Vector3 target)
    {
        Vector3 desiredVelocity = target.cpy().sub(position);
        desiredVelocity.setLength(SPEED_MAX);
        
        return desiredVelocity.sub(velocity).setLength(SPEED_MAX);
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        
        if(active)
        {
            float[] hsv = new float[3];
            color.toHsv(hsv);
            hsv[0] += 60f*delta;
            color.fromHsv(hsv);
            
            borderCheck(model);
            
            Vector3 separation = separation();
            Vector3 allignment = allignment();
            Vector3 cohesion   = cohesion();
            Vector3 boundary   = calculateBoundary(model.WORLD_WIDTH, model.WORLD_HEIGHT);
            Vector3 seek = new Vector3();
            
            float range = 1f;
            if(model.getEntityType(Player.class).size > 0)
            {
                
                Vector3 target = model.getEntityType(Player.class).first().position;
                float dist = target.dst(position);
                
                if(dist < range)
                {
                    seek.set(seek(target));
                }
            }
            
            acceleration.add(separation);
            acceleration.add(allignment);
            acceleration.add(cohesion);
            acceleration.add(boundary);
            acceleration.add(seek);
            
            acceleration.limit(SPEED_MAX/32f*delta);
            
            velocity.add(acceleration);
            velocity.limit(SPEED_MAX*delta);
            
            position.add(velocity);
            
            acceleration.set(0, 0,0);

            Vector3 pos = position.cpy();
            pos.z = -0.1f;
            model.applyRadialForce(position, 150f * delta, width);
        }
    }

    private void borderCheck(WorldModel model)
    {
        if(position.x < -model.WORLD_WIDTH / 2f)
        {
            position.x = -model.WORLD_WIDTH / 2f;
        }
        else if(position.x > model.WORLD_WIDTH / 2f)
        {
            position.x = model.WORLD_WIDTH / 2f;
        }
        
        if(position.y < -model.WORLD_HEIGHT /2f)
        {
            position.y = -model.WORLD_HEIGHT /2f;
        }
        else if(position.y > model.WORLD_HEIGHT / 2f)
        {
            position.y = model.WORLD_HEIGHT / 2f;
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

        Vector2 dir = new Vector2(velocity.x, velocity.y);

        s.draw(tex,
                    position.x - width / 2f, position.y - height /2f,
                    width / 2, height / 2,
                    width, height,
                    1, 1,
                    dir.angle());
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        //immune until active
        if(active)
        {
            health--;
            if(health <= 0)
            {
                int particles = 128;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles*360f;

                    angle += MathUtils.random(-1.5f, 1.5f);

                    Vector3 dim = new Vector3(0.55f, 0.05f, 0.15f);

                    float speed = MathUtils.random(7f, 10f);

                    model.createParticle(
                        position.cpy(),
                        new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                        dim,
                        MathUtils.random(0.1f, 0.5f),
                        Color.WHITE,
                        Color.CYAN);

                    speed = MathUtils.random(2f, 3f);

                    model.createParticle(
                            position.cpy(),
                            new Vector3(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed, 0),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.MAGENTA,
                            Color.WHITE);
                }

                Vector3 pos = position.cpy();
                pos.z = -0.01f;
                model.applyRadialForce(
                              pos,
                        20,
                        (width + height) * 2f);



                active = false;
                isAlive = false;
            }
        }
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width / 2f,
                             position.y - height / 2f,
                                width, height);
    }

    @Override
    public void dispose()
    {
        boids.removeValue(this, true);
    }

    private Vector3 calculateBoundary(float WORLD_WIDTH, float WORLD_HEIGHT)
    {
        Vector3 result = new Vector3();
        
        float range = 3f;
        
        Vector3 wallCheck = new Vector3();

        //left wall
        wallCheck.x = -WORLD_WIDTH/2f;
        wallCheck.y = position.y;
        float dist = position.dst(wallCheck);
        
        if(dist < range)
        {
            result.add(SPEED_MAX*(1f - dist/range), 0, 0);
        }
        
        //right wall
        wallCheck.x = WORLD_WIDTH / 2f;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(-SPEED_MAX*(1f - dist/range), 0, 0);
        }
        
        //bottom wall
        wallCheck.x = position.x;
        wallCheck.y = -WORLD_HEIGHT / 2f;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, SPEED_MAX*(1f - dist/range), 0);
        }
        
        //top wall
        wallCheck.y = WORLD_HEIGHT / 2f;
        
        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, -SPEED_MAX*(1f - dist/range), 0);
        }
        
        return result;
    }

    public boolean isActive()
    {
        return active;
    }
}
