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
package com.golddaniel.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.main.Globals;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class BlackHole extends Entity
{
    boolean isActive;
    float activeTimer;
 
    private static TextureRegion edge = new TextureRegion(new Texture("geometric/dashedCircle.png"));
    
    private static ShapeRenderer sh = new ShapeRenderer();
    
    final int MAX_HEALTH = 10;
    float health;
    
    float MAX_RADIUS = 5f;
    float radius;
    
    Color color;
    
    float dirTimer = 0;
 
    float hue = -90f;
    
    public BlackHole(Vector3 pos)
    {
        isActive = false;
        this.position = pos;
        isAlive = true;
        this.health = MAX_HEALTH/2;
        
        radius = MAX_RADIUS/8f + MAX_RADIUS*7f/8f * health/(float)MAX_HEALTH;
        
        color = Color.CYAN.cpy();
    }


    @Override
    public void update(WorldModel world, float delta)
    {
        if(!isActive)
        {
            activeTimer += delta;
            if(activeTimer > 2)
            {
                isActive = true;
            }
            return;
        }
        dirTimer += delta;
        
        hue += 90f*delta;
        
        if(health < MAX_HEALTH)
        {
            health += 6*delta;
        }
        radius = MAX_RADIUS / 8f + MAX_RADIUS* 7f / 8f * health / (float)MAX_HEALTH;
        
        float force = 10f * delta * (1f + MathUtils.sinDeg(hue));

        world.applyRadialForce(
                position,
                force,
                radius);
        
        float range = radius * 2 * (1f + MathUtils.sinDeg(hue));
        Array<Bullet> bullets = world.getEntityType(Bullet.class);
        for(Bullet b : bullets)
        {
            float dist = b.position.dst(position);
            if(dist < range)
            {   
                Vector3 steering = 
                        b.position.cpy().sub(position).setLength(b.speed/2f);
                Vector2 dir = new Vector2(steering.x, steering.y);

                b.dir = MathUtils.lerpAngleDeg(
                                        b.dir, 
                                        dir.angle(),
                                        -2.5f*delta*(1f - dist/range));
            }
        }
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    @Override
    public void draw(SpriteBatch s)
    {
        s.setColor(Color.WHITE.cpy().fromHsv(hue, 1f, 1f));
        s.draw(
                edge, 
                position.x - radius / 2f,
                position.y - radius / 2f,
                radius / 2f, radius / 2f,
                radius, radius,
                0.5f + MathUtils.sinDeg(hue),
                0.5f + MathUtils.sinDeg(hue),
                hue);

        s.setColor(Color.CYAN);
        s.draw(
                edge,
                position.x - radius / 2f,
                position.y - radius / 2f,
                radius / 2f, radius / 2f,
                radius, radius,
                1f,
                1f,
                hue);

        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(!isActive) return;
        
        health--;
        model.applyRadialForce(position, 2f, 0.025f);
        if(health < 0)
        {
            model.applyRadialForce(position, 20, 0.05f);
            isAlive = false;
        }
        
        float numP = 64;
        for (int i = 0; i < numP; i++)
        {
            float pAngle = (float)i/(float)numP*360f;
            
            Vector3 pos = position.cpy();
            pos.x += MathUtils.cosDeg(pAngle)*radius - radius/2f;
            pos.y += MathUtils.sinDeg(pAngle)*radius;

            Vector3 dim = new Vector3(0.01f, 0.1f, 0.01f);

            Vector3 velocity = new Vector3(MathUtils.cosDeg(pAngle), MathUtils.sinDeg(pAngle), 0);

            model.createParticle(
                pos,
                velocity,
                dim,
                MathUtils.random(0.2f, 0.6f),
                Color.MAGENTA,
                Color.CYAN);
        }
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - radius, position.y - radius, radius*2, radius*2);
    }

    @Override
    public void dispose()
    {
    }
    
}
