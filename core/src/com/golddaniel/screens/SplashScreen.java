/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.main.Globals;

/**
 *
 * @author wrksttn
 */
public class SplashScreen extends VScreen
{
    
    FitViewport viewport;
    OrthographicCamera camera;
    SpriteBatch s;
    
    ShaderProgram fadeShader;
    
    Texture splash;
   
    float opacity = 0f;
    boolean fadeIn = true;
    float delay = 1.5f;
    
    public SplashScreen(ScreenManager sm)
    {
        super(sm);
        
        //we use delay*2 as we want the logo to stay fully on screen
        //for as long as delay lasts 
        new Timer().scheduleTask(new Timer.Task()
        {
            @Override
            public void run()
            {
                fadeIn = false;
            }
        }, delay*2);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(Globals.WIDTH, Globals.HEIGHT, camera);
        camera.setToOrtho(false);
        splash = new Texture("splash.png");
        s = new SpriteBatch();
       
        
        fadeShader = new ShaderProgram(
                Gdx.files.internal("shaders/transition.vert"), 
                Gdx.files.internal("shaders/transition.frag"));
        
        if(!fadeShader.isCompiled())
        {
            Gdx.app.error("SplashScreen", "FadeShader failed to compile");
            Gdx.app.exit();
        }
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if(fadeIn)
        {
            opacity += delta/delay;
        }
        else
        {
            opacity -= delta/delay;
        }

        if(opacity < 0)
        {
            sm.setScreen(ScreenManager.STATE.MAIN_MENU);
        }
        
        fadeShader.begin();
        fadeShader.setUniformf("opacity", opacity);
        fadeShader.end();
        
        s.setShader(fadeShader);
        
        s.setProjectionMatrix(camera.combined);
        s.begin();
        s.draw(splash, 0, 0);
        s.end();
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
    public void dispose()
    {
        splash.dispose();
        s.dispose();
        fadeShader.dispose();
    }
}
