/*
 * Copyright 2018 .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.golddaniel.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.golddaniel.main.AudioSystem;
import com.golddaniel.main.PS4Map;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class Player extends Entity implements ControllerListener
{
    public enum WEAPON_TYPE
    {
        RAPID,
        SPREAD,
        TWIN,
    }

    private WEAPON_TYPE weaponType;

    float hue;

    static TextureRegion tex;
    Vector3 velocity;
    Vector3 moveDir;
    Vector3 shootDir;

    public float width;
    public float height;


    final float COOLDOWN_DEFAULT = 0.125f;
    final float COOLDOWN_POWERUP = 0.02f;

    final int EXTRA_STREAMS_DEFAULT = 2;
    final int EXTRA_STREAMS_POWERUP = 5;


    float powerupTimer = 0;
    int extraStreams = EXTRA_STREAMS_DEFAULT;

    //weapon cooldown, should probably move into its own module
    float cooldown = 0;
    float currentWeaponCooldown = COOLDOWN_DEFAULT;



    @Override
    public void connected(Controller controller)
    {

    }

    @Override
    public void disconnected(Controller controller)
    {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode)
    {
        System.out.println(buttonCode);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode)
    {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value)
    {
        final float DEADZONE = 0.2f;


        if (value * value > DEADZONE * DEADZONE)
        {
            System.out.println(axisCode + " :  " + value);
        }

        if (axisCode == PS4Map.AXIS_LEFT_HORIZONTAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                moveDir.x = value;
            } else
            {
                moveDir.x = 0;
            }
        }
        if (axisCode == PS4Map.AXIS_LEFT_VERTICAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                moveDir.y = -value;
            } else
            {
                moveDir.y = 0;
            }
        }

        if (axisCode == PS4Map.AXIS_RIGHT_HORIZONTAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                shootDir.x = value;
            } else
            {
                shootDir.x = 0;
            }
        }
        if (axisCode == PS4Map.AXIS_RIGHT_VERTICAL)
        {
            if (value * value > DEADZONE * DEADZONE)
            {
                shootDir.y = -value;
            } else
            {
                shootDir.y = 0;
            }
        }
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value)
    {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
    {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
    {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
    {
        return false;
    }

    public static void loadTextures(AssetManager assets)
    {
        if(tex == null)
            tex = new TextureRegion(assets.get("geometric/player.png", Texture.class));
    }

    public Player(AssetManager assets)
    {
        super(assets);


        width = 0.5f;
        height = 0.5f;
        
        position = new Vector3(0, 0, 0);
        velocity = new Vector3();
        moveDir = new Vector3();
        shootDir = new Vector3();
        
        isAlive = true;

        weaponType = WEAPON_TYPE.SPREAD;

        Controllers.addListener(this);
    }


    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }

    public void setMoveDir(float x, float y)
    {
        moveDir.x = x;
        moveDir.y = y;
    }

    public void setShootDir(float x, float y)
    {
        shootDir.x = x;
        shootDir.y = y;
    }

    public void update(WorldModel model, float delta)
    {

        if(Gdx.input.isKeyJustPressed(Input.Keys.U))
        {
            weaponType = WEAPON_TYPE.SPREAD;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.Y))
        {
            weaponType = WEAPON_TYPE.RAPID;
        }
        if(!isAlive) return;

        if(SharedLibraryLoader.isWindows || SharedLibraryLoader.isLinux)
        {
            moveDir.setZero();
            if(Gdx.input.isKeyPressed(Input.Keys.D))
            {
                moveDir.x += 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A))
            {
                moveDir.x -= 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W))
            {
                moveDir.y += 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S))
            {
                moveDir.y -= 1;
            }

            shootDir.setZero();
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            {
                shootDir.x += 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            {
                shootDir.x -= 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.UP))
            {
            shootDir.y += 1;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            {
                shootDir.y -= 1;
            }
        }

        float MAX_SPEED = 2.5f;

        Vector3 acceleration = moveDir.cpy().scl(60f*delta);

        if(moveDir.isZero())
        {
            velocity = velocity.lerp(Vector3.Zero, 5f*delta);
        }
        else
        {
            velocity.add(acceleration);
        }
        velocity.limit(MAX_SPEED);



        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        position.z = 0.00f;

        if(shootDir.len2() > 0)
        {
            fireBullets(model, new Vector3(shootDir.x, shootDir.y, 0));
        }

        powerupTimer -= delta;
        if(powerupTimer <= 0)
        {
            powerupTimer = 0;
            currentWeaponCooldown = COOLDOWN_DEFAULT;
            extraStreams = EXTRA_STREAMS_DEFAULT;
            powerupTimer = 0;
        }

       
        //bound inside world rect
        if(position.x < -model.WORLD_WIDTH/2f)
        {
            position.x = -model.WORLD_WIDTH/2f;
        }
        else if(position.x > model.WORLD_WIDTH/2)
        {
            position.x = model.WORLD_WIDTH/2f;
        }
        
        if(position.y < -model.WORLD_HEIGHT/2f)
        {
            position.y = -model.WORLD_HEIGHT/2f;
        }
        else if(position.y > model.WORLD_HEIGHT/2f)
        {
            position.y = model.WORLD_HEIGHT/2f;
        }

        hue += 90f*delta;
        hue %= 360f;
        
        cooldown -= delta;

        Vector3 pos = position.cpy();
        pos.z = -0.01f;
        model.applyRadialForce(pos, 250 * delta ,width * 1.5f);

        createParticleTrail(model);
    }

    private void createParticleTrail(WorldModel model)
    {
        Color start = Color.RED.cpy().fromHsv(hue, 1f, 1f);
        Color end = Color.WHITE.cpy();
        for (int i = 0; i < 10; i++)
        {
            Vector3 pos = new Vector3(position);

            Vector2 dir = new Vector2(velocity.x, velocity.y);

            pos.x -= MathUtils.cos(dir.angleRad()) * width / 2f;
            pos.y -= MathUtils.sin(dir.angleRad()) * height / 2f;

            float lifespan = 0.2f + MathUtils.random(-0.1f, 0.1f);

            Vector3 vel = velocity.cpy().scl(-1f);

            vel.x += MathUtils.random(-1f, 1f);
            vel.y += MathUtils.random(-1f, 1f);

            Vector3 dim = new Vector3(0.1f, 0.1f, 0.1f);

            model.createParticle(pos, vel, dim, lifespan, start, end);
        }
    }
    
    private void fireBullets(WorldModel model, Vector3 direction)
    {
        if(cooldown <= 0)
        {
            AudioSystem.playSound(AudioSystem.SoundEffect.LASER);

            Vector2 dir = new Vector2(direction.x, direction.y);

            Vector3 bulletPos = new Vector3();
            bulletPos.x = position.x + 0.005f;
            bulletPos.y = position.y + height /2f;

            float speed = 25f;
            model.createBullet(bulletPos,
                    speed,
                    dir.angle(),
                    Bullet.TYPE.LASER_1);

            float dif = 1.5f;
            //adds this amount of bullets to each side
            //i.e. extrabullets*2 gets added
            for (int i = 0; i < extraStreams; i++)
            {
                model.createBullet(bulletPos,
                        speed,
                        dir.angle() + dif*(i+1),
                        Bullet.TYPE.LASER_1);
                model.createBullet(bulletPos,
                        speed,
                        dir.angle() - dif*(i+1),
                        Bullet.TYPE.LASER_1);
            }
            cooldown = currentWeaponCooldown;
        }       
    }

    public void draw(SpriteBatch s)
    {
        Vector2 dir = new Vector2(velocity.x, velocity.y);

        s.setColor(Color.WHITE);
        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1f, 1f,
                dir.angle());
    }

    public void dispose()
    {
        Controllers.removeListener(this);
    }

    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width /2f, position.y - height /2f,
                             width / 2f, height);
    }

    public void kill(WorldModel model)
    {
        AudioSystem.playSound(AudioSystem.SoundEffect.PLAYER_DEATH);
        isAlive = false;
        int particles = 512;
        for (int i = 0; i < particles; i++)
        {
            //particle angle
            float pAngle = (float)i/(float)particles*360f;

            Vector3 dim = new Vector3(0.5f, 0.01f, 0.01f);

            float speed = MathUtils.random(15f, 22f);

            model.createParticle(
                    position.cpy(),
                    new Vector3(
                        MathUtils.cosDeg(pAngle) * speed,
                        MathUtils.sinDeg(pAngle) * speed,
                        0),
                    dim,
                    MathUtils.random(0.4f, 2f),
                    Color.MAGENTA,
                    Color.WHITE);

            speed = MathUtils.random(10f, 14f);

            model.createParticle(
                    position.cpy(),
                    new Vector3(
                            MathUtils.cosDeg(pAngle) * speed,
                            MathUtils.sinDeg(pAngle) * speed,
                            0),
                    dim,
                    MathUtils.random(0.4f, 2f),
                    Color.CYAN,
                    Color.WHITE);
        }
        model.killAllEntities();
    }

    public void applyPowerup()
    {
        extraStreams = EXTRA_STREAMS_POWERUP;
        currentWeaponCooldown = COOLDOWN_POWERUP;

        powerupTimer = 8f;
    }
}
