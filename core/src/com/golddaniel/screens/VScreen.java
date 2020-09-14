/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.golddaniel.core.ScreenManager;
import com.badlogic.gdx.Screen;

/**
 *
 * @author wrksttn
 */
public abstract class VScreen implements Screen
{
    final AssetManager assets;
    final ScreenManager sm;
    boolean inTransition;
    
    /**
     *
     * @param sm
     */
    public VScreen(ScreenManager sm, AssetManager assets)
    {
        this.sm = sm;
        this.assets = assets;
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
    public abstract void resize(int width, int height);

    @Override
    public abstract void pause();

    @Override
    public abstract  void resume();

    @Override
    public abstract void hide();

    @Override
    public void dispose(){}
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
}
