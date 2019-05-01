package com.golddaniel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.golddaniel.main.LevelBuilder;
import com.golddaniel.main.ScreenManager;
import com.sun.java.accessibility.util.TopLevelWindowListener;

public class LevelSelectScreen extends VScreen
{

    private Texture texture0;
    private Texture texture1;
    private Texture texture2;
    private Texture texture3;
    private Texture texture4;
    private Texture texture5;
    private Texture texture6;
    private Texture texture7;
    private Texture texture8;
    private Texture texture9;

    Texture panelTexture;

    SpriteBatch s;

    OrthographicCamera cam;
    FitViewport view;

    BitmapFont font;

    boolean selected = false;

    private enum SelectedLevel
    {
        L_1,
        L_2,
        L_3,
        L_4,
        L_5,
        L_6,
    }

    SelectedLevel level;


    /**
     * @param sm
     * @param assets
     */
    public LevelSelectScreen(final ScreenManager sm, AssetManager assets)
    {
        super(sm, assets);

        panelTexture = assets.get("ui/panel.png", Texture.class);

        s = new SpriteBatch();

        cam = new OrthographicCamera(1920, 1080);
        view = new FitViewport(1920, 1080, cam);


        texture0 = assets.get("textTextures/0.png", Texture.class);
        texture1 = assets.get("textTextures/1.png", Texture.class);
        texture2 = assets.get("textTextures/2.png", Texture.class);
        texture3 = assets.get("textTextures/3.png", Texture.class);
        texture4 = assets.get("textTextures/4.png", Texture.class);
        texture5 = assets.get("textTextures/5.png", Texture.class);
        texture6 = assets.get("textTextures/6.png", Texture.class);
        texture7 = assets.get("textTextures/7.png", Texture.class);
        texture8 = assets.get("textTextures/8.png", Texture.class);
        texture9 = assets.get("textTextures/9.png", Texture.class);


        font = assets.get("Square72.ttf", BitmapFont.class);
    }


    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.position.x = view.getWorldWidth() / 2f;
        cam.position.y = view.getWorldHeight() / 2f;
        cam.update();
        s.setProjectionMatrix(cam.combined);
        s.enableBlending();
        s.begin();

        float panelWidth = panelTexture.getWidth()*3f;
        float panelHeight = panelTexture.getHeight()*3f;
        float sub = panelWidth / 2f;


        Vector2 input = new Vector2();
        if(Gdx.input.getDeltaX() > 0 || Gdx.input.getDeltaY() > 0)
        {
            input = view.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        }

        Rectangle mouse = new Rectangle(
                input.x,
                input.y,
                1, 1);


        Rectangle rect1 = new Rectangle(
                view.getWorldWidth()  / 4f - sub,
                view.getWorldHeight() * 3f / 5f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_1)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect1.x, rect1.y, rect1.width, rect1.height);
        s.setColor(Color.WHITE);

        s.draw( texture1,
                rect1.x + rect1.width / 4f,
                rect1.y + rect1.height / 4f,
                rect1.width / 2f,
                rect1.height /2f);


        Rectangle rect2 = new Rectangle(
                view.getWorldWidth() * 2f / 4f - sub,
                view.getWorldHeight() * 3f / 5f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_2)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect2.x, rect2.y, rect2.width, rect2.height);
        s.setColor(Color.WHITE);

        s.draw( texture2,
                rect2.x + rect2.width / 4f,
                rect2.y + rect2.height / 4f,
                rect2.width / 2f,
                rect2.height /2f);

        Rectangle rect3 = new Rectangle(
                view.getWorldWidth() * 3f / 4f - sub,
                view.getWorldHeight() * 3f / 5f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_3)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect3.x, rect3.y, rect3.width, rect3.height);
        s.setColor(Color.WHITE);

        s.draw( texture3,
                rect3.x + rect3.width / 4f,
                rect3.y + rect3.height / 4f,
                rect3.width / 2f,
                rect3.height / 2f);


        Rectangle rect4 = new Rectangle(
                view.getWorldWidth() / 4f - sub,
                view.getWorldHeight() / 4f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_4)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect4.x, rect4.y, rect4.width, rect4.height);
        s.setColor(Color.WHITE);

        s.draw( texture4,
                rect4.x + rect4.width / 4f,
                rect4.y + rect4.height / 4f,
                rect4.width / 2f,
                rect4.height / 2f);


        Rectangle rect5 = new Rectangle(
                view.getWorldWidth() * 2 / 4f - sub,
                view.getWorldHeight() / 4f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_5)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect5.x, rect5.y, rect5.width, rect5.height);
        s.setColor(Color.WHITE);

        s.draw( texture5,
                rect5.x + rect5.width / 4f,
                rect5.y + rect5.height / 4f,
                rect5.width / 2f,
                rect5.height / 2f);


        Rectangle rect6 = new Rectangle(
                view.getWorldWidth() * 3 / 4f - sub,
                view.getWorldHeight() / 4f,
                panelWidth, panelHeight);

        if(level == SelectedLevel.L_6)
        {
            s.setColor(Color.RED);
        }
        s.draw(panelTexture, rect6.x, rect6.y, rect6.width, rect6.height);
        s.setColor(Color.WHITE);

        s.draw( texture6,
                rect6.x + rect6.width / 4f,
                rect6.y + rect6.height / 4f,
                rect6.width / 2f,
                rect6.height / 2f);


        font.setColor(Color.CYAN);
        font.draw(s, "SELECT A LEVEL", view.getWorldWidth() / 2f - 200, view.getWorldHeight() - 50);

        s.end();

        if(Gdx.input.isTouched())
        {
            if(!selected)
            {
                if (mouse.contains(rect1) || rect1.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel1(assets);
                    level = SelectedLevel.L_1;
                }
                else if (mouse.contains(rect2) || rect2.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel2(assets);
                    level = SelectedLevel.L_2;
                }
                else if (mouse.contains(rect3) || rect3.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel3(assets);
                    level = SelectedLevel.L_3;
                }
                else if (mouse.contains(rect4) || rect4.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel4(assets);
                    level = SelectedLevel.L_4;
                }
                else if (mouse.contains(rect5) || rect5.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel5(assets);
                    level = SelectedLevel.L_5;
                }
                else if (mouse.contains(rect6) || rect6.contains(mouse))
                {
                    selected = true;
                    LevelBuilder.buildLevel6(assets);
                    level = SelectedLevel.L_6;
                }
            }
        }

        if(LevelBuilder.isLoaded())
        {
            selected = false;
            level = null;
            sm.setScreen(ScreenManager.STATE.PLAY);
        }
    }

    @Override
    public void show()
    {

    }

    @Override
    public void resize(int width, int height)
    {
        view.update(width, height, false);
        view.apply();
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
    public void hide()
    {

    }
}
