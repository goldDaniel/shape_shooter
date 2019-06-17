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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Bullet extends Entity implements Pool.Poolable
{

    float width;
    float height;

    @Override
    public void reset()
    {

    }

    public enum TYPE
    {
        LASER_1,
        LASER_2,
        LASER_3,
        LASER_4,
    }
    
    private static TextureRegion LASER_1;
    
    private static TextureRegion LASER_2;
    
    private static TextureRegion LASER_3;
    
    private static TextureRegion LASER_4;
    
    float dir;
    
    TextureRegion tex;
    
    float speed;

    public static void loadTextures(AssetManager assets)
    {
        if(LASER_1 == null)
        {
            LASER_1 = new TextureRegion(assets.get("lasers/laserRed14.png", Texture.class));
        }
        if(LASER_2 == null)
        {
            LASER_2 = new TextureRegion(assets.get("lasers/laserBlue02.png", Texture.class));
        }
        if(LASER_3 == null)
        {
            LASER_3 = new TextureRegion(assets.get("lasers/laserBlue03.png", Texture.class));
        }
        if(LASER_4 == null)
        {
            LASER_4 = new TextureRegion(assets.get("lasers/laserBlue04.png", Texture.class));
        }
    }

    public Bullet(Vector3 position, float speed, float dir, TYPE type, AssetManager assets)
    {
        super(assets);
        init(position, speed, dir, type);
    }
    
    public final void init(Vector3 position, float speed, float dir, TYPE type)
    {
        this.position = position.cpy();
        this.dir = dir;
        this.speed = speed;

        if(type != null)
        {
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
            }
        }
        else
        {
           tex = new TextureRegion();
        }

        width = 0.05f;
        height = 0.65f;

        this.position.x -= width / 2f;
        this.position.y -= height / 2f;

        isAlive = true;
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        position.x += speed*MathUtils.cosDeg(dir)*delta;
        position.y += speed*MathUtils.sinDeg(dir)*delta;
        
        if(position.x < -model.WORLD_WIDTH|| position.x > model.WORLD_WIDTH ||
           position.y < -model.WORLD_HEIGHT || position.y > model.WORLD_HEIGHT)
        {
            position.x = MathUtils.clamp(position.x, -model.WORLD_WIDTH, model.WORLD_WIDTH);
            position.y = MathUtils.clamp(position.y, -model.WORLD_HEIGHT, model.WORLD_HEIGHT);
           
            isAlive = false;
        }

        Vector3 pos = position.cpy();
        model.applyRadialForce(pos, 35 * delta, height);
    }

    
    @Override
    public void draw(SpriteBatch s)
    {
        if(isAlive)
        {
            s.setColor(Color.PINK);
            s.draw(tex,
                    position.x - width / 2f, position.y - height / 2f,
                    width / 2f, height / 2f,
                    width, height,
                    1f, 1f,
                    dir + 90f);
        }
    }
    
    @Override
    public void dispose()
    {
 
    }
    
    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width / 2f, position.y - height / 2f,
            width, height);
    }
    
    @Override
    public void kill(WorldModel model)
    {
        isAlive = false;
    }
}
