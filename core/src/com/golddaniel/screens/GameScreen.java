
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
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Multiplier;
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

    private float gridSpacing = 0.3f;
    private Label gridLabel;
    private Label timeLabel;
    private Label elapsed;

    private Label timerLabel;
    private Label scoreLabel;
    private Label multiplierLabel;

    private PhysicsGrid g;

    private boolean runSim;

    private CameraInputController camController;

    public GameScreen(ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);

        Skin uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);


        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth  = 15;
        float worldHeight = 10;

        float levelTime = 120f;

        model = new WorldModel(worldWidth,worldHeight, toSpawn, levelTime);


        for(int i = 0; i < 120; i += 4)
        {
            Array<Entity> toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector3( worldWidth / 2f, 0, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, 0, 0), assets));
            toAdd.add(new Boid(new Vector3( worldWidth / 2f, 0, 0), assets));
            toAdd.add(new Boid(new Vector3(-worldWidth / 2f, 0, 0), assets));

            toSpawn.put(i, toAdd);
        }

        for(int i = 0; i < 120; i += 12)
        {
            Array<Entity> toAdd = new Array<Entity>();

            toAdd.add(new Bouncer(
                            new Vector3(-worldWidth / 2f, worldHeight / 2f, 0),
                            new Vector3(1, -1, 0),
                            assets));

            toAdd.add(new Bouncer(
                            new Vector3(worldWidth / 2f, worldHeight / 2f, 0),
                            new Vector3(-1, -1, 0),
                            assets));

            toAdd.add(new Bouncer(
                            new Vector3(-worldWidth / 2f, -worldHeight / 2f, 0),
                            new Vector3(1, 1, 0),
                            assets));

            toAdd.add(new Bouncer(
                            new Vector3(worldWidth / 2f, -worldHeight / 2f, 0),
                            new Vector3(-1, 1, 0),
                            assets));

            toSpawn.put(i, toAdd);
        }

        model.addEntity(new Player(assets));

        g = new PhysicsGrid(
                            new Vector2(model.WORLD_WIDTH,
                                        model.WORLD_HEIGHT),
                            gridSpacing);

        model.setGrid(g);

        camController = new CameraInputController(model.getCamera());
        renderer = new WorldRenderer(model, assets);

        Bullet.loadTextures(assets);
        Multiplier.loadTextures(assets);

        uiDebugViewport = new ExtendViewport(800, 600);
        uiDebugViewport.apply();

        uiDebugStage = new Stage(uiDebugViewport);

        buildEditorUI(uiDebugStage, uiSkin);

        uiStage = new Stage(uiDebugViewport);
        Touchpad leftPad = new Touchpad(0.1f, uiSkin);
        Touchpad rightPad = new Touchpad(0.1f, uiSkin);

        float size = 256;
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

        leftPad.setPosition(64,64);
        rightPad.setPosition(uiStage.getWidth() - size - 64, 64);

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

        uiStage.addActor(timerLabel);
        uiStage.addActor(scoreLabel);
        uiStage.addActor(multiplierLabel);
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
            if(model.getRemainingTime() > 0)
            {
                Gdx.input.setInputProcessor(uiStage);

                uiStage.act();
                model.update(delta);
                CollisionSystem.update(model);


                if (model.getPlayer() == null)
                {
                    model.addEntity(new Player(assets));
                }
            }
            renderer.draw(model);
            uiStage.getViewport().getCamera().position.set(
                    uiDebugViewport.getWorldWidth()  / 2f,
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
