
package com.golddaniel.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.core.world.WorldModel;


/**
 *
 * @author wrksttn
 */
public abstract class Entity 
{
    public Vector2 position;

    protected boolean isAlive;

    public Entity()
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
