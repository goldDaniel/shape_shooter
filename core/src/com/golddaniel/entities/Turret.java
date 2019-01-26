package com.golddaniel.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.main.WorldModel;

public class Turret extends Entity
{
    Vector3 destination;


    float fireTimer = 0.25f;

    static TextureRegion tex = new TextureRegion(new Texture("geometric/player.png"));

    public Turret(Vector3 position, Vector3 destination)
    {
        this.position = position.cpy();
        this.destination = destination.cpy();

        isAlive = true;
    }

    @Override
    public void update(WorldModel world, float delta)
    {

        position.lerp(destination, 4f*delta);
        if(position.epsilonEquals(destination)) position.set(destination);

        if(position.epsilonEquals(destination))
        {
            fireTimer -= delta;

            if(fireTimer <= 0)
            {
                Vector2 target = new Vector2(world.getPlayer().position.x, world.getPlayer().position.y);

                fireBullet(world, target);

                fireTimer = 0.25f;
            }
        }
    }

    private void fireBullet(WorldModel model, Vector2 target)
    {
        float dir = target.cpy().sub(position.x, position.y).angle();

        float speed = 2f;

        model.createBullet(position, speed, dir, Bullet.TYPE.LASER_4);
    }

    @Override
    public void draw(SpriteBatch s)
    {
        float size = 0.5f;

        s.setColor(Color.WHITE);
        s.draw(tex,
                position.x - size / 2f, position.y - size / 2f,
                size / 2f, size / 2f,
                size, size,
                1f, 1f,
                0);
    }

    @Override
    public void kill(WorldModel model)
    {

    }

    @Override
    public Rectangle getBoundingBox()
    {
        return null;
    }

    @Override
    public void dispose()
    {

    }
}
