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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 * 
 */
public class Particle implements Pool.Poolable
{

    float width = 0.005f;
    float height = 0.005f;

    Vector3 pos;
    float angle;
    
    float lifespan;
    
    //would make final, but cannot due to poolable
    float START_LIFESPAN;
    
    Color startColor;
    Color endColor;
    Color color;

    float speed;
    
    //would make final, but cannot due to poolable
    float START_SPEED;

    //private static TextureRegion rectTex = new TextureRegion(new Texture("texture.png"));
    private static TextureRegion circleTex = new TextureRegion(new Texture("circle.png"));
    
    boolean isAlive;
    
    public Particle(
            Vector3 pos, float dir, 
            float lifespan, Color startColor, Color endColor, 
            float speed)
    {
        init(pos, dir, lifespan, startColor, endColor, speed);
    }
    
     public void init(
            Vector3 pos, float dir, 
            float lifespan, Color startColor, Color endColor, 
            float speed)
    {
        this.pos = pos;
        this.angle = dir;
        
        START_LIFESPAN = lifespan;
        this.lifespan = lifespan;
        
        this.startColor = startColor;
        this.endColor = endColor;
        
        color = new Color();
        
        this.START_SPEED = speed;

        isAlive = true;
    }
    
    private void lerpColor()
    {
        color.r = MathUtils.lerp(endColor.r, startColor.r, lifespan/START_LIFESPAN);
        color.g = MathUtils.lerp(endColor.g, startColor.g, lifespan/START_LIFESPAN);
        color.b = MathUtils.lerp(endColor.b, startColor.b, lifespan/START_LIFESPAN);
        color.a = MathUtils.lerp(0.3f, 1f, lifespan/START_LIFESPAN);
    }
    
    public void update(WorldModel world, float delta)
    {
        pos.x += MathUtils.cosDeg(angle)*speed*delta;
        pos.y += MathUtils.sinDeg(angle)*speed*delta;
        
        
        speed = START_SPEED * lifespan/START_LIFESPAN;
        
        lerpColor();
        
        lifespan -= delta;
        if(lifespan <= 0) isAlive = false;
    }
    
    public void draw(SpriteBatch s)
    {
        s.enableBlending();
        s.setColor(color);

        s.draw(circleTex,
                pos.x - width / 2f, pos.y - height / 2f,
                width / 2f, height / 2f,
                height, width,
                1f, 1f,
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
        pos = new Vector3(-100, -100, 0);
        angle = 0;
        lifespan = -1;
        START_LIFESPAN = -1;
        
        startColor = endColor = color = null;

        speed = START_SPEED = 0;
    }
}
