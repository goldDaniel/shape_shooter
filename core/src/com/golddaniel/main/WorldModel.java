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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;

import java.awt.*;

/**
 *
 * @author wrksttn
 */
public class WorldModel
{

    private static enum WorldState
    {


    }


    public final float WORLD_WIDTH;
    public final float WORLD_HEIGHT;
    
    Array<Entity> entities;
    Array<Entity> toRemove;
    
    
    //we create lots of particles, so lets create a pool
    Array<Particle> particles;
    Pool<Particle> particlePool;
    
    //we create lots of bullets, so lets create a pool
    Pool<Bullet> bulletPool;
    
    Player player;
    
    PhysicsGrid g;
    
    boolean isUpdating;

    FitViewport viewport;
    PerspectiveCamera cam;

    Vector3 prevCursor = new Vector3();
    Vector3 cursor = new Vector3();

    public WorldModel(float width, float height)
    {
        cam = new PerspectiveCamera(45, 2f, 2f);
        viewport = new FitViewport(1080, 1920, cam);
        viewport.apply();

        cursor.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        cam.position.x = 0;
        cam.position.y = 0;
        cam.position.z = 1f;

        cam.up.x = 0f;
        cam.up.y = 1f;
        cam.up.z = 0f;

        cam.lookAt(0f, 0f, 0f);

        cam.near = 0.25f;
        cam.far = 5000f;

        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        
        entities = new Array<Entity>();
        toRemove = new Array<Entity>();
        
        particles = new Array<Particle>();
        
        particlePool = new Pool<Particle>(4096) {
            protected Particle newObject()
            {
                return new Particle(null,0, 0, null, null, 0);
            }
        };
        
        bulletPool = new Pool<Bullet>(512) {
            protected Bullet newObject()
            {
                return new Bullet(Vector3.Zero, 0, null);
            }
        };
    }
    
    public void update(float delta)
    {
        isUpdating = true;


        cam.position.x = player.position.x * -0.25f;
        cam.position.y = player.position.y * -0.125f;
        cam.up.set(0, 1, 0);
        cam.lookAt(0, 0, 0);

        cam.update();


        float sensitivity = WORLD_HEIGHT / Gdx.graphics.getHeight();

        cursor.set(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY(), 0);

        //sensitivity
        cursor.scl(sensitivity);

        player.position.add(cursor);


        for(Entity e : entities)
        {
            if(!e.isAlive())
            {
                e.dispose();
                toRemove.add(e);
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
                e.dispose();
                particles.removeValue(e, true);
            }
            else
            {
                e.update(this, delta);
            }
        }

        g.update(delta);

        isUpdating = false;
    }

    public void addEntity(Entity e)
    {
        if(!entities.contains(e, true))
        {
            entities.add(e);
        }
    }

    public void addEntity(Player p)
    {
        for(Entity e : entities)
        {
            if(e instanceof Player)
            {
                Gdx.app.log("ERROR", "ADDED MULTIPLE PLAYERS");
            }
        }
        player = p;
        entities.add(p);
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
    }
    //////////////////////////////////////////////////////////////////////////////
    
    public void createBullet(Vector3 pos, float dir, Bullet.TYPE type)
    {
        Bullet b = bulletPool.obtain();
        b.init(pos, dir, type);
        addEntity(b);
    }
    
    public void createParticle(Vector3 pos, float dir, float lifespan, float speed, Color startColor, Color endColor)
    {
        Particle p = particlePool.obtain();
        p.init(pos.cpy(), dir, lifespan, startColor, endColor, speed);
        particles.add(p);
    }
    
    public void killAllEntities()
    {
        for(Entity e : entities)
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

    public Vector3 getCursor() { return cursor.cpy(); }

}
