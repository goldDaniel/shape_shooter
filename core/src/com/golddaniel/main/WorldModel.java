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
package com.golddaniel.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;

/**
 *
 * @author wrksttn
 */
public class WorldModel
{

    public final float WORLD_WIDTH;
    public final float WORLD_HEIGHT;

    Array<Entity> entities;

    ArrayMap<Integer, Array<Entity>> toSpawn;

    Array<Entity> toRemove;
    Array<Entity> toAdd;


    //we create lots of particles, so lets create a pool
    Array<Particle> particles;
    Pool<Particle> particlePool;
    
    //we create lots of bullets, so lets create a pool
    Pool<Bullet> bulletPool;

    Player player;
    
    PhysicsGrid g;
    
    boolean isUpdating;

    ExtendViewport viewport;
    PerspectiveCamera cam;

    float remainingTime;
    float elapsedTime = 0;

    public boolean editMode = false;

    public float TIMESCALE = 1f;

    int scoreMultiplier = 1;
    int score = 0;

    float respawnTimer = 0f;

    public Camera getCamera()
    {
        return cam;
    }

    public WorldModel(float width, float height, ArrayMap<Integer, Array<Entity>> toSpawn, float levelTime)
    {
        this.toSpawn = toSpawn;
        this.remainingTime = levelTime;
        float vWidth  = 1920;
        float vHeight = 1080;

        cam = new PerspectiveCamera(67, vWidth, vHeight);
        viewport = new ExtendViewport(vWidth, vHeight, cam);
        viewport.apply();

        cam.position.x = 0;
        cam.position.y = 0;
        cam.position.z = 30f;

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        cam.near = 1f;
        cam.far = 5000f;

        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;

        entities = new Array<Entity>();
        toRemove = new Array<Entity>();
        toAdd = new Array<Entity>();

        particles = new Array<Particle>();

        //arbitrary large number
        particlePool = new Pool<Particle>(8192*2) {
            protected Particle newObject()
            {
                return new Particle(new Vector3(-1000,-1000,-1000),
                                    new Vector3(0,0,0),
                                    new Vector3(0,0,0),
                            0, null, null);
            }
        };

        bulletPool = new Pool<Bullet>(1024)
        {
            protected Bullet newObject()
            {
                return new Bullet(Vector3.Zero, 0, 0, null, null);
            }
        };
    }

    public float getElapsedTime()
    {
        return elapsedTime;
    }
    public float getRemainingTime() { return remainingTime; }
    public int getScore() { return score; };
    public int getScoreMultiplier() { return scoreMultiplier; }
    public void incrementMultiplier()
    {
        scoreMultiplier++;
    }

