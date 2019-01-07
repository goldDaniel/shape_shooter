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
import com.golddaniel.entities.BlackHole;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Cuber;

/**
 *
 * @author wrksttn
 */
public class CollisionSystem
{   
    public static void update(WorldModel model)
    {
        if(model == null) return;
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
           
            if(bouncer.isAlive() && model.getPlayer() != null)
            {
                Rectangle playerRect = model.getPlayer().getBoundingBox();
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
        
        for(Cuber cb : model.getEntityType(Cuber.class))
        {
            if(cb.isActive())
            {
                for(Bullet bullet : bullets)
                {
                    Rectangle bulletRect = bullet.getBoundingBox();

                    if(bullet.isAlive() && cb.isAlive())
                    {
                        if(bulletRect.overlaps(cb.getBoundingBox()))
                        {
                            cb.kill(model);
                            bullet.kill(model);
                        }
                    }
                }
            }
                if(model.getPlayer() != null)
                {
                    Rectangle p = model.getPlayer().getBoundingBox();
                    if(p.overlaps(cb.getBoundingBox()))
                    {
                        model.getPlayer().kill(model);
                    }
                }
        }
        for(BlackHole b : model.getEntityType(BlackHole.class))
        {
            if(!b.isActive()) continue;
            
            if(model.getPlayer() != null)
            {
                Rectangle p = model.getPlayer().getBoundingBox();
                if(p.overlaps(b.getBoundingBox()))
                {
                    model.getPlayer().kill(model);
                }
            }
            for(Bullet bullet : bullets)
            {
                Rectangle bulletRect = bullet.getBoundingBox();
                
                if(bullet.isAlive() && b.isAlive())
                {
                    if(bulletRect.overlaps(b.getBoundingBox()))
                    {
                        b.kill(model);
                        bullet.kill(model);
                    }
                }
            }
        }
    }
}
