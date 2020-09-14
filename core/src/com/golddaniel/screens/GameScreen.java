
package com.golddaniel.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.core.input.AndroidInputController;
import com.golddaniel.core.input.InputConfig;
import com.golddaniel.core.input.KeyboardInputController;
import com.golddaniel.core.input.PlayerInputController;
import com.golddaniel.core.world.CollisionSystem;
import com.golddaniel.core.world.WorldModel;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.TextParticle;
import com.golddaniel.core.*;

/**
 * @author wrksttn
 */
public class GameScreen extends VScreen
{
    private PlayerInputController playerInput;
    private WorldModel model;
    private UIRenderer uiRenderer;
    private WorldRenderer worldRenderer;
    private float gameRestart = 5f;


    public GameScreen(ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
        Bullet.loadTextures(assets);
        Multiplier.loadTextures(assets);
        Player.loadTextures(assets);
        Particle.loadTextures(assets);
        AudioSystem.loadSounds(assets);
        Boid.loadTextures(assets);
        TextParticle.loadTextures(assets);
        Bouncer.LoadTextures(assets);
        Cuber.LoadTextures(assets);
    }


    @Override
    public void render(float delta)
    {
        if(model == null)
        {
            model = LevelBuilder.getWorldModel();


            InputConfig playerInputKeys = new InputConfig(
                                            Input.Keys.A,
                                            Input.Keys.D,
                                            Input.Keys.W,
                                            Input.Keys.S,
                                            Input.Keys.LEFT,
                                            Input.Keys.RIGHT,
                                            Input.Keys.UP,
                                            Input.Keys.DOWN);

            if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            {
                playerInput = new KeyboardInputController(model.getPlayer(), playerInputKeys);
            }
            if(Gdx.app.getType() == Application.ApplicationType.Android)
            {
                playerInput = new AndroidInputController(model.getPlayer());
            }

            uiRenderer = new UIRenderer(model, assets);
            worldRenderer = new WorldRenderer(model, assets);

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            InputMultiplexer input = new InputMultiplexer(playerInput,
                    uiRenderer.getStage());
            Gdx.input.setInputProcessor(input);
        }
        else
        {
            if (model.getRemainingTime() > 0)
            {
                AudioSystem.startMusic();

                playerInput.update();

                model.update(delta);
                CollisionSystem.update(model);


                worldRenderer.draw(delta);
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
                    Array<Entity> entities = model.getAllEntities();

                    model.getPlayer().kill(model);

                    for(Entity e : entities)
                    {
                        e.kill();


                        int particles = 64;
                        Vector2 velocity = new Vector2();
                        Vector2 dim = new Vector2(1.f, 0.1f);
                        float hue = MathUtils.random(360f);
                        Color color = new Color().fromHsv(hue, 1.f, 1.f);

                        model.applyRadialForce(e.position, 16, 1, new Color().fromHsv(360f - hue, 1.f, 1.f));

                        for (int i = 0; i < particles; i++)
                        {
                            float angle = (float)i/(float)particles*360f;

                            float speed = MathUtils.random(14f, 22f);

                            model.createParticle(
                                    e.position,
                                    velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                                    dim,
                                    MathUtils.random(0.5f, 1.5f),
                                    color,
                                    Color.WHITE);

                            speed = MathUtils.random(8f, 12f);

                            model.createParticle(
                                    e.position,
                                    velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                                    dim,
                                    MathUtils.random(0.5f, 1.5f),
                                    color,
                                    Color.ORANGE);
                        }
                    }
                    model.update(delta);

                    gameRestart -= delta;
                    worldRenderer.draw(delta);
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
        LevelBuilder.resetWorldModel();
    }

    @Override
    public void resize(int width, int height)
    {
        //these rely on worldmodel existing, bad design but
        //I have to finish the game eventually
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
