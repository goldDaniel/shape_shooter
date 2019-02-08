package com.golddaniel.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
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

    @Override
    public void create () 
    {
        sm = new ScreenManager();

        assets = new AssetManager();

        assets.load("texture.png", Texture.class);

        {
            FileHandleResolver resolver = new InternalFileHandleResolver();
            assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
            assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

            FreetypeFontLoader.FreeTypeFontLoaderParameter parms = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            parms.fontFileName = "fonts/Square.ttf";  // path of .ttf file where that exist
            parms.fontParameters.size = 64;
            assets.load("Square.ttf", BitmapFont.class, parms);   // fileName with extension, sameName will use to get from manager
        }

        {
            SkinLoader.SkinParameter parms = new SkinLoader.SkinParameter("ui/neon/skin/neon-ui.atlas");
            assets.load("ui/neon/skin/neon-ui.atlas", TextureAtlas.class);
            assets.load("ui/neon/skin/neon-ui.json",
                    Skin.class, parms);
        }

        assets.load("skybox.jpg", Texture.class);
        assets.load("geometric/dashedCircle.png", Texture.class);
        assets.load("geometric/player.png", Texture.class);

        assets.load("lasers/laserBlue01.png", Texture.class);
        assets.load("lasers/laserBlue02.png", Texture.class);
        assets.load("lasers/laserBlue03.png", Texture.class);
        assets.load("lasers/laserBlue04.png", Texture.class);

        while(assets.update())
        {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
        assets.finishLoading();
        Gdx.app.log("ENGINE", "FINISHED LOADING");

        //initalize our screens with enums to access
        sm.initalizeScreen(ScreenManager.STATE.MAIN_MENU, new MainMenuScreen(sm, assets));
        sm.initalizeScreen(ScreenManager.STATE.PLAY, new GameScreen(sm, assets));
        sm.setScreen(ScreenManager
                .STATE.MAIN_MENU);

        finishedLoading = true;
    }

    @Override
    public void render ()
    {
        if(finishedLoading)
        {
            sm.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose ()
    {
        //fixed issue on linux where you could not move mouse after
        //closing the window
        

        sm.dispose();
    }
    
    @Override
    public void resize(int width, int height)
    {
        sm.resize(width, height);
    }
}
