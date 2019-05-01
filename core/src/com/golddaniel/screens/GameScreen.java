
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.TextParticle;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class GameScreen extends VScreen
{
    private WorldModel model;
    private UIRenderer uiRenderer;
    private WorldRenderer worldRenderer;
    private boolean runSim;
    private float gameRestart = 5f;


    private ShapeRenderer sh;

    private boolean finishedLoading = false;

    public GameScreen(ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
        sh = new ShapeRenderer();
        Bullet.loadTextures(assets);
        Multiplier.loadTextures(assets);
        Player.loadTextures(assets);
        Particle.loadTextures(assets);
        AudioSystem.loadSounds(assets);
        Boid.loadTextures(assets);
        TextParticle.loadTextures(assets);
    }


    @Override
    public void render(float delta)
    {
        if(model == null)
        {
            model = LevelBuilder.getWorldModel();

            uiRenderer = new UIRenderer(model, assets);
            worldRenderer = new WorldRenderer(model.getCamera(), assets);

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            finishedLoading = true;
        }

        if(!finishedLoading) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1))
        {
            model.editMode = !model.editMode;
            runSim = false;
        }

        if (model.editMode)
        {
            AudioSystem.pauseMusic();
            if (runSim)
            {

                model.update(delta);
                CollisionSystem.update(model);
            }
            worldRenderer.draw(model);
        }

        else
        {
            Gdx.input.setInputProcessor(uiRenderer.getStage());
            if (model.getRemainingTime() > 0)
            {
                AudioSystem.startMusic();

                model.update(delta);
                CollisionSystem.update(model);
                worldRenderer.draw(model);
                uiRenderer.update(model);
                uiRenderer.draw(model);
            }
            else
            {
                if (gameRestart <= 0)
                {
                    gameRestart = 5f;
                    AudioSystem.stopMusic();

                    dispose();
                    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                    sm.setScreen(ScreenManager.STATE.MAIN_MENU);
                }
                else
                {
                    gameRestart -= delta;
                    worldRenderer.draw(model);
                    uiRenderer.update(model);
                    uiRenderer.draw(model);
                }
            }

        }

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
    public void dispose()
    {
        if(model != null) model.dispose();
        if(uiRenderer != null) uiRenderer.dispose();
        if(worldRenderer != null) worldRenderer.dispose();

        model = null;
        uiRenderer = null;
        worldRenderer = null;

        gameRestart = 5f;
        finishedLoading = false;
        LevelBuilder.resetWorldModel();
    }

    @Override
    public void resize(int width, int height)
    {
        //these rely on worldmodel existing
        if(model != null)
        {
            uiRenderer.resize(width, height);
            worldRenderer.resize(width, height);
        }
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
