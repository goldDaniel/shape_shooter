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

import bloom.Bloom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Player;

/**
 *
 * @author wrksttn
 */
public class WorldRenderer
{

    FitViewport viewport;

    SpriteBatch s;
    
    TextureRegion tex;
    
    Bloom bloom;

    public boolean doBloom = true;


    public WorldRenderer(WorldModel model)
    {
        tex = new TextureRegion(new Texture("texture.png"));
        
        s = new SpriteBatch();

        this.viewport = model.viewport;

        bloom = new Bloom(model.viewport, 0.75f);
        bloom.setBloomIntesity(1f);
        bloom.setTreshold(0.1f);
    }
    
    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    public void draw(WorldModel model)
    {
        if(Gdx.input.isKeyJustPressed(Keys.P))
        {
            doBloom = !doBloom;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        bloom.setClearColor(0f, 0f, 0f, 1f);
        
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        


        if(doBloom) bloom.capture();

        s.setProjectionMatrix(model.cam.combined);
        s.enableBlending();
        s.begin();

        model.getGrid().draw(s);

        for(int i = 0; i < model.getAllEntities().size; i++)
        {
            Entity e = model.getAllEntities().get(i);
            if(!(e instanceof  Player))
            {
                e.draw(s);
            }
        }
        
        //draw player on top
        if(model.player != null)
        {
            model.player.draw(s);
        }

        for(int i = 0; i < model.getAllParticles().size; i++)
        {
            model.getAllParticles().get(i).draw(s);
        }
        
        s.end();
        if(doBloom)bloom.render();
    }
    
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        viewport.apply();
    }
}