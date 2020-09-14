package com.golddaniel.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.core.AudioSystem;
import com.golddaniel.core.world.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Cuber extends Entity
{
    int health;
    
    
    static Texture tex;
    
    Vector2 dir;
    
    float width;
    float height;
    
    float activeTimer = 2f;

    float dirTimer = 0;

    Rectangle boundingBox = new Rectangle();

    public static void LoadTextures(AssetManager assets)
    {
        if(tex == null)
        {
            tex = assets.get("texture.png", Texture.class);
        }
    }

    public Cuber(Vector2 pos)
    {
        health = 9;
        isAlive = true;
        position = new Vector2(pos);

        dir = new Vector2();
        
        width = height = 0.4f;
        
        boolean axis = MathUtils.randomBoolean();
        
        boolean direction = MathUtils.randomBoolean();
        
        
        if(axis)//horizontal
        {
            dir.y = 0;
            dir.x = direction ? 1 : -1;
            
        }
        else//vertical
        {
            dir.x = 0;
            dir.y = direction ? 1 : -1;
        }
    }

    private Vector2 getMid()
    {
        return position.cpy().add(width/2f, height/2f);
    }
    
    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    @Override
    public void update(final WorldModel model, float delta)
    {
        if(activeTimer <= 0)
        {
            if(dirTimer <= 0)
            {
                dirTimer = 0.5f;

                if(model.getPlayer() != null)
                {
                    Vector2 pPos = model.getPlayer().position.cpy();

                    float xDist = pPos.x - position.x;
                    float yDist = pPos.y - position.y;

                    if(abs(xDist) > abs(yDist))
                    {
                        dir.y = 0;
                        if(xDist > 0)
                        {
                            dir.x = 1;
                        }
                        else
                        {
                            dir.x = -1;
                        }
                    }
                    else
                    {
                        dir.x = 0;
                        if(yDist > 0)
                        {
                            dir.y = 1;
                        }
                        else
                        {
                            dir.y = -1;
                        }
                    }
                }
                else
                {
                    if(abs(dir.x) > 0)
                    {
                        dir.x = 0;
                        dir.y = MathUtils.randomBoolean() ? 1 : -1;
                    }
                    else
                    {
                        dir.y = 0;
                        dir.x = MathUtils.randomBoolean() ? 1 : -1;
                    }
                }
            }
            else
            {
                dirTimer -= delta;
            }

            position.add(dir.cpy().scl(2f*delta));
            
            if(position.x < -model.WORLD_WIDTH/2f)
            {
                position.x = -model.WORLD_WIDTH /2f;
                dir.x = -dir.x;
            }
            else if(position.x > model.WORLD_WIDTH / 2f)
            {
                position.x = model.WORLD_WIDTH / 2f;
                dir.x = -dir.x;
            }
            
            if(position.y < -model.WORLD_HEIGHT /2f)
            {
                position.y = -model.WORLD_HEIGHT / 2f;
                dir.y = -dir.y;
            }
            else if(position.y > model.WORLD_HEIGHT /2f)
            {
                position.y = model.WORLD_HEIGHT / 2f;
                dir.y = -dir.y;
            }
        }
        else
        {
            Vector2 dim = new Vector2(0.35f, 0.05f);
            Vector2 velocity = new Vector2();
            activeTimer -= delta;
            for(int i = 0; i < 6; i++)
            {
                float angle = MathUtils.PI * activeTimer * (i + 1);
                float speed = MathUtils.random(12f, 14f);
                speed *= 0.5f*activeTimer;


                model.createParticle(
                        position,
                        velocity.set(MathUtils.cos(angle) * speed,
                                MathUtils.sin(angle) * speed),
                        dim,
                        MathUtils.random(0.1f, 0.25f),
                        Color.RED,
                        Color.WHITE);
            }
        } 
    }

    @Override
    public void draw(SpriteBatch s)
    {
        Color c = Color.RED.cpy();
        if(activeTimer > 0)
        {
            c.a = 0.4f;
        }
        s.setColor(c);
        
        s.draw(tex, position.x - width / 2f, position.y - height / 2f, width, height);
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer > 0) return;
        health--;
        if(health <= 0)
        {
            AudioSystem.playSound(AudioSystem.SoundEffect.ENEMY_DEATH);
            isAlive = false;
            model.applyRadialForce(getMid(), 55f, width * 5, Color.CYAN);

            model.addScore(10);
            model.createMultipliers(position, 5);

            int particles = 256;
            Vector2 vel = new Vector2();
            Vector2 dim = new Vector2(0.5f, 0.075f);
            for (int i = 0; i < particles; i++)
            {
                float angle = (float)i/(float)particles*360f;


                float speed = 14f + MathUtils.random(-2f, 2f);


                vel .set(
                        MathUtils.cosDeg(angle)*speed,
                        MathUtils.sinDeg(angle)*speed);



                model.createParticle(
                        position,
                        vel,
                        dim,
                        MathUtils.random(0.5f, 0.65f),
                        Color.RED,
                        Color.WHITE);
            } 
        } 
    }

    @Override
    public Rectangle getBoundingBox()
    {
        boundingBox.set(position.x - width / 2f, position.y - width / 2f, width, height);
        return boundingBox;
    }

    @Override
    public void dispose()
    {
    }

    public boolean isActive()
    {
        return activeTimer <= 0;
    }
    
}
