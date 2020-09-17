package com.golddaniel.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.core.world.WorldModel;

//TODO: i really have to rework particles
public class TextParticle extends Entity
{
    private Vector2 pos;

    private String multiplier;

    private float timer = 1.2f;

    private float size;

    private static Texture textureX;
    private static Texture texture0;
    private static Texture texture1;
    private static Texture texture2;
    private static Texture texture3;
    private static Texture texture4;
    private static Texture texture5;
    private static Texture texture6;
    private static Texture texture7;
    private static Texture texture8;
    private static Texture texture9;

    public static void loadTextures(AssetManager assets)
    {

        textureX = assets.get("textTextures/x.png", Texture.class);
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
    }

    public TextParticle(String num, Vector2 pos)
    {
        this.pos = pos.cpy();
        multiplier = num;
    }

    public void update(WorldModel model, float delta)
    {
        pos.y += 1.5f * delta;

        timer -= delta;

        size = 0.25f - 0.15f * timer;

        if(timer <= 0) isAlive = false;
    }

    public void draw(SpriteBatch s)
    {
        s.setColor(Color.ORANGE);

        for(int i = 0; i < multiplier.length(); i++)
        {
            Texture tex = null;
            if(multiplier.charAt(i) == 'x')
            {
                tex =  textureX;
            }
            else if(multiplier.charAt(i) == '0')
            {
                tex = texture0;
            }
            else if(multiplier.charAt(i) == '1')
            {
                tex = texture1;
            }
            else if(multiplier.charAt(i) == '2')
            {
                tex = texture2;
            }
            else if(multiplier.charAt(i) == '3')
            {
                tex = texture3;
            }
            else if(multiplier.charAt(i) == '4')
            {
                tex = texture4;
            }
            else if(multiplier.charAt(i) == '5')
            {
                tex = texture5;
            }
            else if(multiplier.charAt(i) == '6')
            {
                tex = texture6;
            }
            else if(multiplier.charAt(i) == '7')
            {
                tex = texture7;
            }
            else if(multiplier.charAt(i) == '8')
            {
                tex = texture8;
            }
            else if(multiplier.charAt(i) == '9')
            {
                tex = texture9;
            }

            s.draw(tex, pos.x + size*i, pos.y, size, size);
        }
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        isAlive = false;
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return null;
    }

    public void dispose()
    {

    }
}
