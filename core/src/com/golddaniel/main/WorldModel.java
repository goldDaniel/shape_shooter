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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.RapidFire;

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
    
    Array<Particle> particles;
    Pool<Particle> particlePool;
    
    Player player;
    
    PhysicsGrid g;
    
    boolean isUpdating;
    
    /*
    // I really dont want score in here, but it will do for nows
    */
    int score;
    
    public WorldModel()
    {
        this(Globals.WIDTH, Globals.HEIGHT);
    }
    
    public WorldModel(float width, float height)
    {
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        
        entities = new Array<Entity>();
        toRemove = new Array<Entity>();
        
        particles = new Array<Particle>();
        
        particlePool = new Pool<Particle>(4096) {
            @Override
            protected Particle newObject()
            {
                return new Particle(null,0, 0, null, null, 0, null);
            }
        };
        
        
    }
    
    public void update(float delta)
    {
        if(Gdx.input.isKeyJustPressed(Keys.SPACE))
        {
            Vector2 pos = new Vector2(
                    MathUtils.random(WORLD_WIDTH/4f, WORLD_WIDTH*3f/4f), 
                    MathUtils.random(WORLD_HEIGHT/4f, WORLD_HEIGHT*3f/4f));
            
            addEntity(new RapidFire(pos));
        }
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
        
        if(!entities.contains(player, true))
        {
            player = null;
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
    }
  
    public void addEntity(Entity e)
    {
        if(!entities.contains(e, true))
        {
            entities.add(e);
            
            if(e instanceof Player)
            {
                player = (Player)e;
            }
            else if(e instanceof PhysicsGrid)
            {
                g = (PhysicsGrid)e;
            }
        }
    }
    
    /*/////////////////////////////////////////////////////////////////////////
        maybe change?
        currently null checking as this is how we use grid, but 
        sometimes we dont have a grid, when we are testing
    */    
    public void applyRadialForce(Vector2 pos, float force, float radius)
    {   
        if(g != null)
            g.applyRadialForce(pos, force, radius);
    }
    //////////////////////////////////////////////////////////////////////////////
    
    public void createBullet(Vector2 pos, float dir, Bullet.TYPE type)
    {
        addEntity(new Bullet(pos, dir, type));
    }
    
    public void createParticle(Vector2 pos, float dir, float lifespan, float speed, Color startColor, Color endColor, Particle.TYPE type)
    {
        Particle p = particlePool.obtain();
        p.init(pos, dir, lifespan, startColor, endColor, speed, type);
        particles.add(p);
    }
    
    public void killAllEntities()
    {
        for(Entity e : entities)
        {
            if(!(e instanceof PhysicsGrid)) e.kill();
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
    
    public void addToScore(int score)
    {
        assert(score > 0);
        this.score += score;
    }
    
    public int getScore()
    {
        return score;
    }
}
