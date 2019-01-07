
package com.golddaniel.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ArrayMap;
import com.golddaniel.screens.PlayScreen;
import com.golddaniel.screens.VScreen;

/**
 *
 * @author wrksttn
 */
public class ScreenManager
{
    public enum STATE
    {
        PLAY,
        LEVEL_SELECT,
        MAIN_MENU,
        SPLASH,
    }
    
    STATE currentState;
    ArrayMap<STATE, VScreen> screenMap;
    
    public ScreenManager()
    {
        screenMap = new ArrayMap<STATE, VScreen>();
    }
    
    public boolean setScreen(STATE state)
    {
        boolean result = false;
        
        if(screenMap.containsKey(state))
        {
            currentState = state;
            result = true;
        }
        return result;
    }

    /**
     * 
     * @param state
     * @param screen
     * @return true if initialized, false if failed
     */
    public boolean initalizeScreen(STATE state, VScreen screen)
    {
        boolean result = false;
        
        if(!screenMap.containsKey(state))
        {
            screenMap.put(state, screen);
        }
        
        return result;
    }
    
    public boolean disposeScreen(STATE state)
    {
        boolean result = false;
        
        if(screenMap.containsKey(state))
        {
            screenMap.removeKey(state).dispose();
            result = true;
        }
        return result;
    }
    
    /**
     * Call at program end
     */
    public void dispose()
    {
        for(VScreen screen : screenMap.values().toArray())
        {
            screen.dispose();
        }
    }
    
    public void render(float delta)
    {   
        screenMap.get(currentState).render(delta);
        
        if(Gdx.input.isKeyJustPressed(Keys.R))
        {
            disposeScreen(STATE.PLAY);
            initalizeScreen(STATE.PLAY, new PlayScreen(this));
            setScreen(STATE.PLAY);
        }
    }
    
    public void resize(int width, int height)
    {
        for(VScreen screen : screenMap.values().toArray())
        {
            screen.resize(width, height);
        }
    }
}