    public void update(float delta)
    {
        delta *= TIMESCALE;
        isUpdating = true;

        elapsedTime += delta;
        remainingTime -= delta;

        entities.addAll(toAdd);
        toAdd.clear();
        if(toSpawn.containsKey((int)elapsedTime))
        {
            Array<Entity> e = toSpawn.get((int)elapsedTime);
            for(Entity en : e)
            {
                addEntity(en);
            }
            toSpawn.removeKey((int)elapsedTime);
        }

        for(Entity e : entities)
        {
            if(!e.isAlive())
            {
                e.dispose();
                toRemove.add(e);
                if(e instanceof  Bullet)     bulletPool.free((Bullet)e);
            }
            else
            {
                e.update(this, delta);
            }
        }

        entities.removeAll(toRemove, true);
        toRemove.clear();

        for(Particle e : particles)
        {
            if(!e.isAlive())
            {
                particles.removeValue(e, true);
                particlePool.free(e);
            }
            else
            {
                e.update(this, delta);
            }
        }

        g.update(delta);

        Vector3 target = new Vector3();
        if(player != null)
        {
            target.set(player.position);

            if (player.position.x < -WORLD_WIDTH / 2f)
            {
                target.x = -WORLD_WIDTH / 2f;
            }
            if (player.position.x > WORLD_WIDTH / 2f)
            {
                target.x = WORLD_WIDTH / 2f;
            }

            if (player.position.y < -WORLD_HEIGHT / 2f)
            {
                target.y = -WORLD_HEIGHT / 2f;
            }
            if (player.position.y > WORLD_HEIGHT / 2f)
            {
                target.y = WORLD_HEIGHT / 2f;
            }
        }
        if(!editMode)
        {
            //maintain our rotation around Z axis before lookAt
            cam.up.set(0f, 1f, 0f);

            cam.position.x = MathUtils.lerp(cam.position.x, target.x, 0.05f);
            cam.position.y = MathUtils.lerp(cam.position.y, target.y, 0.05f);
            cam.position.z = MathUtils.lerp(
                                cam.position.z,
                        (abs(cam.position.x) + WORLD_WIDTH + WORLD_HEIGHT + abs(cam.position.y)) / 3.5f,
                       delta*2f);

            cam.lookAt(cam.position.x, cam.position.y, 0f);
        }
        cam.update();


        float respawnTime = 1.5f;
        if(player == null)
        {
            respawnTimer += delta;

            int particles = 8;
            for(int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles * 360f;

                angle += MathUtils.random(-30f, 30f);

                Vector3 velocity = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
                velocity.scl(MathUtils.random(2f, 4f));

                createParticle(
                        Vector3.Zero,
                        velocity,
                        new Vector3(0.5f, 0.01f, 0.01f),
                        MathUtils.random(0.25f, 0.4f),
                        Color.MAGENTA,
                        Color.CYAN);

                velocity = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
                velocity.scl(MathUtils.random(3f, 5f));

                createParticle(
                        Vector3.Zero,
                        velocity,
                        new Vector3(0.5f, 0.01f, 0.01f),
                        MathUtils.random(0.25f, 0.4f),
                        Color.CYAN,
                        Color.YELLOW);
            }
            if(respawnTimer >= respawnTime)
            {
                AudioSystem.playSound(AudioSystem.SoundEffect.RESPAWN);
                //WE RESPAWN THE PLAYER IN HERE
                ///////////////////////////////////////////////////////////////////////
                addEntity(new Player(null));
                applyRadialForce(new Vector3(), 2000f*delta, 2.25f);

                particles = 256;
                for(int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles * 360f;

                    angle += MathUtils.random(-10f, 10f);

                    Vector3 velocity = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);

                    velocity.scl(MathUtils.random(10f, 14f));
                    createParticle(
                            Vector3.Zero,
                            velocity,
                            new Vector3(0.75f, 0.02f, 0.02f),
                            MathUtils.random(0.7f, 0.9f),
                            Color.MAGENTA,
                            Color.CYAN);

                    velocity = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);

                    velocity.scl(MathUtils.random(8f, 14f));
                    createParticle(
                            Vector3.Zero,
                            velocity,
                            new Vector3(0.75f, 0.02f, 0.02f),
                            MathUtils.random(0.7f, 0.9f),
                            Color.CYAN,
                            Color.WHITE);
                }
                //////////////////////////////////////////////////////////////////////////////////
            }
        }
        else
        {
            respawnTimer = 0;
        }
        float audioLerp = (1f - respawnTimer/respawnTime);
        AudioSystem.setMusicVolume(0.1f +  0.9f * (float)Math.pow(audioLerp, 4f));

        isUpdating = false;
    }

    public void addScore(int score)
    {
        this.score += score*scoreMultiplier;
    }

    public void addEntity(Entity e)
    {
        if(!entities.contains(e, true))
        {
            if(isUpdating)
            {
                toAdd.add(e);
            }
            else
            {
                entities.add(e);
            }
        }
        if(e instanceof Player) player = (Player)e;
    }

    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }

    /*/////////////////////////////////////////////////////////////////////////
        maybe change?
        currently null checking as this is how we use grid, but
        sometimes we dont have a grid, when we are testing
    */
    public void applyRadialForce(Vector3 pos, float force, float radius)
    {
        if(g != null)
            g.applyRadialForce(pos, force, radius);
        else
            Gdx.app.log("WORLD-MODEL", "NO GRID TO APPLY FORCE TO");
    }

    public void applyRadialForce(Vector3 pos, float force, float radius, Color c)
    {
        if(g != null)
            g.applyRadialForce(pos, force, radius, c);
        else
            Gdx.app.log("WORLD-MODEL", "NO GRID TO APPLY FORCE TO");
    }
    //////////////////////////////////////////////////////////////////////////////

    public void createBullet(Vector3 pos, float speed, float dir, Bullet.TYPE type)
    {
        Bullet b = bulletPool.obtain();
        b.init(pos, speed, dir, type);
        addEntity(b);
    }

    public void createParticle(Vector3 pos, Vector3 vel, Vector3 dim, float lifespan, Color startColor, Color endColor)
    {
        Particle p = particlePool.obtain();
        p.init(pos.cpy(), vel.cpy(), dim.cpy(), lifespan, startColor, endColor);
        particles.add(p);
    }

    public void createMultipliers(Vector3 pos, int count)
    {
        for(int i = 0; i < count; i++)
        {

            float angle = ((float)(i)/(float)count) * 360f;
            float speed = MathUtils.random(0.5f, 1.5f);

            Vector3 vel = new Vector3();
            vel.x = MathUtils.cos(angle) * speed;
            vel.y = MathUtils.sin(angle) * speed;

            Multiplier m = new Multiplier(pos.cpy(), vel, null);
            addEntity(m);
        }
    }

    public void killAllEntities()
    {
        for(Entity e : entities)
        {
            e.kill();
        }
        player = null;
    }

    public Array<Entity> getAllEntities()
    {
        return entities;
    }

    public Array<Particle> getAllParticles()
    {
        return particles;
    }

    public <T> Array<T> getEntityType(Class<T> type)
    {
        Array<T> result = new Array<T>();

        for(int i = 0; i < entities.size; i++)
        {
            T obj = (T)entities.get(i);
            if(obj.getClass() == type)
            {
                result.add(obj);
            }
        }
        return result;
    }

    public void dispose()
    {
        for(Entity e : entities)
        {
            e.dispose();
        }
    }

    public Player getPlayer()
    {
        return player;
    }

    public PhysicsGrid getGrid()
    {
        return g;
    }
    public void setGrid(PhysicsGrid g) { this.g = g; }
}
