package com.golddaniel.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.golddaniel.core.input.AndroidInputController;
import com.golddaniel.core.world.WorldModel;

public class UIRenderer
{
    private Stage uiStage;

    private Label timerLabel;
    private Label scoreLabel;
    private Label multiplierLabel;

    BitmapFont endFont;

    public UIRenderer(final WorldModel model, AssetManager assets)
    {
        Skin uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);

        endFont = assets.get("Square72.ttf", BitmapFont.class);

        uiStage = new Stage(new ExtendViewport(800, 600));

        if(SharedLibraryLoader.isAndroid)
        {
            Touchpad leftPad = new Touchpad(0.25f, uiSkin);
            Touchpad rightPad = new Touchpad(0.25f, uiSkin);

            float size = 216;
            leftPad.setSize(size, size);
            rightPad.setSize(size, size);

            leftPad.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    if (model.getPlayer() != null)
                    {
                        // This is run when anything is changed on this actor.
                        float deltaX = ((Touchpad) actor).getKnobPercentX();
                        float deltaY = ((Touchpad) actor).getKnobPercentY();
                        AndroidInputController.moveDir.set(deltaX, deltaY);
                    }
                }
            });
            rightPad.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    if (model.getPlayer() != null)
                    {
                        // This is run when anything is changed on this actor.
                        float deltaX = ((Touchpad) actor).getKnobPercentX();
                        float deltaY = ((Touchpad) actor).getKnobPercentY();
                        AndroidInputController.shootDir.set(deltaX, deltaY);
                    }
                }
            });

            leftPad.setPosition(96, 96);
            rightPad.setPosition(800 - 88, 96);
            uiStage.addActor(leftPad);
            uiStage.addActor(rightPad);
        }

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

    public Stage getStage()
    {
        return uiStage;
    }

    public void update(WorldModel model)
    {
        if(model.getRemainingTime() > 0)
        {
            timerLabel.setText("REMAINING TIME: " + (int)model.getRemainingTime());
            scoreLabel.setText("SCORE: " + model.getScore());
            multiplierLabel.setText("MULTIPLIER: " + model.getScoreMultiplier());
        }
        uiStage.act();
    }

    public void draw(WorldModel model)
    {
        uiStage.draw();

        if(model.getRemainingTime() <= 0)
        {
            uiStage.getBatch().setColor(Color.WHITE);
            uiStage.getBatch().begin();
            endFont.draw(uiStage.getBatch(), "LEVEL COMPLETE",
                         uiStage.getWidth() / 3, uiStage.getHeight() / 2);
            uiStage.getBatch().end();
        }
    }

    public void dispose()
    {
        uiStage.dispose();
    }

    public void resize(int width, int height)
    {

    }
}
