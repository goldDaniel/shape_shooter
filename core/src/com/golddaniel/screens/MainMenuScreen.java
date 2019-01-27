/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import bloom.Bloom;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.golddaniel.main.Assets;
import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.main.Globals;
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
    
    PhysicsGrid g;

    Skin uiSkin;
    Stage uiStage;

    public MainMenuScreen(final ScreenManager sm, Assets assets)
    {
        super(sm, assets);
                
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(72, 128, camera);
        viewport.apply();
        s = new SpriteBatch();
        s.enableBlending();        
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 140;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();

        uiSkin = new Skin(Gdx.files.internal("ui/neon/skin/neon-ui.json"));
        uiStage = new Stage(viewport);

        int spacing = 8;
        g = new PhysicsGrid(new Vector2(72f*2, 128f*2), spacing);

        Table table = new Table();
        table.setFillParent(true);

        table.setY(-128f/2f);
        table.setX(-72f/2f);

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

    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(uiStage);

        g.update(delta);
        
        camera.position.x = 0f;
        camera.position.y = 0f;
        camera.update();

        s.setProjectionMatrix(camera.combined);
        s.begin();
        g.draw(s);
        s.end();

        uiStage.act();
        uiStage.getBatch().setProjectionMatrix(camera.combined);
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