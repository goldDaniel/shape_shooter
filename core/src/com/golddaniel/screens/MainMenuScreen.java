/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.golddaniel.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.golddaniel.entities.Particle;
import com.golddaniel.main.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author wrksttn
 */
public class MainMenuScreen extends VScreen
{
    private ScreenViewport titleViewport;
    private PerspectiveCamera titleCamera;

    private FitViewport viewport;
    private OrthographicCamera camera;


    private SpriteBatch s;

    private BitmapFont font;

    private Skin uiSkin;
    private Stage uiStage;

    float hue;

    float dist = 5000;

    private Pool<Particle> particlePool = new Pool<Particle>(2048)
    {
        @Override
        protected Particle newObject()
        {
            return new Particle(new Vector3(), new Vector3(), new Vector3(), 0, Color.WHITE, Color.WHITE);
        }
    };
    Array<Particle> particles = new Array<Particle>();

    public MainMenuScreen(final ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);
                
        camera = new OrthographicCamera(1920, 1080);
        viewport = new FitViewport(400, 200, camera);
        viewport.apply();

        titleCamera = new PerspectiveCamera(67, 1920, 1080);
        titleCamera.far = 5000f;
        titleViewport = new ScreenViewport(titleCamera);
        titleViewport.apply();

        s = new SpriteBatch();
        s.enableBlending();        


        font = assets.get("Square72.ttf", BitmapFont.class);
        uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);

        uiStage = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);


        uiStage.addActor(table);

        final TextButton playBtn = new TextButton("PLAY", uiSkin, "default");
        playBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                sm.setScreen(ScreenManager.STATE.LEVEL_SELECT);
            }
        });

        final TextButton quitBtn = new TextButton("QUIT", uiSkin, "default");
        quitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });

        final TextButton spacer = new TextButton("", uiSkin, "default");
        spacer.setDisabled(true);

        table.setPosition(0, -32);
        table.row();
        table.add(playBtn).fill();
        table.row();
        table.add(quitBtn).fill();
    }
    
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(uiStage);

        float distFinal = 150;
        if(dist > distFinal)
        {
            dist -= 2500f*delta;
        }
        else
        {
            dist = distFinal;
        }

        hue += 90*delta;


        if((int)hue % 5 == 0)
        {
            Vector3 pos = new Vector3(MathUtils.random(-1400, -200), MathUtils.random(-1300, -400), 0);

            float speed = MathUtils.random(600, 900);

            Vector3 velocity = new Vector3(speed, speed, 0f);
            Vector3 dim = new Vector3(50f, 1f, 1f);


            Particle p = particlePool.obtain();
            p.init(pos, velocity, dim, 10f, Color.WHITE, Color.WHITE);
            particles.add(p);
            Array<Particle> toRemove = new Array<Particle>();
            for(Particle a : particles)
            {
                if(!a.isAlive())
                {
                    particlePool.free(a);
                    toRemove.add(a);
                }
            }
            particles.removeAll(toRemove, true);
        }


        titleCamera.position.x = viewport.getWorldWidth()  / 2f;
        titleCamera.position.y = viewport.getWorldHeight() / 2f;
        titleCamera.position.z = dist;
        titleCamera.update();


        s.setProjectionMatrix(camera.combined);
        s.begin();
        for(Particle a : particles)
        {
            a.update(null, delta);
            a.draw(s);
        }
        s.end();

        if(MathUtils.isEqual(dist, distFinal))
        {
            camera.position.x = viewport.getWorldWidth() / 2f;
            camera.position.y = viewport.getWorldHeight() / 2f;
            camera.position.z = 1f;
            camera.update();
            uiStage.act();
            uiStage.draw();
        }


        s.setProjectionMatrix(titleCamera.combined);
        s.begin();
        font.setColor(Color.WHITE.cpy().fromHsv(hue, 1f, 1f));
        font.draw(s, "SHAPE SHOOTER", titleCamera.position.x - 172, titleCamera.position.y + 84);
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
        viewport.update(width, height);
        viewport.apply();

        titleViewport.update(width, height);
        titleViewport.apply();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override 
    public void dispose()
    {
        s.dispose();
    }
}