/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import bloom.Bloom;
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
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.controllers.ControllerMapping;
import com.golddaniel.controllers.InputController;
import com.golddaniel.main.Globals;
import com.golddaniel.main.PhysicsGrid;

/**
 *
 * @author wrksttn
 */
public class MainMenuScreen extends VScreen
{
    
    FitViewport viewport;
    OrthographicCamera camera;
    SpriteBatch s;
    
    
    Color titleColor;
    float titleHue;
    BitmapFont font;
    
    PhysicsGrid g;
    
    Texture tex;
            
    Vector2 point;
    
    Bloom bloom;
    
    public MainMenuScreen(ScreenManager sm)
    {
        super(sm);
                
        camera = new OrthographicCamera();
        viewport = new FitViewport(Globals.WIDTH, Globals.HEIGHT, camera);
        camera.position.x = Globals.WIDTH/2f;
        camera.position.y = Globals.HEIGHT/2f;
        viewport.apply();
        s = new SpriteBatch();
        s.enableBlending();        
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 256;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        
        titleColor = new Color(1f, 1f, 1f, 1f);
        
        int spacing = 32;
        g = new PhysicsGrid(new Vector2(Globals.WIDTH*2, Globals.HEIGHT*2), 
                Globals.WIDTH*2/spacing, Globals.HEIGHT*2/spacing);
        
        tex = new Texture("texture.png");
        
        point = new Vector2(Globals.WIDTH/2f, Globals.HEIGHT/2f);

        bloom = new Bloom(viewport, 1f);
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
        
        s.setProjectionMatrix(camera.combined);
        
        titleHue += 15f*delta;
        titleHue %= 360f;
        
        g.setColor(titleColor.cpy().fromHsv(((titleHue + 180) % 360), 1f, 1f));
        
        titleColor.fromHsv(titleHue, 1f, 1f);

        if(Gdx.input.isKeyJustPressed(Keys.ENTER) || 
                ( (InputController.controller != null && 
                InputController.controller.getButton(ControllerMapping.START)) ))
        {
            sm.setScreen(ScreenManager.STATE.LEVEL_SELECT);
        }
        
        if(InputController.controller != null)
        {
            
            float x = InputController.controller.getAxis(ControllerMapping.R_STICK_HORIZONTAL_AXIS);
            float y = -InputController.controller.getAxis(ControllerMapping.R_STICK_VERTICAL_AXIS);
            
            
            if(SharedLibraryLoader.isWindows)
            {
                y = -y;
            }
            
            if(abs(x) < 0.15f) x = 0;
            if(abs(y) < 0.15f) y = 0;
            
            Vector2 dir = new Vector2(x, y).nor();
            point.add(dir.scl(600f*delta));
            g.applyRadialForce(point, 1000f, 256);
        }
        
        
        g.update(delta);
        
        camera.position.x = Globals.WIDTH/2f;
        camera.position.y = Globals.HEIGHT/2f;
        camera.update();
        
        bloom.capture();
        s.begin();
        
        g.draw(s);
        
        font.setColor(titleColor);
        font.draw(s, "SHAPE SHOOTER", 32, Globals.HEIGHT-64);
        
        font.setColor(Color.WHITE);
        font.draw(s, "PRESS START", 192, Globals.HEIGHT/2f);
        s.end();
        
        bloom.render();
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
        s.dispose();
    }
}