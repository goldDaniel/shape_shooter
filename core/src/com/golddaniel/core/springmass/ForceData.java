package com.golddaniel.core.springmass;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ForceData
{
    public Vector2 pos;
    public float radius;
    public float force;
    Color color;

    public ForceData(Vector2 pos, float force, float radius, Color color)
    {
        this.pos = pos;
        this.force = force;
        this.radius = radius;
        this.color = color;
    }
}
