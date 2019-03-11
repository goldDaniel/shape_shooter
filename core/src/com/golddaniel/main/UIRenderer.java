package com.golddaniel.main;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class UIRenderer
{
    private Stage uiStage;

    private Label timerLabel;
    private Label scoreLabel;
    private Label multiplierLabel;

    private Label endLabel;


    public UIRenderer(final WorldModel model, AssetManager assets)
    {
        Skin uiSkin = assets.get("ui/neon/skin/neon-ui.json", Skin.class);

        uiStage = new Stage(new ExtendViewport(800, 600));
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
                    model.getPlayer().setMoveDir(deltaX, deltaY);
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
                    model.getPlayer().setShootDir(deltaX, deltaY);
                }
            }
        });

        leftPad.setPosition(96, 96);
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
        endLabel.setPosition(800 / 2f, 600 / 2f);
        endLabel.setFontScale(2);
        endLabel.scaleBy(4f);
        endLabel.setVisible(false);


        uiStage.addActor(timerLabel);
        uiStage.addActor(scoreLabel);
        uiStage.addActor(multiplierLabel);
        uiStage.addActor(endLabel);
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
        else
        {
            endLabel.setVisible(true);
        }
        uiStage.act();
    }

    public void draw(WorldModel model)
    {
        uiStage.draw();
    }

    public void dispose()
    {
        uiStage.dispose();
    }

    public void resize(int width, int height)
    {

    }
}
