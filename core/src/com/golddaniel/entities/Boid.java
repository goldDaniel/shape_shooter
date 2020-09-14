package com.golddaniel.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.core.AudioSystem;
import com.golddaniel.core.world.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Boid extends Entity
{
    private static float SPEED_MAX = 0.9f;

    private static TextureRegion tex;

    private Array<Boid> nearbyBoids;

    private Vector2 velocity;
    private Vector2 acceleration;

    private Vector2 cohesion;
    private Vector2 alignment;
    private Vector2 separation;

    private Color color;

    private float width;
    private float height;

    private int health = 5;

    private float activeTimer = 2f;

    private float widthAngle = 0;

    private Vector2 scratch = new Vector2();

    Rectangle boundingBox = new Rectangle();

    public static void loadTextures(AssetManager assets)
    {
        if(tex == null)
            tex = new TextureRegion(assets.get("geometric/boid.png", Texture.class));
    }

    public Boid(Vector2 position)
    {
        super();

        this.position = new Vector2(position);

        float angle = MathUtils.random(MathUtils.PI*2);
        acceleration = new Vector2();
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).scl(SPEED_MAX);

        cohesion = new Vector2();
        alignment = new Vector2();
        separation = new Vector2();

        width = 0.25f;
        height = 0.25f;
        color = Color.CYAN.cpy();

        nearbyBoids = new Array<Boid>();
    }
    
    /**
     *  Sets direction vector based on position of all boids in range
     *
     */
    private void cohesion()
    {
        int count = 0;
        float range = 5f;
        
        cohesion.setZero();
        for(Boid b : nearbyBoids)
        {
            float dist = position.dst(b.position);
            
            /**
             * comment out if we currently want average of all boids
             * to pull them together 
             */
            if(dist > 0 && dist < range)
            {
                cohesion.add(b.position);
                count++;
            }
        }
        if(count > 0)
        {
            cohesion.scl(1f/(float)count);
            cohesion.limit(SPEED_MAX);
        }
    }
    
    /**
     *  Sets direction vector based on velocity of all boids in range
     *
     */
    private void allignment()
    {
        float range = 3.5f;
        int count = 0;
        
        Vector2 sum = new Vector2();
        for(Boid b : nearbyBoids)
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
            
            alignment.set(sum.sub(velocity));
            alignment.limit(SPEED_MAX);
        }
    }
    
    /**
     *  sets direction vector based on separation of all boids in range.
     * 
     * if a boid is in range, add a vector to the sum and give the opposite direction
     *
     */
    private void separation()
    {
        int count = 0;
        
        float range = 0.75f;
        
        Vector2 sum = new Vector2();
        for(Boid b : nearbyBoids)
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
            
            separation.set(sum.sub(velocity));
            separation.limit(SPEED_MAX);
        }
    }
    
    private Vector2 seek(Vector2 target)
    {
        Vector2 desiredVelocity = target.cpy().sub(position);
        desiredVelocity.setLength(SPEED_MAX);
        
        return desiredVelocity.sub(velocity).setLength(SPEED_MAX);
    }

    private Vector2 calculateBoundary(float WORLD_WIDTH, float WORLD_HEIGHT)
    {
        Vector2 result = new Vector2();

        float range = 4f;

        Vector2 wallCheck = new Vector2();

        //left wall
        wallCheck.x = -WORLD_WIDTH/2f;
        wallCheck.y = position.y;
        float dist = position.dst(wallCheck);

        if(dist < range)
        {
            result.add(SPEED_MAX*(1f - dist/range), 0);
        }

        //right wall
        wallCheck.x = WORLD_WIDTH / 2f;

        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(-SPEED_MAX*(1f - dist/range), 0);
        }

        //bottom wall
        wallCheck.x = position.x;
        wallCheck.y = -WORLD_HEIGHT / 2f;

        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, SPEED_MAX*(1f - dist/range));
        }

        //top wall
        wallCheck.y = WORLD_HEIGHT / 2f;

        dist = position.dst(wallCheck);
        if(dist < range)
        {
            result.add(0, -SPEED_MAX*(1f - dist/range));
        }

        return result;
    }


    @Override
    public void update(WorldModel model, float delta)
    {
        if(activeTimer <= 0)
        {
            nearbyBoids = model.getNearbyBoids(nearbyBoids, this);

            width = 0.1f + 0.4f * MathUtils.sin(widthAngle)*MathUtils.sin(widthAngle);
            widthAngle += MathUtils.PI * delta;

            Vector2 boundary = calculateBoundary(model.WORLD_WIDTH, model.WORLD_HEIGHT);
            boundary.scl(3f);
            Vector2 seek = new Vector2();

            float range = 10f;
            if (model.getEntityType(Player.class).size > 0)
            {

                Vector2 target = model.getEntityType(Player.class).first().position;
                float dist = target.dst(position);
                if (dist < range)
                {
                    seek.set(seek(target));
                }
            }
            seek.scl(2f);

            separation();
            allignment();
            cohesion();

            acceleration.add(separation);
            acceleration.add(alignment);
            acceleration.add(cohesion);
            acceleration.add(boundary);
            acceleration.add(seek);

            acceleration.limit(SPEED_MAX / 32f * delta);

            velocity.add(acceleration);
            velocity.limit(SPEED_MAX * delta);

            position.add(velocity);

            acceleration.set(0, 0);

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
        else
        {
            Vector2 veloctiy = new Vector2();
            Vector2 dim = new Vector2(0.35f, 0.1f);
            activeTimer -= delta;
            for(int i = 0; i < 6; i++)
            {
                float angle = MathUtils.PI * activeTimer * (i + 1);
                float speed = MathUtils.random(12f, 14f);
                speed *= 0.5f*activeTimer;

                model.createParticle(
                                position,
                                veloctiy.set(MathUtils.cos(angle) * speed,
                                             MathUtils.sin(angle) * speed),
                                dim,
                                MathUtils.random(0.1f, 0.25f),
                                Color.WHITE,
                                color);
            }
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
        scratch.set(velocity.x, velocity.y);

        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1, 1,
                scratch.angle());

        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer <= 0) health--;
        if(health <= 0)
        {
            Vector2 dim = new Vector2(0.5f, 0.075f);
            Vector2 velocity = new Vector2();
            int particles = 256;
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;

                float speed = MathUtils.random(14f, 22f);
                model.createParticle(
                    position,
                    velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                    dim,
                    MathUtils.random(0.1f, 0.5f),
                    Color.CYAN,
                    Color.WHITE);

                speed = MathUtils.random(5f, 8f);

                model.createParticle(
                        position,
                        velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                        dim,
                        MathUtils.random(0.1f, 0.5f),
                        Color.FOREST,
                        Color.WHITE);
            }

            Vector2 pos = position.cpy();

            model.createMultipliers(position, 3);
            model.addScore(5);
            model.applyRadialForce(
                            pos,
                            32,
                            1.5f,
                            Color.CYAN.cpy().fromHsv(210f, 0.65f, 1f));


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
        boundingBox.set(position.x - 0.5f / 2f,
                             position.y - 0.5f / 2f,
                                0.5f, 0.5f);
        return boundingBox;
    }

    @Override
    public void dispose()
    {

    }
}
