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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.TextParticle;


/**
 * @author wrksttn
 */
public class WorldModel
{

    public final float WORLD_WIDTH;
    public final float WORLD_HEIGHT;

    private Array<Entity> entities;

    private ArrayMap<Integer, Array<Entity>> toSpawn;

    private Array<Entity> toRemove;
    private Array<Entity> toAdd;


    private Array<TextParticle> textParticles;

    //we create lots of particles, so lets create a pool
    private Array<Particle> particles;
    private Pool<Particle> particlePool;

    //we create lots of bullets, so lets create a pool
    private Pool<Bullet> bulletPool;

    private Player player;

    private PhysicsGrid g;

    private boolean isUpdating;

    private PerspectiveCamera cam;

    private float remainingTime;
    private float elapsedTime = 0;

    public boolean editMode = false;

    public float TIMESCALE = 1f;

    private int scoreMultiplier = 1;
    private int score = 0;

    final float RESPAWN_TIME = 2f;
    private float respawnTimer = 0f;

    public PerspectiveCamera getCamera()
    {
        return cam;
    }

    public WorldModel(float width, float height, ArrayMap<Integer, Array<Entity>> toSpawn, float levelTime)
    {
        this.toSpawn = toSpawn;
        this.remainingTime = levelTime;
        float vWidth = 1920;
        float vHeight = 1080;

        cam = new PerspectiveCamera(67, vWidth, vHeight);

        cam.position.x = 0;
        cam.position.y = 0;
        cam.position.z = 64f;

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        cam.near = 1f;
        cam.far = 5000f;

        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;

        entities = new Array<Entity>();
        toRemove = new Array<Entity>();
        toAdd = new Array<Entity>();

        textParticles = new Array<TextParticle>(128);

        particles = new Array<Particle>();

        //arbitrary large number
        particlePool = new Pool<Particle>(8192 * 2)
        {
            protected Particle newObject()
            {
                return new Particle(new Vector3(-1000, -1000, -1000),
                        new Vector3(0, 0, 0),
                        new Vector3(0, 0, 0),
                        0, null, null);
            }
        };

        bulletPool = new Pool<Bullet>(2048)
        {
            protected Bullet newObject()
            {
                return new Bullet(Vector3.Zero, 0, 0, null);
            }
        };
    }


    public void update(float delta)
    {
        delta *= TIMESCALE;

        isUpdating = true;

        elapsedTime += delta;
        remainingTime -= delta;

        entities.addAll(toAdd);
        toAdd.clear();
        if (toSpawn.containsKey((int) elapsedTime))
        {
            Array<Entity> e = toSpawn.get((int) elapsedTime);
            for (Entity en : e)
            {
                addEntity(en);
            }
            toSpawn.removeKey((int) elapsedTime);
        }

        for (Entity e : entities)
        {
            if (!e.isAlive())
            {
                e.dispose();
                toRemove.add(e);
                if (e instanceof Bullet)
                {
                    bulletPool.free((Bullet) e);
                }
            }
            else
            {
                e.update(this, delta);
            }
        }

        entities.removeAll(toRemove, true);
        toRemove.clear();

        for (Particle e : particles)
        {
            if (!e.isAlive())
            {
                particles.removeValue(e, true);
                particlePool.free(e);
            }
            else
            {
                e.update(this, delta);
            }
        }
        Array<TextParticle> toRemove = new Array<TextParticle>();
        for(TextParticle p : textParticles)
        {
            p.update(this, delta);
            if(!p.isAlive()) toRemove.add(p);
        }
        textParticles.removeAll(toRemove, true);

        g.update(delta);

        //CAMERA LOGIC///////////////////////////////////////////////////////////////////////////

        if (!editMode)
        {
            Vector3 target = new Vector3();
            if (player != null)
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

                target.z = 5.5f;
            }
            else
            {
                target.z = 16.5f;
            }
            cam.position.x = MathUtils.lerp(cam.position.x, target.x, 0.05f);
            cam.position.y = MathUtils.lerp(cam.position.y, target.y, 0.05f);
            cam.position.z = MathUtils.lerp(
                    cam.position.z,
                    target.z,
                    delta * 2f);

            cam.lookAt(cam.position.x, cam.position.y, 0f);

            //maintain our rotation around Z axis before lookAt, otherwise
            //we get weird rotation due to floating point error with lookAt
            cam.up.set(0f, 1f, 0f);
        }
        cam.update();
        /////////////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////////////////
        //WE RESPAWN THE PLAYER IN HERE////////////////////////////////////////////////////////////
        if (player == null)
        {
            respawnTimer += delta;

            int particles = 16;

            Vector3 velocity = new Vector3();
            Vector3 dim = new Vector3(0.75f, 0.025f, 0.025f);

            for (int i = 0; i < particles; i++)
            {
                float angle = (float) i / (float) particles * 360f;

                angle += MathUtils.random(-30f, 30f);

                velocity.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
                velocity.scl(MathUtils.random(2f, 4f));


                createParticle(
                        Vector3.Zero,
                        velocity,
                        dim,
                        MathUtils.random(0.4f, 0.6f),
                        Color.PURPLE,
                        Color.BLUE);

                velocity = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
                velocity.scl(MathUtils.random(3f, 5f));

                createParticle(
                        Vector3.Zero,
                        velocity,
                        dim,
                        MathUtils.random(0.4f, 0.6f),
                        Color.RED,
                        Color.YELLOW);
            }
            if (respawnTimer >= RESPAWN_TIME)
            {
                AudioSystem.playSound(AudioSystem.SoundEffect.RESPAWN);
                //RESPAWN PLAYER
                addEntity(new Player(null));
                applyRadialForce(new Vector3(), 2000f * delta, 2.25f);

                particles = 256;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float) i / (float) particles * 360f;

                    angle += MathUtils.random(-10f, 10f);

                    velocity.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);

