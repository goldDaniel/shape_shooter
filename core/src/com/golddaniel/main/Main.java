package com.golddaniel.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.golddaniel.screens.*;

/**
 * entry point for our logic
 * @author wrksttn
 */
public class Main extends ApplicationAdapter {
    
    ScreenManager sm;
    
    @Override
    public void create () 
    {   
        Gdx.input.setCursorCatched(true);
        sm = new ScreenManager();
        
        //initalize our screens with enums to access
        sm.initalizeScreen(ScreenManager.STATE.SPLASH, new SplashScreen(sm));
        sm.initalizeScreen(ScreenManager.STATE.MAIN_MENU, new MainMenuScreen(sm));
        sm.initalizeScreen(ScreenManager.STATE.LEVEL_SELECT, new LevelSelectScreen(sm));
        sm.initalizeScreen(ScreenManager.STATE.PLAY, new PlayScreen(sm));
        
        
        sm.setScreen(ScreenManager.STATE.MAIN_MENU);
    }

    @Override
    public void render () 
    {
        sm.render(Gdx.graphics.getDeltaTime()*Globals.TIMESCALE);
    }

    @Override
    public void dispose () 
    {
        //fixed issue on linux where you could not move mouse after
        //closing the window
        Gdx.input.setCursorCatched(false);
        
        
        sm.dispose();
    }
    
    @Override
    public void resize(int width, int height)
    {
        sm.resize(width, height);
    }
}
