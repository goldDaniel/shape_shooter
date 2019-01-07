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
package com.golddaniel.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 * 
 */
public class Particle implements Pool.Poolable
{
    public static enum TYPE
    {
        NORMAL,
        SPIN,
        TRAIL,
    }
    
    Vector2 pos;
    float angle;
    
    float lifespan;
    
    //would make final, but cannot due to poolable
    float START_LIFESPAN;
    
    Color startColor;
    Color endColor;
    Color color;
    
    
    float START_WIDTH;
    float width;
    float height;
    
    float speed;
    
    //would make final, but cannot due to poolable
    float START_SPEED;
    
    TYPE type;
    
    //private static TextureRegion rectTex = new TextureRegion(new Texture("texture.png"));
    private static TextureRegion circleTex = new TextureRegion(new Texture("circle.png"));
    
    boolean isAlive;
    
    public Particle(
            Vector2 pos, float dir, 
            float lifespan, Color startColor, Color endColor, 
            float speed, TYPE type)
    {
        init(pos, dir, lifespan, startColor, endColor, speed,type);
    }
    
     public void init(
            Vector2 pos, float dir, 
            float lifespan, Color startColor, Color endColor, 
            float speed,
            TYPE type)
    {
        this.pos = pos;
        this.angle = dir;
        
        START_LIFESPAN = lifespan;
        this.lifespan = lifespan;
        
        this.startColor = startColor;
        this.endColor = endColor;
        
        color = new Color();
        
        this.START_SPEED = speed;
        
        this.type = type;
        
        
        height = 4;
        
        if(type == TYPE.SPIN)
        {
            START_WIDTH = 64;
        }
        else if(type == TYPE.TRAIL)
        {
            START_WIDTH = 8;
            height = 8;
        }
        else
        {
            START_WIDTH = 128;
        }
        isAlive = true;
    }
    
    private void lerpColor()
    {
        color.r = MathUtils.lerp(endColor.r, startColor.r, lifespan/START_LIFESPAN);
        color.g = MathUtils.lerp(endColor.g, startColor.g, lifespan/START_LIFESPAN);
        color.b = MathUtils.lerp(endColor.b, startColor.b, lifespan/START_LIFESPAN);
        color.a = MathUtils.lerp(0.05f, 1f, lifespan/START_LIFESPAN);
    }
    
    public void update(WorldModel world, float delta)
    {
        pos.x += MathUtils.cosDeg(angle)*speed*delta;
        pos.y += MathUtils.sinDeg(angle)*speed*delta;
        
        
        speed = START_SPEED * lifespan/START_LIFESPAN;
        
        if(type == TYPE.NORMAL || type == TYPE.SPIN)
        {
            width = START_WIDTH*lifespan/START_LIFESPAN;
        }
        else
        {
            width = START_WIDTH;
        }
        
        if(type == TYPE.SPIN)
        {
            angle += MathUtils.random(180f, 270f) * delta * lifespan/START_LIFESPAN;
        }
        
        lerpColor();
        
        lifespan -= delta;
        if(lifespan <= 0) isAlive = false;
    }
    
    public void draw(SpriteBatch s)
    {
        s.enableBlending();
        s.setColor(color);

        
        s.draw(circleTex,
                pos.x, pos.y,
                width/2f, height/2f,
                width, height,
                1, 1,
                angle);
        s.setColor(Color.WHITE);
    }

    public boolean isAlive()
    {
        return isAlive;
    }
    
    public void dispose()
    {
        
    }

    @Override
    public void reset()
    {
        pos = new Vector2(-100, -100);
        angle = 0;
        lifespan = -1;
        START_LIFESPAN = -1;
        
        startColor = endColor = color = null;
        
        width = height = 0;
        speed = START_SPEED = 0;
    }
}
