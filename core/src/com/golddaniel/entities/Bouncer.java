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
import com.golddaniel.core.AudioSystem;
import com.golddaniel.core.world.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Bouncer extends Entity
{
    static TextureRegion tex;
    
    Color color;
    
    //should always be normalized
    Vector2 dir;
    float speed;
    
    int prevHealth;
    int health;
    
    float width;
    float height;

    float activeTimer = 2f;

    Rectangle boundingBox = new Rectangle();

    //used for particles
    Vector2 velocity = new Vector2();

    public static void LoadTextures(AssetManager assets)
    {
        if(tex == null)
        {
            tex = new TextureRegion(assets.get("geometric/player.png", Texture.class));
        }
    }

    public Bouncer(Vector2 pos, Vector2 dir)
    {


        width = 1f;
        height = 0.5f;
        this.position = pos;
        position.x += width/2;
        position.y += height/2;
        //normalize just in case a normal vector was not passed
        this.dir = new Vector2(dir.x, dir.y).nor();
        speed = 2f;
        
        color = Color.YELLOW.cpy();

        health = 10;
        prevHealth = health;
    }

    @Override
    public void update(WorldModel model, float delta)
    {
        if(activeTimer > 0)
        {

            Vector2 dim = new Vector2(0.35f, 0.05f);
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
                        Color.YELLOW,
                        color);
            }
        }
        else
        {
            if(prevHealth > health)
            {
                color = Color.CYAN;
            }
            else
            {
                color = Color.YELLOW;
            }
            
            position.x += dir.x*speed*delta;
            position.y += dir.y*speed*delta;
            
            prevHealth = health;
        }
        
        
        if(position.x < -model.WORLD_WIDTH / 2f)
        {
            position.x = -model.WORLD_WIDTH / 2f;
            dir.x = -dir.x;
        }
        else if(position.x > model.WORLD_WIDTH / 2f)
        {
            position.x = model.WORLD_WIDTH / 2f;
            dir.x = -dir.x;
        }
        if(position.y < -model.WORLD_HEIGHT / 2f)
        {
            position.y = -model.WORLD_HEIGHT / 2f;
            dir.y = -dir.y;
        }
        else if(position.y > model.WORLD_HEIGHT / 2f)
        {
            position.y = model.WORLD_HEIGHT / 2f;
            dir.y  = -dir.y;
        }
    }

    @Override
    public void draw(SpriteBatch s)
    {   
        if(activeTimer <= 0)
        { 
            color.a = 1f;
        }
        else
        {
            color.a = 0.45f;
        }
        s.setColor(color);

        s.draw(tex,
                    position.x - width / 2f, position.y - height / 2f,
                    width / 2, height / 2,
                    width, height,
                    1, 1,
                    dir.angle());
        
        s.setColor(Color.WHITE);
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public Rectangle getBoundingBox()
    {
        boundingBox.set(position.x - width / 2f, position.y - height /2f, height, height);
        return boundingBox;
    }
    
    public void kill()
    {
        isAlive = false;
    }
    
    @Override
    public void kill(WorldModel model)
    {
        if(activeTimer <= 0)
        {
            health--;
            if(health <= 0)
            {
                Vector2 dim = new Vector2(0.5f, 0.05f);
                Vector2 velocity = new Vector2();
                int particles = 256;
                for (int i = 0; i < particles; i++)
                {
                    float angle = (float)i/(float)particles*360f;




                    float speed = MathUtils.random(14f, 22f);

                    model.createParticle(
                            position,
                            velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.YELLOW,
                            Color.WHITE);

                    speed = MathUtils.random(8f, 12f);

                    model.createParticle(
                            position,
                            velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed),
                            dim,
                            MathUtils.random(0.1f, 0.5f),
                            Color.WHITE,
                            Color.ORANGE);
                }

                isAlive = false;

                model.addScore(10);
                model.createMultipliers(position, 6);
                model.applyRadialForce(position, 48f,
                                        2f,
                                        Color.MAGENTA);

                AudioSystem.playSound(AudioSystem.SoundEffect.ENEMY_DEATH);
            }
        }
    }
    
    public boolean isActive()
    {
        return activeTimer <= 0;
    }
}
