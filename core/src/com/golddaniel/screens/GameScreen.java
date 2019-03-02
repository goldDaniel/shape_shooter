
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Bullet;
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class GameScreen extends VScreen
{
    private WorldModel model;
    private WorldRenderer renderer;

    private Stage uiDebugStage;
    private ExtendViewport uiDebugViewport;

    private Stage uiStage;

    private float gridSpacing = 0.35f;
    private Label gridLabel;
    private Label timeLabel;
    private Label elapsed;

    private Label timerLabel;
    private Label scoreLabel;
    private Label multiplierLabel;

    private Label endLabel;

    private PhysicsGrid g;

    private boolean runSim;

    private CameraInputController camController;

    private float gameRestart = 5f;

    private void buildTestWorld()
    {
        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth  = 12;
        float worldHeight = 8;

        float levelTime = 90f;

        model = new WorldModel(worldWidth,worldHeight, toSpawn, levelTime);

        for(int i = 0; i < 20; i += 4)
        {
            Array<Entity> toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0) , assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0) , assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0) , assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0) , assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toSpawn.put(i, toAdd);
        }

        Array<Entity> toAdd = new Array<Entity>();

        for(int i= 0; i < 4; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f, -worldHeight / 2f + 2*(i + 1), 0),
                    new Vector3(1, 0, 0),
                    assets));
        }
        toSpawn.put(24, toAdd);
        toAdd = new Array<Entity>();

        for(int i= 0; i < 4; i++)
        {
            toAdd.add(new Bouncer(new Vector3(worldWidth / 2f, -worldHeight / 2f + 2*(i), 0),
                    new Vector3(-1, 0, 0),
                    assets));
        }
        toSpawn.put(32, toAdd);

        toAdd = new Array<Entity>();


        for(int i= 0; i < 6; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f + 2*(i), -worldHeight / 2f, 0),
                    new Vector3(0, 1, 0),
                    assets));
        }
        toSpawn.put(42, toAdd);

        for(int i= 0; i < 6; i++)
        {
            toAdd.add(new Bouncer(new Vector3(-worldWidth / 2f + 2*(i), worldHeight / 2f, 0),
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

        for(int i = 0; i < 30; i += 4)
        {
            toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, -worldHeight / 2f, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0), assets));


            toSpawn.put(68 + i, toAdd);
        }

        g = new PhysicsGrid(
                new Vector2(model.WORLD_WIDTH,
                            model.WORLD_HEIGHT),
                gridSpacing);

        model.setGrid(g);
    }

    private void init()
    {
        Skin uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);

        buildTestWorld();

        camController = new CameraInputController(model.getCamera());

        Bullet.loadTextures(assets);
        Multiplier.loadTextures(assets);
        Player.loadTextures(assets);
        Particle.loadTextures(assets);
        AudioSystem.loadSounds(assets);

        uiDebugViewport = new ExtendViewport(800, 600);
        uiDebugViewport.apply();

        uiDebugStage = new Stage(uiDebugViewport);

        buildEditorUI(uiDebugStage, uiSkin);

        uiStage = new Stage(uiDebugViewport);
        Touchpad leftPad = new Touchpad(0.25f, uiSkin);
        Touchpad rightPad = new Touchpad(0.25f, uiSkin);

        float size = 192;
        leftPad.setSize(size, size);
        rightPad.setSize(size, size);

        leftPad.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                if(model.getPlayer() != null)
                {
                    // This is run when anything is changed on this actor.
                    float deltaX = ((Touchpad) actor).getKnobPercentX();
                    float deltaY = ((Touchpad) actor).getKnobPercentY();
                    model.getPlayer().setMoveDir(deltaX, deltaY);
                }
            }
        });
        rightPad.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                if(model.getPlayer() != null)
                {
                    // This is run when anything is changed on this actor.
                    float deltaX = ((Touchpad) actor).getKnobPercentX();
                    float deltaY = ((Touchpad) actor).getKnobPercentY();
                    model.getPlayer().setShootDir(deltaX, deltaY);
                }
            }
        });

        leftPad.setPosition(96,96);
        rightPad.setPosition(uiStage.getWidth() - size - 96, 96);

        uiStage.addActor(leftPad);
        uiStage.addActor(rightPad);

        timerLabel = new Label("", uiSkin);
        timerLabel.setPosition(0, 600 - 32);
        timerLabel.setFontScale(2);

        scoreLabel = new Label("", uiSkin);
        scoreLabel.setPosition(0, 600 - 32 - 32);
        scoreLabel.setFontScale(2);

        multiplierLabel = new Label("", uiSkin);
        multiplierLabel.setPosition(0, 600 - 32 - 32 - 32);
        multiplierLabel.setFontScale(2);

        endLabel = new Label("LEVEL COMPLETE", uiSkin);
        endLabel.setPosition(800/2f, 600/2f);
        endLabel.setFontScale(2);
        endLabel.scaleBy(4f);

        uiStage.addActor(timerLabel);
        uiStage.addActor(scoreLabel);
        uiStage.addActor(multiplierLabel);

        TextButton powerUp = new TextButton("POWERUP", uiSkin);
        powerUp.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(model.getPlayer() != null)
                    model.getPlayer().applyPowerup();
            }
        });
        powerUp.setSize(120, 80);

        powerUp.setPosition(800-140, 600 - 100);
        uiStage.addActor(powerUp);

        renderer = new WorldRenderer(model.getViewport(), assets);
    }

    public GameScreen(ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
        init();
    }

    private void buildEditorUI(Stage stage, Skin skin)
    {
        Table table = new Table();
        table.setDebug(false);
        table.setFillParent(true);

        table.align(Align.topLeft);

        Label modeLabel = new Label("EDIT MODE", skin);
        elapsed = new Label("ELAPSED TIME: " + model.getElapsedTime(), skin);

        table.row();
        table.add(modeLabel).align(Align.center).padBottom(5f);
        table.add(elapsed).align(Align.center).padBottom(5f);
        table.row();


        gridLabel = new Label("SPACING: " + gridSpacing, skin);

        final Slider gridSlider = new Slider(0.05f, 1f, 0.05f, false, skin);
        gridSlider.setAnimateDuration(0.1f);
        gridSlider.setValue(gridSpacing);
        gridSlider.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                float spacing = gridSlider.getValue();

                gridSpacing = MathUtils.round(spacing * 100f) / 100f;

                g.dispose();
                g = new PhysicsGrid(new Vector2(model.WORLD_WIDTH, model.WORLD_HEIGHT), gridSpacing);
                model.setGrid(g);

                gridLabel.setText("SPACING: " + gridSpacing);
            }
        });
        table.add(gridSlider);
        table.add(gridLabel);

        table.row();

        timeLabel = new Label("TIMESCALE: " + model.TIMESCALE, skin);
        final Slider timeSlider = new Slider(0.1f, 1.5f, 0.1f, false, skin);
        timeSlider.setAnimateDuration(0.1f);
        timeSlider.setValue(1);
        timeSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                    model.TIMESCALE = MathUtils.round(timeSlider.getValue() * 10f) / 10f;
                    timeLabel.setText("TIMESCALE: " + model.TIMESCALE);
            }
        });

        table.add(timeSlider);
        table.add(timeLabel);

        final TextButton saveBtn = new TextButton(runSim + "", skin);
        saveBtn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                runSim = !runSim;
                saveBtn.setText(runSim + "");
            }
        });


        table.row();
        table.add(saveBtn).align(Align.top);

        stage.addActor(table);
    }

    @Override
    public void render(float delta)
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1))
        {
            model.editMode = !model.editMode;
            runSim = false;
        }

        if(model.editMode)
        {
            AudioSystem.pauseMusic();

            Gdx.input.setInputProcessor(new InputMultiplexer(uiDebugStage, camController));

            uiDebugStage.act();

            if(runSim)
            {
                model.update(delta);
                CollisionSystem.update(model);
            }
            uiDebugStage.getViewport().getCamera().position.set(
                                                           uiDebugViewport.getWorldWidth()  / 2f,
                                                           uiDebugViewport.getWorldHeight() / 2f,
                                                           1);
            renderer.draw(model);
            uiDebugStage.draw();
        }
        else
        {
            if (model.getRemainingTime() > 0)
            {
                AudioSystem.startMusic();
                Gdx.input.setInputProcessor(uiStage);

                uiStage.act();
                model.update(delta);
                CollisionSystem.update(model);
            }
            else
            {
                uiStage.addActor(endLabel);

                if(gameRestart <= 0)
                {
                    gameRestart = 5f;
                    AudioSystem.stopMusic();

                    init();
                    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                    sm.setScreen(ScreenManager.STATE.MAIN_MENU);
                }
                else
                {
                    gameRestart -= delta;
                }
            }
            renderer.draw(model);
            uiStage.getViewport().getCamera().position.set(
                    uiDebugViewport.getWorldWidth() / 2f,
                    uiDebugViewport.getWorldHeight() / 2f,
                    1);
            uiStage.draw();
        }

        elapsed.setText("ELAPSED TIME: " + (int)model.getElapsedTime());
        timerLabel.setText("REMAINING TIME: " + (int)model.getRemainingTime());
        scoreLabel.setText("SCORE: " + model.getScore());
        multiplierLabel.setText("MULTIPLIER: x" + model.getScoreMultiplier());

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
        renderer.dispose();
    }
    
    @Override
    public void resize(int width, int height)
    {
        uiDebugViewport.update(width, height);
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
