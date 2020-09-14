package com.golddaniel.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.core.world.WorldModel;

public class Multiplier extends Entity
{
    Vector2 velocity;

    static TextureRegion tex;

    float width;
    float height;
    float angle = 0;

    float lifespan = 5f;

    boolean inRangeOfPlayer = false;

    Color color;

    public Multiplier(Vector2 pos, Vector2 vel)
    {
        init(pos, vel);
        width = 0.20f;
        height = width / 2f;

        color = Color.LIME.cpy();
        color.a = 1.f;
    }

    public void init(Vector2 pos, Vector2 vel)
    {
        this.position = pos;
        this.velocity = vel;
        isAlive = true;
    }

    public static void loadTextures(AssetManager assets)
    {
        if(tex == null)
            tex = new TextureRegion(assets.get("texture.png", Texture.class));
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        lifespan -= delta;
        angle += velocity.len()*360f*delta;

        Player p  = model.getPlayer();

        //this block is here, because once we are in range, the
        //multiplier will always move towards player, even once
        //they have left range
        if(lifespan < 4f)
        {
            if (p.position.dst2(position) < 2f*2f) inRangeOfPlayer = true;
        }
        if(inRangeOfPlayer)
        {
            Vector2 dir = p.position.cpy().sub(position);
            dir.nor().scl(velocity.len());
            velocity.set(dir);

            //increase acceleration the closer we are to the player
            Vector2 toAdd = velocity.cpy().nor();
            toAdd.scl(25f * delta * 1f / p.position.dst(position));
            velocity.add(toAdd);


            Vector2 dim = new Vector2(0.07f, 0.07f);
            Vector2 pos = position.cpy();
            pos.sub(-width/2f, -height/2f);
            model.createParticle(
                    pos,
                    Vector2.Zero,
                    dim,
                    MathUtils.random(0.1f, 0.5f),
                    Color.LIME,
                    Color.WHITE);

            pos = position.cpy();
            pos.sub(-width/2f, height/2f);
            model.createParticle(
                    pos,
                    Vector2.Zero,
                    dim,
                    MathUtils.random(0.1f, 0.5f),
                    Color.LIME,
                    Color.WHITE);
        }

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        if(position.x < -model.WORLD_WIDTH / 2f)
        {
            position.x = -model.WORLD_WIDTH /2f;
            velocity.x = -velocity.x;
        }
        else if(position.x > model.WORLD_WIDTH / 2f)
        {
            position.x = model.WORLD_WIDTH /2f;
            velocity.x = -velocity.x;
        }
        if(position.y < -model.WORLD_HEIGHT / 2f)
        {
            position.y = -model.WORLD_HEIGHT /2f;
            velocity.y = -velocity.y;
        }
        else if(position.y > model.WORLD_HEIGHT / 2f)
        {
            position.y = model.WORLD_HEIGHT / 2f;
            velocity.y = -velocity.y;
        }

        if(lifespan <= 0) isAlive = false;
    }

    @Override
    public void draw(SpriteBatch s)
    {

        color.a = 1.f - 0.5f * (1.f - lifespan / 5.f);

        s.setColor(color);
        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1f, 1f,
                angle);
    }

    @Override
    public void kill(WorldModel model)
    {
        isAlive = false;
        int particles = 64;
        Vector2 dim = new Vector2(0.25f, 0.025f);
        Vector2 velocity = new Vector2();
        for (int i = 0; i < particles; i++)
        {
            float angle = (float) i / (float) particles * 360f;

            float speed = MathUtils.random(8f, 14f);

            model.createParticle(
                    position,
                    velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                    dim,
                    MathUtils.random(0.05f, 0.15f),
                    Color.FIREBRICK,
                    Color.GREEN);

            speed = MathUtils.random(4f, 6f);

            model.createParticle(
                    position,
                    velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                    dim,
                    MathUtils.random(0.05f, 0.15f),
                    Color.LIME,
                    Color.CORAL);
        }
    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width / 2f,
                             position.y - height / 2f,
                                width, height);
    }

    @Override
    public void dispose()
    {

    }
}
