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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.Boid;

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
    
    Player player;
    
    PhysicsGrid g;
    
    boolean isUpdating;
    
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
    }
    
    public void update(float delta)
    {
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
    public void applyDIrectionalGridForce(Vector2 pos, Vector2 force, float radius)
    {
        if(g != null)
            g.applyDirectionalForce(pos, force, radius);
    }
    
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
    
    public void createParticle(Vector2 pos, float dir, float lifespan, float speed, Color startColor, Color endColor)
    {
        addEntity(new Particle(pos, dir, lifespan, startColor, endColor, speed));
    }
    
    public void killAllEntities()
    {
        for(Entity e : entities)
        {
            if(e instanceof Boid ||
               e instanceof Bouncer) e.kill();
        }
    }
    
    public Array<Entity> getAllEntities()
    {
        return entities;
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
}
