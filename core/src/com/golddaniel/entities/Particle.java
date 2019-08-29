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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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


    Vector3 pos;
    Vector2 velocity;
    Vector3 dim;

    float lifespan;

    //would make final, but cannot due to poolable
    float START_LIFESPAN;

    Color startColor;
    Color endColor;
    Color color;

    private static TextureRegion circleTex;

    boolean isAlive;

    public static void loadTextures(AssetManager assets)
    {
        if(circleTex == null)
            circleTex = new TextureRegion(assets.get("circle.png", Texture.class));
    }

    public Particle(
            Vector3 pos, Vector3 velocity, Vector3 dim,
            float lifespan, Color startColor, Color endColor)
    {
        init(pos, velocity, dim, lifespan, startColor, endColor);
    }

    public void init(
            Vector3 pos, Vector3 velocity, Vector3 dim,
            float lifespan, Color startColor, Color endColor)
    {
        this.pos = pos;
        this.velocity = new Vector2(velocity.x, velocity.y);
        this.dim = dim;

        START_LIFESPAN = lifespan;
        this.lifespan = lifespan;

        this.startColor = startColor;
        this.endColor = endColor;

        color = new Color();

        isAlive = true;
    }

    private void lerpColor()
    {
        color.r = MathUtils.lerp(endColor.r, startColor.r, lifespan / START_LIFESPAN);
        color.g = MathUtils.lerp(endColor.g, startColor.g, lifespan / START_LIFESPAN);
        color.b = MathUtils.lerp(endColor.b, startColor.b, lifespan / START_LIFESPAN);
        color.a = MathUtils.lerp(0.01f, 1f, lifespan / START_LIFESPAN);
    }

    public void update(WorldModel world, float delta)
    {

        pos.add(velocity.x * delta, velocity.y * delta, 0);

        lerpColor();

        lifespan -= delta;
        if (lifespan <= 0) isAlive = false;
    }

    public void draw(SpriteBatch s)
    {
        s.enableBlending();
        s.setColor(color);

        s.draw(circleTex,
                pos.x - dim.x / 2f, pos.y - dim.y / 2f,
                dim.x / 2f, dim.y / 2f,
                dim.x, dim.y,
                1f, 1f,
                velocity.angle());
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
        pos.set(-1000, -1000, -1000);
        velocity.set(0, 0);
        dim.set(0, 0, 0);
        isAlive = true;


        startColor = endColor = color = Color.WHITE.cpy();
    }
}