                    velocity.scl(MathUtils.random(10f, 14f));
                    createParticle(
                            Vector3.Zero,
                            velocity,
                            dim.set(0.75f, 0.1f, 0.1f),
                            MathUtils.random(0.7f, 0.9f),
                            Color.MAGENTA,
                            Color.BLUE);

                    velocity.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);

                    velocity.scl(MathUtils.random(8f, 14f));
                    createParticle(
                            Vector3.Zero,
                            velocity,
                            dim,
                            MathUtils.random(0.7f, 0.9f),
                            Color.PURPLE,
                            Color.GREEN);
                }
            }
        }
        else
        {
            respawnTimer = 0;
        }
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        float audioLerp = (1f - respawnTimer / RESPAWN_TIME);
        AudioSystem.setMusicVolume(0.05f + 0.95f * (float) Math.pow(audioLerp, 4f));
        isUpdating = false;
    }


    public void addEntity(Entity e)
    {
        if (!entities.contains(e, true))
        {
            if (isUpdating)
            {
                toAdd.add(e);
            }
            else
            {
                entities.add(e);
            }
        }
        if (e instanceof Player) player = (Player) e;
    }

    public float getRemainingTime()
    {
        return remainingTime;
    }

    protected int getScore()
    {
        return score;
    }

    public int getScoreMultiplier()
    {
        return scoreMultiplier;
    }

    protected void incrementMultiplier()
    {
        scoreMultiplier++;
    }

    public void addScore(int score)
    {
        this.score += score * scoreMultiplier;
    }

    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }

    public void applyRadialForce(Vector3 pos, float force, float radius)
    {
        g.applyRadialForce(pos, force, radius);
    }

    public void applyRadialForce(Vector3 pos, float force, float radius, Color c)
    {
        g.applyRadialForce(pos, force, radius, c);
    }

    public void createBullet(Vector3 pos, float speed, float dir)
    {
        Bullet b = bulletPool.obtain();
        b.init(pos, speed, dir);
        addEntity(b);
    }

    public void createTextParticle(int num, Vector3 pos)
    {
        TextParticle p = new TextParticle(null, "x" + num, pos);
        textParticles.add(p);
    }

    public void createParticle(Vector3 pos, Vector3 vel, Vector3 dim, float lifespan, Color startColor, Color endColor)
    {
        Particle p = particlePool.obtain();
        p.init(pos.cpy(), vel.cpy(), dim.cpy(), lifespan, startColor, endColor);
        particles.add(p);
    }

    public void createMultipliers(Vector3 pos, int count)
    {
        Vector3 vel = new Vector3();
        for (int i = 0; i < count; i++)
        {
            float angle = ((float) (i) / (float) count) * 360f;
            float speed = MathUtils.random(0.5f, 1.5f);

            vel.x = MathUtils.cos(angle) * speed;
            vel.y = MathUtils.sin(angle) * speed;

            Multiplier m = new Multiplier(pos.cpy(), vel.cpy(), null);
            addEntity(m);
        }
    }

    public void killAllEntities()
    {
        for (Entity e : entities)
        {
            e.kill();
        }
        player = null;
    }

    protected Array<Entity> getAllEntities()
    {
        return entities;
    }

    protected Array<Particle> getAllParticles()
    {
        return particles;
    }

    protected Array<TextParticle> getTextParticles() { return textParticles; }

    public <T> Array<T> getEntityType(Class<T> type)
    {
        Array<T> result = new Array<T>();

        for (int i = 0; i < entities.size; i++)
        {
            T obj = (T) entities.get(i);
            if (obj.getClass() == type)
            {
                result.add(obj);
            }
        }
        return result;
    }

    public void dispose()
    {
        for (Entity e : entities)
        {
            e.dispose();
        }
        g.dispose();
    }

    public Player getPlayer()
    {
        return player;
    }

    public PhysicsGrid getGrid()
    {
        return g;
    }

    public void setGrid(PhysicsGrid g)
    {
        this.g = g;
    }
}
