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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Particle extends Entity
{
    Vector2 pos;
    float angle;
    
    float lifespan;
    final float START_LIFESPAN;
    
    Color startColor;
    Color endColor;
    Color color;
    
    
    float width;
    float height;
    
    float speed;
    final float START_SPEED;
    
    private static TextureRegion tex = new TextureRegion(new Texture("texture.png"));
    
    public Particle(
            Vector2 pos, float dir, 
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
        
        
        
        height = 4;
        
        isAlive = true;
    }
    
    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    private void lerpColor()
    {
        color.r = MathUtils.lerp(endColor.r, startColor.r, lifespan/START_LIFESPAN);
        color.g = MathUtils.lerp(endColor.g, startColor.g, lifespan/START_LIFESPAN);
        color.b = MathUtils.lerp(endColor.b, startColor.b, lifespan/START_LIFESPAN);
        color.a = MathUtils.lerp(0.4f, 1f, lifespan/START_LIFESPAN);
    }
    
    @Override
    public void update(WorldModel world, float delta)
    {
        pos.x += MathUtils.cosDeg(angle)*speed*delta;
        pos.y += MathUtils.sinDeg(angle)*speed*delta;
        
        
        width = 128*lifespan/START_LIFESPAN;
        
        speed = START_SPEED * lifespan/START_LIFESPAN;
        
        lerpColor();
        
        lifespan -= delta;
        if(lifespan <= 0) isAlive = false;
    }

    @Override
    public void draw(SpriteBatch s)
    {
        s.enableBlending();
        s.setColor(color);
        s.draw(tex,
                pos.x, pos.y,
                width / 2, height / 2,
                width, height,
                1, 1,
                angle);
        s.setColor(Color.WHITE);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(pos.x, pos.y, width, height);
    }
    
    @Override
    public void kill(WorldModel model)
    {
        //isAlive = false;
    }
}
