
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.BlackHole;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.Turret;
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

    TextureRegion tex;

    float startTimer = 3f;

    public PlayScreen(ScreenManager sm, Assets assets)
    {
        super(sm, assets);

        tex = new TextureRegion(new Texture("texture.png"));
        
        model = new WorldModel(27,12);

        model.addEntity(new Player());

        PhysicsGrid g = new PhysicsGrid(
                            new Vector2(model.WORLD_WIDTH,
                                        model.WORLD_HEIGHT),
                    0.15f);
        model.setGrid(g);

        for(int i = 0; i < 50; i++)
        {
            model.addEntity(new Boid(new Vector3(MathUtils.random(-5f, 5f), 5f, 0f)));
        }

        renderer = new WorldRenderer(model);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 512;
        font = generator.generateFont(parameter); 
        generator.dispose(); 
        
        
        s = new SpriteBatch();
        
        uiCam = new OrthographicCamera();
        uiViewport = new FitViewport(1080, 1920, uiCam);
        uiCam.position.x = 0f;
        uiCam.position.y = 0f;
        uiViewport.apply();
    }

    @Override
    public void render(float delta)
    {
        Gdx.input.setInputProcessor(null);

        if(startTimer > -1)
        {
            startTimer -= delta;
        }
        else
        {
            model.update(delta);
            CollisionSystem.update(model);
        }

        renderer.draw(model);

        font.setColor(Color.CYAN);
        s.setProjectionMatrix(uiCam.combined);
        s.begin();
        if(startTimer > 2)
        {
            //draw 3
            font.draw(s, "3", -256, 0);
        }
        else if(startTimer > 1)
        {
            //draw 2
            font.draw(s, "2", -256, 0);
        }
        else if(startTimer > 0)
        {
            //draw 1
            font.draw(s, "I", -256, 0);
        }
        else if(startTimer > -1)
        {
            font.draw(s, "-GO-", -256, 0);
        }
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
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }
}
