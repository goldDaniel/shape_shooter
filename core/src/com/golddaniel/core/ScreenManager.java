
package com.golddaniel.core;

import com.badlogic.gdx.utils.ArrayMap;
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
        MAIN_MENU,
        LEVEL_SELECT,
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
            screenMap.get(currentState).show();
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
    }
    
    public void resize(int width, int height)
    {
        for(VScreen screen : screenMap.values().toArray())
        {
            screen.resize(width, height);
        }
    }
}
