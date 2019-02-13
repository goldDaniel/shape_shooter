/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.PhysicsGrid;

/**
 *
 * @author wrksttn
 */
public class MainMenuScreen extends VScreen
{
    
    ExtendViewport viewport;
    OrthographicCamera camera;
    SpriteBatch s;

    BitmapFont font;

    Skin uiSkin;
    Stage uiStage;

    public MainMenuScreen(final ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
                
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(72, 128, camera);
        viewport.apply();
        s = new SpriteBatch();
        s.enableBlending();        


        font = assets.get("Square.ttf", BitmapFont.class);
        uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);

        uiStage = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);


        uiStage.addActor(table);

        final TextButton playBtn = new TextButton("PLAY", uiSkin, "default");

        playBtn.getLabel().setFontScale(0.5f);

        playBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                sm.setScreen(ScreenManager.STATE.PLAY);
            }
        });

        final TextButton optionsBtn = new TextButton("OPTIONS", uiSkin, "default");

        optionsBtn.getLabel().setFontScale(0.5f);

        optionsBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){

            }
        });

        final TextButton quitBtn = new TextButton("QUIT", uiSkin, "default");
        quitBtn.getLabel().setFontScale(0.5f);

        quitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });

        table.add(playBtn);
        table.row();
        table.add(optionsBtn);
        table.row();
        table.add(quitBtn);
    }
    
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(uiStage);

        camera.position.x = 0f;
        camera.position.y = 0f;
        camera.update();

        camera.position.x = viewport.getWorldWidth()  / 2f;
        camera.position.y = viewport.getWorldHeight() / 2f;
        camera.update();

        uiStage.act();
        uiStage.draw();
    }

    @Override
    public void show()
    {
    }

    @Override
    public void hide()
    {
    }
    
    @Override 
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        viewport.apply();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override 
    public void dispose()
    {
        s.dispose();
    }
}