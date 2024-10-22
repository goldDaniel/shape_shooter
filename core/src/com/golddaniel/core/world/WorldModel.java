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
package com.golddaniel.core.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.core.AudioSystem;
import com.golddaniel.core.springmass.GridUpdater;
import com.golddaniel.core.springmass.SpringMassGrid;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.TextParticle;
import com.golddaniel.utils.QuadTree;


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

    private GridUpdater g;

    private boolean isUpdating;


    private float remainingTime;
    private float elapsedTime = 0;

    public float TIMESCALE = 1f;

    private int scoreMultiplier = 1;
    private int score = 0;

    final float RESPAWN_TIME = 2f;
    private float respawnTimer = 0f;


    public WorldModel(float width, float height, ArrayMap<Integer, Array<Entity>> toSpawn, float levelTime)
    {
        this.toSpawn = toSpawn;
        this.remainingTime = levelTime;

        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;

        entities = new Array<Entity>();
        toRemove = new Array<Entity>();
        toAdd = new Array<Entity>();

        textParticles = new Array<TextParticle>(128);

        particles = new Array<Particle>();

        //arbitrary large number
        particlePool = new Pool<Particle>(8192 * 8)
        {
            protected Particle newObject()
            {
                return new Particle(new Vector2(-1000, -1000),
                        new Vector2(0, 0),
                        new Vector2(0, 0),
                        0, new Color(), new Color());
            }
        };

        bulletPool = new Pool<Bullet>(2048)
        {
            protected Bullet newObject()
            {
                return new Bullet(Vector2.Zero, 0, 0);
            }
        };

        addEntity(player = new Player());
    }


    public void update(float delta)
    {
        delta *= TIMESCALE;

        if(remainingTime <= 0) entities.clear();

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

        g.update();

        if(!player.isAlive() && remainingTime > 0)
        {
            player.update(this, delta);
        }

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
    }

    public float getRemainingTime()
    {
        return remainingTime;
    }

    public int getScore()
    {
        return score;
    }

    public int getScoreMultiplier()
    {
        return scoreMultiplier;
    }

    public void incrementMultiplier()
    {
        scoreMultiplier++;
    }

    public void addScore(int score)
    {
        this.score += score * scoreMultiplier;
    }

    public void applyRadialForce(Vector2 pos, float force, float radius)
    {
        g.applyRadialForce(pos, force, radius);
    }

    public void applyRadialForce(Vector2 pos, float force, float radius, Color c)
    {
        g.applyRadialForce(pos, force, radius, c);
    }

    public void createBullet(Vector2 pos, float speed, float dir)
    {
        Bullet b = bulletPool.obtain();
        b.init(pos, speed, dir);
        addEntity(b);
    }

    public void createTextParticle(int num, Vector2 pos)
    {
        TextParticle p = new TextParticle("x" + num, pos);
        textParticles.add(p);
    }

    public void createParticle(Vector2 pos, Vector2 vel, Vector2 dim, float lifespan, Color startColor, Color endColor)
    {
        Particle p = particlePool.obtain();
        p.init(pos, vel, dim, lifespan, startColor, endColor);
        particles.add(p);
    }

    public void createMultipliers(Vector2 pos, int count)
    {
        Vector2 vel = new Vector2();
        for (int i = 0; i < count; i++)
        {
            float angle = ((float) (i) / (float) count) * 360f;
            float speed = MathUtils.random(0.5f, 1.5f);

            vel.x = MathUtils.cos(angle) * speed;
            vel.y = MathUtils.sin(angle) * speed;

            Multiplier m = new Multiplier(pos.cpy(), vel.cpy());
            addEntity(m);
        }
    }

    public void killAllEntities()
    {
        for (Entity e : entities)
        {
            e.kill();
        }
    }

    public Array<Entity> getAllEntities()
    {
        return entities;
    }

    public Array<Particle> getAllParticles()
    {
        return particles;
    }

    public Array<TextParticle> getTextParticles() { return textParticles; }

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
    }

    public Player getPlayer()
    {
        return player;
    }

    public SpringMassGrid getGrid()
    {
        return g.getGrid();
    }

    public void setGrid(SpringMassGrid grid)
    {
        g  = new GridUpdater(grid);
    }
}
