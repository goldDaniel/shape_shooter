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

import com.badlogic.gdx.assets.AssetManager;
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
import com.golddaniel.main.AudioSystem;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Boid extends Entity
{
    private static float SPEED_MAX = 0.85f;

    private static Array<Boid> boids = new Array<Boid>();

    private static TextureRegion circleTex;
    private static TextureRegion tex;

    private Vector3 velocity;
    private Vector3 acceleration;

    private Color color;

    private float width;
    private float height;

    private int health = 5;

    private float activeTimer = 2f;
    
    public Boid(Vector3 position, AssetManager assets)
    {
        super(assets);
        if(tex == null)
        {
            tex = new TextureRegion(assets.get(("geometric/player.png"), Texture.class));
        }
        if(circleTex == null)
        {
            circleTex = new TextureRegion(assets.get("circle.png", Texture.class));
        }

        this.position = new Vector3(position);

        float angle = MathUtils.random(MathUtils.PI*2);
        acceleration = new Vector3();
        velocity = new Vector3(MathUtils.cos(angle), MathUtils.sin(angle), 0).scl(SPEED_MAX);

        width = 0.25f;
        height = 0.25f;
        color = Color.CYAN.cpy();

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
        float range = 5f;
        
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
        float range = 3.5f;
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
        
        float range = 0.75f;
        
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

    private Vector3 calculateBoundary(float WORLD_WIDTH, float WORLD_HEIGHT)
    {
        Vector3 result = new Vector3();

        float range = 4f;

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


    public void update(float delta)
    {
        Vector3 separation = separation();
        Vector3 allignment = allignment();
        Vector3 cohesion = cohesion();

        acceleration.add(separation);
        acceleration.add(allignment);
        acceleration.add(cohesion);

        acceleration.limit(SPEED_MAX / 32f * delta);

        velocity.add(acceleration);
        velocity.limit(SPEED_MAX * delta);

        position.add(velocity);

        acceleration.set(0, 0, 0);

        Vector3 pos = position.cpy();
        pos.z = -0.1f;
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        if(activeTimer <= 0)
        {
            borderCheck(model);

            Vector3 separation = separation();
            Vector3 allignment = allignment();
            Vector3 cohesion = cohesion();
            Vector3 boundary = calculateBoundary(model.WORLD_WIDTH, model.WORLD_HEIGHT);
            Vector3 seek = new Vector3();

            float range = 7f;
            if (model.getEntityType(Player.class).size > 0)
            {

                Vector3 target = model.getEntityType(Player.class).first().position;
                float dist = target.dst(position);
                if (dist < range)
                {
                    seek.set(seek(target));
                }
            }

            acceleration.add(separation);
            acceleration.add(allignment);
            acceleration.add(cohesion);
            acceleration.add(boundary);
            acceleration.add(seek);

            acceleration.limit(SPEED_MAX / 32f * delta);

            velocity.add(acceleration);
            velocity.limit(SPEED_MAX * delta);

            position.add(velocity);

            acceleration.set(0, 0, 0);

            Vector3 pos = position.cpy();
            pos.z = -0.1f;
            model.applyRadialForce(position, 150f * delta, width);
        }
        else
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
                                Color.WHITE,
                                color);
            }
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

        Color c = color.cpy();
        if(activeTimer > 0)
        {
            c.a = 0.5f;
        }
        s.setColor(c);
        Vector2 dir = new Vector2(velocity.x, velocity.y);

        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1, 1,
                dir.angle());

        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer <= 0) health--;
        if(health <= 0)
        {
            int particles = 32;
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;

                angle += MathUtils.random(-1.5f, 1.5f);

                Vector3 dim = new Vector3(0.5f, 0.075f, 0.075f);

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

            model.createMultipliers(position, 3);
            model.addScore(1);
            model.applyRadialForce(
                          pos,
                    22,
                    (width) * 4f, Color.CYAN.cpy().fromHsv(210f, 0.65f, 0.8f));

            boids.removeValue(this, true);

            AudioSystem.playSound(AudioSystem.SoundEffect.ENEMY_DEATH);

            isAlive = false;
        }
    }

    public boolean isActive()
    {
        return activeTimer <= 0;
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
}
