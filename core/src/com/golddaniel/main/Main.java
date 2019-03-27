package com.golddaniel.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.golddaniel.screens.*;

/**
 * entry point for our logic
 * @author wrksttn
 */
public class Main extends ApplicationAdapter {
    
    ScreenManager sm;

    AssetManager assets;

    boolean finishedLoading = false;

    SpriteBatch s;

    Skin uiSkin;
    ProgressBar bar;

    @Override
    public void create () 
    {
        s = new SpriteBatch();
        uiSkin = new Skin(Gdx.files.internal("ui/neon/skin/neon-ui.json"));

        bar = new ProgressBar(0f, 1f, 0.001f, false, uiSkin);
        bar.setSize(Gdx.graphics.getWidth(), 50);

        assets = new AssetManager();

        assets.load("texture.png", Texture.class);
        assets.load("circle.png", Texture.class);

        {
            FileHandleResolver resolver = new InternalFileHandleResolver();
            assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
            assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

            FreetypeFontLoader.FreeTypeFontLoaderParameter parms72 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            parms72.fontFileName = "fonts/Square.ttf";  // path of .ttf file
            parms72.fontParameters.size = 48;
            assets.load("Square72.ttf", BitmapFont.class, parms72);   // fileName with extension, sameName will use to get from manager

            FreetypeFontLoader.FreeTypeFontLoaderParameter parms32 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            parms32.fontFileName = "fonts/Square.ttf";  // path of .ttf file
            parms32.fontParameters.size = 32;
            assets.load("Square32.ttf", BitmapFont.class, parms32);
        }

        {
            SkinLoader.SkinParameter parms = new SkinLoader.SkinParameter("ui/neon/skin/neon-ui.atlas");
            assets.load("ui/neon/skin/neon-ui.atlas", TextureAtlas.class);
            assets.load("ui/neon/skin/neon-ui.json",
                    Skin.class, parms);
        }


        assets.load("textTextures/x.png", Texture.class);
        assets.load("textTextures/0.png", Texture.class);
        assets.load("textTextures/1.png", Texture.class);
        assets.load("textTextures/2.png", Texture.class);
        assets.load("textTextures/3.png", Texture.class);
        assets.load("textTextures/4.png", Texture.class);
        assets.load("textTextures/5.png", Texture.class);
        assets.load("textTextures/6.png", Texture.class);
        assets.load("textTextures/7.png", Texture.class);
        assets.load("textTextures/8.png", Texture.class);
        assets.load("textTextures/9.png", Texture.class);

        assets.load("skybox.jpg", Texture.class);
        assets.load("geometric/dashedCircle.png", Texture.class);
        assets.load("geometric/dashedSquare.png", Texture.class);
        assets.load("geometric/player.png", Texture.class);

        assets.load("lasers/laserRed14.png", Texture.class);
        assets.load("lasers/laserBlue02.png", Texture.class);
        assets.load("lasers/laserBlue03.png", Texture.class);
        assets.load("lasers/laserBlue04.png", Texture.class);


        assets.load("sounds/bouncer_death.mp3", Sound.class);
        assets.load("sounds/player_death.wav", Sound.class);
        assets.load("sounds/pickup.wav", Sound.class);
        assets.load("sounds/laser.wav", Sound.class);
        assets.load("sounds/respawn.wav", Sound.class);

        assets.load("sounds/background.mp3", Music.class);
    }

    @Override
    public void render ()
    {
        if(finishedLoading)
        {
            if(sm != null) sm.render(Gdx.graphics.getDeltaTime());
        }
        else
        {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            //ghetto loading bar
            s.begin();
            bar.setPosition(0, Gdx.graphics.getHeight() / 2f);
            bar.setValue(assets.getProgress());
            bar.act(Gdx.graphics.getDeltaTime());
            bar.draw(s, 1);
            s.end();

            finishedLoading = assets.update();

            if(finishedLoading)
            {
                Gdx.app.log("ENGINE", "FINISHED LOADING");
                assets.finishLoading();

                if(sm == null)
                {
                    sm = new ScreenManager();
                    //initalize our screens with enums to access
                    sm.initalizeScreen(ScreenManager.STATE.MAIN_MENU, new MainMenuScreen(sm, assets));
                    sm.initalizeScreen(ScreenManager.STATE.PLAY, new GameScreen(sm, assets));
                    sm.initalizeScreen(ScreenManager.STATE.LEVEL_SELECT, new LevelSelectScreen(sm, assets));
                    sm.setScreen(ScreenManager
                            .STATE.MAIN_MENU);

                    sm.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }

            }
        }
    }

    @Override
    public void dispose ()
    {
        //fixed issue on linux where you could not move mouse after
        //closing the window
        
        AudioSystem.dispose();
        sm.dispose();
    }
    
    @Override
    public void resize(int width, int height)
    {
        if(sm != null) sm.resize(width, height);
    }
}
