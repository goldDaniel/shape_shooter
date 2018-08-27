/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import bloom.Bloom;
import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.golddaniel.main.PhysicsGrid;

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
    
    PhysicsGrid g;
       
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
        unlocked = new boolean[rows][cols];
        unlocked[rows - 1][0] = true;
        selectedPanel = new Vector2(rows - 1, 0);
                
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
        
        int spacing = 32;
        g = new PhysicsGrid(new Vector2(Globals.WIDTH*2, Globals.HEIGHT*2), 
                Globals.WIDTH*2/spacing, Globals.HEIGHT*2/spacing);
        

        bloom = new Bloom(viewport, 1f);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        s.setProjectionMatrix(camera.combined);
        
        g.update(delta);
        
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
            unlocked[(int)selectedPanel.y][(int)selectedPanel.x] = true;
        }        
        
        Vector2 pos = new Vector2();
        pos.x = selectedPanel.x*Globals.WIDTH/cols + 128 +  192;
        pos.y = selectedPanel.y*Globals.HEIGHT/1.5f/rows + 128 + 64;
        
        g.applyRadialForce(pos, 4000f, 192);
        
        camera.position.x = Globals.WIDTH/2f;
        camera.position.y = Globals.HEIGHT/2f;
        camera.update();
        
        bloom.capture();
        s.begin();
        
        g.draw(s);
        
        float w = 256;
        float h = 256;
        
        
        for (int j = 0; j < cols; j++)
        {
            for (int i = 0; i < rows; i++)
            {
                Color color = Color.WHITE.cpy();
                color.a =  0.5f;
                
                if(!unlocked[i][j])
                {
                    color.r = 1f;
                    color.g = 0.1f;
                    color.b = 0.1f;
                }
                if(selectedPanel.x == j && selectedPanel.y == i)
                {
                    color.a = 1f;
                }
                
                s.setColor(color);
                s.draw(panelTexture, j* Globals.WIDTH/cols + 192, i* Globals.HEIGHT/1.5f/rows + 64, w, h);
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