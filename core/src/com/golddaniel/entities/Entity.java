
package com.golddaniel.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public abstract class Entity 
{
    public Vector3 position;

    protected boolean isAlive;

    public Entity(AssetManager assets)
    {
        isAlive = true;
    }

    public abstract void update(WorldModel world, float delta);
    public abstract void draw(SpriteBatch s);
    
    public abstract void kill(WorldModel model);
    
    public abstract Rectangle getBoundingBox();
    
    public abstract void dispose();
    
    
    public final boolean isAlive()
    {
        return isAlive;
    }
    
    public void kill()
    {
        isAlive = false;
    }

}
