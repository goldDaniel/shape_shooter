
package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.BlackHole;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Obstacle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.Turret;
import com.golddaniel.main.*;

/**
 * @author wrksttn
 */
public class GameScreen extends VScreen
{
    WorldModel model;
    WorldRenderer renderer;
    
    BitmapFont font;
    SpriteBatch s;

    Skin uiSkin;
    Stage uiStage;

    OrthographicCamera uiCam;
    ExtendViewport uiViewport;

    TextureRegion tex;

    float gridSpacing = 0.25f;
    Label gridLabel;
    Label timeLabel;
    Label elapsed;

    PhysicsGrid g;

    boolean runSim;

    CameraInputController camController;

    public GameScreen(ScreenManager sm, Assets assets)
    {
        super(sm, assets);

        tex = new TextureRegion(new Texture("texture.png"));

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth  = 26;
        float worldHeight = 20;

        int waves = 128;
        for(int i = 0; i < waves; i++)
        {
            Array<Entity> toAdd = new Array<Entity>();


            float angle = (float)i/(float)waves*360f;

            toAdd.add(new Bouncer(
                            new Vector3(),
                            new Vector3(MathUtils.cos(angle), MathUtils.sin(angle), 0)));

            toSpawn.put((i+1), toAdd);
        }



        model = new WorldModel(worldWidth,worldHeight, toSpawn);


        model.addEntity(new Player());

        g = new PhysicsGrid(
                            new Vector2(model.WORLD_WIDTH,
                                        model.WORLD_HEIGHT),
                                        gridSpacing);



        model.setGrid(g);

        camController = new CameraInputController(model.getCamera());
        renderer = new WorldRenderer(model);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        font = generator.generateFont(parameter); 
        generator.dispose(); 
        
        
        s = new SpriteBatch();

        float vWidth;
        float vHeight;
        if(SharedLibraryLoader.isAndroid)
        {
            vWidth = 600;
            vHeight = 800;
        }
        else
        {
            vWidth = 800;
            vHeight = 600;
        }

        uiCam = new OrthographicCamera(vWidth, vHeight);
        uiViewport = new ExtendViewport(vWidth, vHeight, uiCam);
        uiCam.position.x = 0f;
        uiCam.position.y = 0f;
        uiViewport.apply();

        uiStage = new Stage(uiViewport);
        uiSkin = new Skin(Gdx.files.internal("ui/neon/skin/neon-ui.json"));
        buildEditorUI(uiStage, uiSkin);
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

        timeLabel = new Label("TIMESCALE: " + Globals.TIMESCALE, skin);
        final Slider timeSlider = new Slider(0.1f, 1.5f, 0.1f, false, skin);
        timeSlider.setAnimateDuration(0.1f);
        timeSlider.setValue(1);
        timeSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                    Globals.TIMESCALE = MathUtils.round(timeSlider.getValue() * 10f) / 10f;
                    timeLabel.setText("TIMESCALE: " + Globals.TIMESCALE);
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
        };
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
            Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, camController));

            uiStage.act();

            if(runSim)
            {
                model.update(delta);
                CollisionSystem.update(model);
            }
            elapsed.setText("ELAPSED TIME: " + model.getElapsedTime());

            s.setProjectionMatrix(uiCam.combined);
            uiStage.getViewport().getCamera().position.set(uiViewport.getWorldWidth()  / 2f,
                                                           uiViewport.getWorldHeight() / 2f,
                                                           1);

            renderer.draw(model);
            uiStage.draw();
        }
        else
        {
            Gdx.input.setInputProcessor(null);

            model.update(delta);
            CollisionSystem.update(model);
            renderer.draw(model);
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
