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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.main.Globals;

/**
 *
 * @author wrksttn
 */
public class LevelSelectScreen extends VScreen
{
    
    FitViewport viewport;
    OrthographicCamera camera;
    SpriteBatch s;
    
    BitmapFont font;
    
    Texture panelTexture;
    
    Bloom bloom;
    
    int rows = 2;
    int cols = 3;
    
    boolean[][] unlocked;
    
    Vector2 selectedPanel;
    
    public LevelSelectScreen(ScreenManager sm)
    {
        super(sm);
        
        
        //defaults to false
        unlocked = new boolean[cols][rows];
        unlocked[0][rows - 1] = true;
        selectedPanel = new Vector2(0, rows - 1);
                
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
        
        panelTexture = new Texture("ui/glassPanel_cornerTL.png");
        

        bloom = new Bloom(viewport, 1f);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        s.setProjectionMatrix(camera.combined);
        
        if(Gdx.input.isKeyJustPressed(Keys.LEFT))
        {
            selectedPanel.x--;
            if(selectedPanel.x < 0)
            {
                selectedPanel.x = 0;
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.RIGHT))
        {
            selectedPanel.x++;
            if(selectedPanel.x > cols - 1)
            {
                selectedPanel.x = cols - 1;
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.UP))
        {
            selectedPanel.y++;
            if(selectedPanel.y > rows - 1)
            {
                selectedPanel.y = rows - 1;
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.DOWN))
        {
            selectedPanel.y--;
            if(selectedPanel.y < 0)
            {
                selectedPanel.y = 0;
            }
        }
        //selectedPanel is in valid state by now
        if(Gdx.input.isKeyJustPressed(Keys.ENTER))
        {
            unlocked[(int)selectedPanel.x][(int)selectedPanel.y] = true;
        }        
        
        camera.position.x = Globals.WIDTH/2f;
        camera.position.y = Globals.HEIGHT/2f;
        camera.update();
        
        bloom.capture();
        s.begin();
        float w = 256;
        float h = 256;
        
        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                Color color = Color.MAGENTA.cpy();
                
                color.a =  0.55f;
                
                if(!unlocked[i][j])
                {
                    color.r = Color.RED.r;
                    color.g = Color.RED.g;
                    color.b = Color.RED.b;
                }
                if(selectedPanel.x == i && selectedPanel.y == j)
                {
                    color.a = 1f;
                }
                
                s.setColor(color);
                s.draw(panelTexture, i* Globals.WIDTH/cols + 192, j* Globals.HEIGHT/1.5f/rows + 64, w, h);
            }
        }
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