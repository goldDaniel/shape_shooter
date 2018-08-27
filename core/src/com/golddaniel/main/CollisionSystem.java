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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.Boid;
import gold.daniel.level.Level;

/**
 *
 * @author wrksttn
 */
public class CollisionSystem
{   
    public void update(Level level)
    {
        WorldModel model = level.getModel();
        
        Array<Bullet> bullets = model.getEntityType(Bullet.class);
        Array<Bouncer> bouncers = model.getEntityType(Bouncer.class);
        Array<Boid> boids = model.getEntityType(Boid.class);
        
        for(Bouncer bouncer : bouncers)
        {
            if(!bouncer.isActive())
            {
                continue;
            }
            Rectangle bouncerRect = bouncer.getBoundingBox();
            
            for(Bullet bullet : bullets)
            {
                Rectangle bulletRect = bullet.getBoundingBox();
                
                if(bullet.isAlive() && bouncer.isAlive())
                {
                    if(bulletRect.overlaps(bouncerRect))
                    {
                        bouncer.kill(model);
                        bullet.kill(model);
                    }
                }
            }
           
            if(bouncer.isAlive() && model.getEntityType(Player.class).size > 0)
            {
                Rectangle playerRect = model.player.getBoundingBox();
                if(playerRect.overlaps(bouncerRect) || bouncerRect.overlaps(playerRect))
                {
                    model.player.kill(model);
                }
            }
        }
        for(Boid boid : boids)
        {
            if(!boid.isActive())
            {
                continue;
            }
            Rectangle bouncerRect = boid.getBoundingBox();
            
            for(Bullet bullet : bullets)
            {
                Rectangle bulletRect = bullet.getBoundingBox();
                
                if(bullet.isAlive() && boid.isAlive())
                {
                    if(bulletRect.overlaps(bouncerRect))
                    {
                        boid.kill(model);
                        bullet.kill(model);
                    }
                }
            }
           
            if(boid.isAlive() && model.getEntityType(Player.class).size > 0)
            {
                Rectangle playerRect = model.player.getBoundingBox();
                if(playerRect.overlaps(bouncerRect) || bouncerRect.overlaps(playerRect))
                {
                    model.player.kill(model);
                }
            }
        }
    }
}
