package com.golddaniel.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.main.WorldModel;

public class Obstacle extends Entity
{

    Vector2 dim;

    static ShapeRenderer sh = new ShapeRenderer();

    public Obstacle(Vector3 pos, Vector2 dim)
    {
        this.position = pos;
        this.dim = dim;

        isAlive = true;
    }

    @Override
    public void update(WorldModel world, float delta)
    {

    }

    @Override
    public void draw(SpriteBatch s)
    {
        System.out.println("DRAW");
        s.end();

        sh.setColor(Color.BLACK);
        sh.setProjectionMatrix(s.getProjectionMatrix());
        sh.begin(ShapeRenderer.ShapeType.Filled);
        sh.rect(position.x - dim.x / 2f, position.y - dim.y / 2f, dim.x, dim.y);
        sh.end();

        sh.setColor(Color.LIME);
        sh.begin(ShapeRenderer.ShapeType.Line);
        sh.rect(position.x - dim.x / 2f, position.y - dim.y / 2f, dim.x, dim.y);
        sh.end();

        s.begin();
    }

    @Override
    public void kill(WorldModel model)
    {

    }

    @Override
    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - dim.x / 2f, position.y - dim.y / 2f, dim.x, dim.y);
    }

    @Override
    public void dispose()
    {

    }
}
