
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Player;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class PlayScreen extends VScreen
{

    WorldModel model;
    WorldRenderer renderer;
    
    BitmapFont font;
    SpriteBatch s;
    
    OrthographicCamera uiCam;
    FitViewport uiViewport;

    TextureRegion tex = new TextureRegion(new Texture("texture.png"));


    public PlayScreen(ScreenManager sm)
    {
        super(sm);
        
        
        model = new WorldModel(0.46f, 0.82f);

        model.addEntity(new Player());

        PhysicsGrid g = new PhysicsGrid(
                new Vector2(model.WORLD_WIDTH,
                            model.WORLD_HEIGHT),
                0.025f);
        model.setGrid(g);

        for(int i = 0; i < 20; i++)
        {
            model.addEntity(new Boid(new Vector3(-0.25f, 1.25f, 0)));
            model.addEntity(new Boid(new Vector3( 0.25f, 1.25f, 0)));
        }

        renderer = new WorldRenderer(model);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        font = generator.generateFont(parameter); 
        generator.dispose(); 
        
        
        s = new SpriteBatch();
        
        uiCam = new OrthographicCamera();
        uiViewport = new FitViewport(9, 16, uiCam);
        uiCam.position.x = 0f;
        uiCam.position.y = 0f;
        uiViewport.apply();
    }

    @Override
    public void render(float delta)
    {
        Gdx.input.setInputProcessor(null);

        model.update(delta);
        CollisionSystem.update(model);


        renderer.draw(model);
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
}
