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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Player;

import bloom.Bloom;

/**
 *
 * @author wrksttn
 */
public class WorldRenderer
{

    ExtendViewport viewport;

    SpriteBatch s;
    ModelBatch m;

    TextureRegion tex;
    
    Bloom bloom;
    public boolean doBloom = false;

    public WorldRenderer(WorldModel model)
    {
        tex = new TextureRegion(new Texture("texture.png"));
        
        s = new SpriteBatch();
        m = new ModelBatch();


        this.viewport = model.viewport;

        bloom = new Bloom(model.viewport, 2f);
        bloom.setBloomIntesity(1f);
        bloom.setTreshold(0.95f);
    }
    
    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    public void draw(WorldModel model)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        s.setProjectionMatrix(model.cam.combined);
        s.enableBlending();


        s.begin();
        {
            model.getGrid().draw(s);

            for (int i = 0; i < model.getAllEntities().size; i++)
            {
                Entity e = model.getAllEntities().get(i);
                if (!(e instanceof Player))
                {
                    e.draw(s);
                }
            }

            //draw player on top
            if (model.player != null)
            {
                model.player.draw(s);
            }

            for (int i = 0; i < model.getAllParticles().size; i++)
            {
                model.getAllParticles().get(i).draw(s);
            }
        }
        s.end();
    }
    
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        viewport.apply();
    }
}