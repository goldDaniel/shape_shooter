package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.golddaniel.main.LevelBuilder;
import com.golddaniel.main.ScreenManager;
import com.sun.java.accessibility.util.TopLevelWindowListener;

public class LevelSelectScreen extends VScreen
{

    private Texture texture0;
    private Texture texture1;
    private Texture texture2;
    private Texture texture3;
    private Texture texture4;
    private Texture texture5;
    private Texture texture6;
    private Texture texture7;
    private Texture texture8;
    private Texture texture9;

    Texture panelTexture;

    OrthographicCamera cam;
    ExtendViewport view;

    Stage uiStage;

    /**
     * @param sm
     * @param assets
     */
    public LevelSelectScreen(final ScreenManager sm, final AssetManager assets)
    {
        super(sm, assets);
        cam = new OrthographicCamera(320, 180);
        view = new ExtendViewport(320, 180, cam);


        Skin uiSkin = assets.get("ui/neon/skin/neon-ui.json",Skin.class);
        uiStage = new Stage(view);

        texture0 = assets.get("textTextures/0.png", Texture.class);
        texture1 = assets.get("textTextures/1.png", Texture.class);
        texture2 = assets.get("textTextures/2.png", Texture.class);
        texture3 = assets.get("textTextures/3.png", Texture.class);
        texture4 = assets.get("textTextures/4.png", Texture.class);
        texture5 = assets.get("textTextures/5.png", Texture.class);
        texture6 = assets.get("textTextures/6.png", Texture.class);
        texture7 = assets.get("textTextures/7.png", Texture.class);
        texture8 = assets.get("textTextures/8.png", Texture.class);
        texture9 = assets.get("textTextures/9.png", Texture.class);


        Table table = new Table();
        table.setFillParent(true);


        final TextButton level1 = new TextButton("1", uiSkin);
        level1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel1(assets);
                Gdx.input.setInputProcessor(null);
            }
        });


        TextButton level2 = new TextButton("2", uiSkin);
        level2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel2(assets);
                Gdx.input.setInputProcessor(null);
            }
        });

        TextButton level3 = new TextButton("3", uiSkin);
        level3.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel3(assets);
                Gdx.input.setInputProcessor(null);
            }
        });

        TextButton level4 = new TextButton("4", uiSkin);
        level4.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel4(assets);
                Gdx.input.setInputProcessor(null);
            }
        });

        TextButton level5 = new TextButton("5", uiSkin);
        level5.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel5(assets);
                Gdx.input.setInputProcessor(null);
            }
        });

        TextButton level6 = new TextButton("6", uiSkin);
        level6.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                LevelBuilder.buildLevel6(assets);
                Gdx.input.setInputProcessor(null);
            }
        });

        table.center();
        table.row().height(50);
        table.add(new Label("", uiSkin));
        table.add(new Label("SELECT A LEVEL", uiSkin));
        table.row().height(50);
        table.add(level1).width(100);
        table.add(level2).width(100);
        table.add(level3).width(100);
        table.row().height(50);
        table.add(level4).width(100);
        table.add(level5).width(100);
        table.add(level6).width(100);


        uiStage.addActor(table);
    }


    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(uiStage);
        uiStage.act(delta);
        uiStage.draw();

        if(LevelBuilder.isLoaded())
        {
            sm.setScreen(ScreenManager.STATE.PLAY);
        }
    }

    @Override
    public void show()
    {

    }

    @Override
    public void resize(int width, int height)
    {
        view.update(width, height, false);
        view.apply();
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
    public void hide()
    {

    }
}
