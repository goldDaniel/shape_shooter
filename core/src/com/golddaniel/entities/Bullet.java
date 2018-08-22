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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.Globals;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Bullet extends Entity
{

   
    
    public static enum TYPE
    {
        LASER_1,
        LASER_2,
        LASER_3,
        LASER_4,
    }
    
    private final static TextureRegion LASER_1 = 
            new TextureRegion(new Texture("lasers/laserBlue01.png"));
    
    private final static TextureRegion LASER_2 = 
            new TextureRegion(new Texture("lasers/laserBlue02.png"));
    
    private final static TextureRegion LASER_3 = 
            new TextureRegion(new Texture("lasers/laserBlue03.png"));
    
    private final static TextureRegion LASER_4 = 
            new TextureRegion(new Texture("lasers/laserBlue04.png"));
    
    float dir;
    
    TextureRegion tex;
    
    final float MAX_SPEED = Globals.WIDTH;
    
    public Bullet(Vector2 position, float dir, TYPE type)
    {
        this.position = position.cpy();
        this.dir = dir;
        
        
        switch(type)
        {
            case LASER_1:
                tex = LASER_1;
                break;
            case LASER_2:
                tex = LASER_2;
                break;
            case LASER_3:
                tex = LASER_3;
                break;
            case LASER_4:
                tex = LASER_4;
                break;
            default:
                tex = new TextureRegion();
                Gdx.app.log("BULLET", "bullet type not valid");
                break;
        }
        
        this.position.x -= tex.getRegionWidth()/2f;
        this.position.y -= tex.getRegionHeight()/2f;
        
        isAlive = true;
    }
    
    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        position.x += MAX_SPEED*MathUtils.cosDeg(dir)*delta;
        position.y += MAX_SPEED*MathUtils.sinDeg(dir)*delta;
        
        if(position.x < -tex.getRegionWidth() || position.x > model.WORLD_WIDTH ||
           position.y < -tex.getRegionWidth() || position.y > model.WORLD_HEIGHT)
        {
            position.x = MathUtils.clamp(position.x, 0, model.WORLD_WIDTH - tex.getRegionWidth());
            position.y = MathUtils.clamp(position.y, 0, model.WORLD_HEIGHT - tex.getRegionWidth());
           
            isAlive = false;
        }
        
        model.applyRadialForce(getMid(), 400, 256);
    }

    private Vector2 getMid()
    {
        return new Vector2(
                position.x + tex.getRegionWidth()/2, 
                position.y + tex.getRegionHeight()/2);
    }
    
    @Override
    public void draw(SpriteBatch s)
    {
        s.draw(tex, 
                position.x, position.y, 
                tex.getRegionWidth()/2, tex.getRegionHeight()/2, 
                tex.getRegionWidth(), tex.getRegionHeight(), 
                1, 1, 
                dir);
    }
    
    @Override
    public void dispose()
    {
 
    }
    
    @Override
    public Rectangle getBoundingBox()
    {
        float radius = tex.getRegionWidth() > tex.getRegionHeight() ? 
                tex.getRegionWidth() : tex.getRegionHeight();
        
        
        return new Rectangle(position.x, position.y, 
            radius, radius);
    }
    
    @Override
    public void kill(WorldModel model)
    {
        isAlive = false;
    }
}
