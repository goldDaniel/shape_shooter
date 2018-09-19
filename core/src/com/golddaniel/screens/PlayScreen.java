
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class PlayScreen extends VScreen implements MessageListener
{

    LevelModel level;
    WorldRenderer renderer;

    CollisionSystem cSystem;
    AudioSystem aSystem;
    
    BitmapFont font;
    SpriteBatch s;
    
    OrthographicCamera uiCam;
    FitViewport uiViewport;
    
    public PlayScreen(ScreenManager sm)
    {
        super(sm);
        
        
        level = new LevelModel(Gdx.files.internal("levels/testLevel_1.lvl"));
        renderer = new WorldRenderer();
        cSystem = new CollisionSystem();
        aSystem = new AudioSystem();
        
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        font = generator.generateFont(parameter); 
        generator.dispose(); 
        
        
        s = new SpriteBatch();
        
        uiCam = new OrthographicCamera();
        uiViewport = new FitViewport(Globals.WIDTH, Globals.HEIGHT, uiCam);
        uiCam.position.x = Globals.WIDTH  / 2;
        uiCam.position.y = Globals.HEIGHT / 2;
        uiViewport.apply();
        
        Messenger.startNotifying();
    }

    
    @Override
    public void render(float delta)
    {   
        level.update(delta);
        cSystem.update(level.getWorldModel());
        renderer.draw(level.getWorldModel());
        
        s.setProjectionMatrix(uiViewport.getCamera().combined);
        
        //draw UI 
        s.begin();
        
        font.draw(s, 
                  "REMAINGING TIME: " + level.getRemainingTime(), 
                  0, Globals.HEIGHT);
        font.draw(s, 
                  "SCORE: " + level.getScore(), 
                  0, 
                  Globals.HEIGHT - 64f);
        
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
        uiViewport.update(width, height);
        renderer.resize(width, height);
    }

    @Override
    public void onNotify(Messenger.EVENT event)
    {
      
    }
}
