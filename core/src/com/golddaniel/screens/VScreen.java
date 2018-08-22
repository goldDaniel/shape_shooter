/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Screen;

/**
 *
 * @author wrksttn
 */
public abstract class VScreen implements Screen
{
    final ScreenManager sm;
    boolean inTransition;
    
    /**
     *
     * @param sm
     */
    public VScreen(ScreenManager sm)
    {
        this.sm = sm;
        inTransition = false;
    }
    
    //GAME STUFF
    @Override
    public abstract void render(float delta);

    /////////////////////////////////////////////////////////////
    //SCREEN API/////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    @Override
    public abstract void show();

    @Override
    public void resize(int width, int height){}

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public abstract void hide();

    @Override
    public void dispose(){}
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
}
