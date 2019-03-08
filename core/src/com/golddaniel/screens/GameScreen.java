
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
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class GameScreen extends VScreen
{
    private WorldModel model;
    private UIRenderer uiRenderer;
    private WorldRenderer worldRenderer;

    private float gridSpacing = 0.4f;
    private boolean runSim;
    private float gameRestart = 5f;


    private ShapeRenderer sh;

    private void buildTestWorld()
    {
        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 16;
        float worldHeight = 9;

        float levelTime = 90f;

        model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);

        for (int i = 0; i < 20; i += 4)
        {
            Array<Entity> toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toSpawn.put(i, toAdd);
        }

        Array<Entity> toAdd = new Array<Entity>();

        for (int i = 0; i < worldHeight / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f, -worldHeight / 2f + 2 * (i + 1), 0),
                    new Vector3(1, 0, 0),
                    assets));
        }
        toSpawn.put(24, toAdd);
        toAdd = new Array<Entity>();

        for (int i = 0; i < worldHeight / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector3(worldWidth / 2f, -worldHeight / 2f + 2 * (i), 0),
                    new Vector3(-1, 0, 0),
                    assets));
        }
        toSpawn.put(32, toAdd);

        toAdd = new Array<Entity>();


        for (int i = 0; i < worldWidth / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f + 2 * (i), -worldHeight / 2f, 0),
                    new Vector3(0, 1, 0),
                    assets));
        }
        toSpawn.put(42, toAdd);

        for (int i = 0; i < worldWidth / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f + 2 * (i), worldHeight / 2f, 0),
                    new Vector3(0, -1, 0),
                    assets));
        }
        toSpawn.put(46, toAdd);

        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));
        toSpawn.put(54, toAdd);

        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
        toAdd.add(new Cuber(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));
        toSpawn.put(62, toAdd);

        for (int i = 0; i < 30; i += 4)
        {
            toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3(worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));


            toSpawn.put(68 + i, toAdd);
        }

        PhysicsGrid g = new PhysicsGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                gridSpacing);

        model.setGrid(g);
    }

    private void init()
    {

        buildTestWorld();
        uiRenderer = new UIRenderer(model, assets);
        worldRenderer = new WorldRenderer(model.getCamera(), assets);
    }

    public GameScreen(ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
        sh = new ShapeRenderer();
        init();
    }



    @Override
    public void render(float delta)
    {
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
            }
            else
            {
                if (gameRestart <= 0)
                {
                    gameRestart = 5f;
                    AudioSystem.stopMusic();

                    dispose();
                    init();
                    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                    sm.setScreen(ScreenManager.STATE.MAIN_MENU);
                }
                else
                {
                    gameRestart -= delta;
                }
            }
            worldRenderer.draw(model);
            uiRenderer.update(model);
            uiRenderer.draw(model);
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
        model.dispose();
        uiRenderer.dispose();
        worldRenderer.dispose();
    }

    @Override
    public void resize(int width, int height)
    {
        uiRenderer.resize(width, height);
        worldRenderer.resize(width, height);
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
