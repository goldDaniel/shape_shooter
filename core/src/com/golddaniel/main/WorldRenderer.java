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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.SharedLibraryLoader;
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

    private ExtendViewport viewport;

    private SpriteBatch s;
    private ModelBatch m;

    private Bloom bloom;

    private Model skyboxModel;
    private ModelInstance skybox;

    private AssetManager assets;

    private FrameBuffer fbo;

    boolean rebuildFramebuffer = false;

    boolean doBloom = true;

    public WorldRenderer(WorldModel model, AssetManager assets)
    {
        s = new SpriteBatch();
        m = new ModelBatch();


        this.viewport = model.viewport;
        this.assets = assets;

        float scale;
        if(SharedLibraryLoader.isAndroid)
        {
            scale = 1f/12f;
        }
        else
        {
            scale = 2f;

        }

        bloom = new Bloom(model.viewport, scale);
        bloom.setTreshold(0f);
        bloom.setBloomIntesity(1.25f);

        Texture tex = assets.get("skybox.jpg", Texture.class);

        ModelBuilder modelBuilder = new ModelBuilder();
        skyboxModel = modelBuilder.createSphere(
                                     -256f, -256f, -256f,
                                            128, 128,
                                            new Material(TextureAttribute.createDiffuse(tex)),
                                    VertexAttributes.Usage.Position |
                                             VertexAttributes.Usage.Normal |
                                             VertexAttributes.Usage.TextureCoordinates);

        skybox = new ModelInstance(skyboxModel);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), false);
    }

    public void draw(WorldModel model)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) doBloom = !doBloom;

        if(rebuildFramebuffer)
        {
            fbo.dispose();
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888,
                                  viewport.getScreenWidth(),
                                  viewport.getScreenHeight(),
                         false);
            rebuildFramebuffer = false;
        }

        fbo.begin();

        m.begin(model.getCamera());
        m.render(skybox);
        m.end();

        s.enableBlending();
        s.setProjectionMatrix(model.cam.combined);

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
            for (int i = 0; i < model.getAllParticles().size; i++)
            {
                model.getAllParticles().get(i).draw(s);
            }
            //draw player on top
            if (model.player != null)
            {
                model.player.draw(s);
            }
            s.end();
        }
        fbo.end();

        //need to flip that y axis
        Matrix4 proj = new Matrix4().setToOrtho2D(
                                    0, Gdx.graphics.getHeight(),
                                    Gdx.graphics.getWidth(),
                                    -Gdx.graphics.getHeight());
        s.setProjectionMatrix(proj);


        if(doBloom) bloom.capture();
        s.begin();
        s.draw(fbo.getColorBufferTexture(),
                0, 0);
        s.end();
        if(doBloom) bloom.render();
    }
    
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        viewport.apply();

        rebuildFramebuffer = true;
    }

    public void dispose()
    {
        skyboxModel.dispose();
        s.dispose();
        m.dispose();
        bloom.dispose();
    }
}