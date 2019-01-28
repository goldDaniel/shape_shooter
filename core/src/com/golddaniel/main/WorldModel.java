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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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

    ExtendViewport viewport;
    PerspectiveCamera cam;

    Vector3 cursor = new Vector3();

    float sleepTimer = 0;


    public boolean editMode = false;


    public WorldModel(float width, float height)
    {
        float vWidth;
        float vHeight;
        if(SharedLibraryLoader.isAndroid)
        {
            vWidth = 1080;
            vHeight = 1920;
        }
        else
        {
            vWidth = 1920;
            vHeight = 1080;
        }
        cam = new PerspectiveCamera(67, vWidth, vHeight);
        viewport = new ExtendViewport(vWidth, vHeight, cam);

        viewport.apply();

        cursor.set(Gdx.input.getX(), Gdx.input.getY(), 0);
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
        
        particles = new Array<Particle>();
        
        particlePool = new Pool<Particle>(4096) {
            protected Particle newObject()
            {
                return new Particle(new Vector3(-1000,-1000,-1000),
                                    new Vector3(0,0,0),
                                    new Vector3(0,0,0),
                            0, null, null);
            }
        };
        
        bulletPool = new Pool<Bullet>(512) {
            protected Bullet newObject()
            {
                return new Bullet(Vector3.Zero, 0, 0, null);
            }
        };

    }

    public void update(float delta)
    {
        sleepTimer -= delta;
        if(sleepTimer <= 0) sleepTimer = 0;
        else                return;

        isUpdating = true;



        //should move input into another module////////////////////
        if(SharedLibraryLoader.isAndroid)
        {
            float sensitivity = WORLD_HEIGHT / Gdx.graphics.getHeight();

            cursor.set(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY(), 0);

            //sensitivity
            cursor.scl(sensitivity);

            player.position.add(cursor);
        }

        ///////////////////////////////////////////////////////////

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


        float subDivision = 1f/8f;

        Gdx.input.setInputProcessor(null);

        Vector3 target = new Vector3(player.position);

        if (player.position.x < -WORLD_WIDTH + WORLD_WIDTH * subDivision)target.x = -WORLD_WIDTH * subDivision;
        if (player.position.x > WORLD_WIDTH - WORLD_WIDTH * subDivision) target.x = WORLD_WIDTH * subDivision;

        if (player.position.y < -WORLD_HEIGHT * subDivision) target.y = -WORLD_HEIGHT * subDivision;
        if (player.position.y > WORLD_HEIGHT * subDivision) target.y = WORLD_HEIGHT * subDivision;

        //maintain our rotation around Z axis before lookAt
        cam.up.set(0f, 1f, 0f);

        cam.position.x = MathUtils.lerp(cam.position.x, target.x, 0.05f);
        cam.position.y = MathUtils.lerp(cam.position.y, target.y, 0.05f);
        cam.position.z = MathUtils.lerp(cam.position.z, 7f, 0.05f);

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        cam.update();

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

    public Camera getCamera() { return cam; }

    public void sleep(float seconds)
    {
        sleepTimer += seconds;
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
