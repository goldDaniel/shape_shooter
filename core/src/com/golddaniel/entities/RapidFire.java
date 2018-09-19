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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class RapidFire extends Entity
{

    static Texture tex = new Texture("texture.png");
    
    public RapidFire(Vector2 pos)
    {
        isAlive = true;
        position = new Vector2(pos);
    }
    
    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    @Override
    public void update(WorldModel world, float delta)
    {
    }

    @Override
    public void draw(SpriteBatch s)
    {
        s.setColor(Color.RED);
        s.draw(tex, position.x, position.y, 64, 64);
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        isAlive = false;
        model.applyRadialForce(getMid(), 320000f, 128);
        model.applyRadialForce(getMid(), 320000f, 256);
    }

    private Vector2 getMid()
    {
        return new Vector2(position.x + 32, position.y + 32);
    }
    
    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x, position.y, 64, 64);
    }

    @Override
    public void dispose()
    {
    }
    
}
