
package com.golddaniel.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.MessageListener;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public abstract class Entity implements MessageListener
{
    public Vector2 position;
    
    protected boolean isAlive;
    @Override
    public abstract void onNotify(Messenger.EVENT event);
    
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
