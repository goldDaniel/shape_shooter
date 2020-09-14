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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.core.AudioSystem;
import com.golddaniel.core.world.WorldModel;

/**
 * @author wrksttn
 */
public class Player extends Entity
{
    private static TextureRegion tex;

    private float particleHue;
    private Vector2 velocity;
    private Vector2 moveDir;
    private Vector2 shootDir;

    private float width;
    private float height;

    private final float RESPAWN_TIME = 1.75f;
    private float respawnTimer;

    //weapon cooldown, should probably move into its own module
    private final float COOLDOWN_DEFAULT = 0.125f;
    private float cooldown = 0;
    private float currentWeaponCooldown = COOLDOWN_DEFAULT;

    Vector2 scratch = new Vector2();

    public static void loadTextures(AssetManager assets)
    {
        if (tex == null)
            tex = new TextureRegion(assets.get("geometric/player.png", Texture.class));
    }

    public Player()
    {
        width = 0.5f;
        height = 0.5f;

        position = new Vector2(0, 0);
        velocity = new Vector2();
        moveDir = new Vector2();
        shootDir = new Vector2();

        respawnTimer = RESPAWN_TIME;
        isAlive = false;
    }


    public Vector2 getMoveDir()
    {
        return moveDir;
    }

    public Vector2 getShootDir()
    {
        return shootDir;
    }

    public void update(WorldModel model, float delta)
    {
        if(!isAlive)
        {
            respawnTimer -= delta;
            if(respawnTimer <= 0)
            {
                position.set(0, 0);
                applyRespawnEffects(model);
                model.addEntity(this);
                isAlive = true;
            }
            return;
        }

        float MAX_SPEED = 3.5f;

        Vector2 acceleration = moveDir.cpy().scl(40f * delta);

        if (moveDir.isZero())
        {
            velocity = velocity.lerp(Vector2.Zero, 3f * delta);
        }
        else
        {
            velocity.add(acceleration);
        }
        velocity.limit(MAX_SPEED);

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        if (shootDir.len2() > 0)
        {
            fireBullets(model, new Vector2(shootDir.x, shootDir.y));
        }


        //bound inside world rect
        if (position.x < -model.WORLD_WIDTH / 2f)
        {
            position.x = -model.WORLD_WIDTH / 2f;
        }
        else if (position.x > model.WORLD_WIDTH / 2)
        {
            position.x = model.WORLD_WIDTH / 2f;
        }

        if (position.y < -model.WORLD_HEIGHT / 2f)
        {
            position.y = -model.WORLD_HEIGHT / 2f;
        }
        else if (position.y > model.WORLD_HEIGHT / 2f)
        {
            position.y = model.WORLD_HEIGHT / 2f;
        }

        particleHue += 90f * delta;
        particleHue %= 360f;

        cooldown -= delta;

        createParticleTrail(model);
    }

    private void createParticleTrail(WorldModel model)
    {
        Color start = Color.RED.cpy().fromHsv(particleHue, 1f, 1f);
        Color end = Color.WHITE;
        Vector2 pos = new Vector2();
        Vector2 dim = new Vector2(0.1f, 0.1f);
        for (int i = 0; i < 10; i++)
        {

            pos.set(position);
            scratch.set(velocity.x, velocity.y);

            pos.x -= MathUtils.cos(scratch.angleRad() + MathUtils.PI / 4f) * width / 1.8f;
            pos.y -= MathUtils.sin(scratch.angleRad() + MathUtils.PI / 4f) * height / 1.8f;

            float lifespan = 0.2f + MathUtils.random(-0.1f, 0.1f);

            Vector2 vel = velocity.cpy().scl(-1f);

            vel.x += MathUtils.random(-1f, 1f);
            vel.y += MathUtils.random(-1f, 1f);



            model.createParticle(pos, vel, dim, lifespan, start, end);

            pos.set(position);
            pos.x -= MathUtils.cos(scratch.angleRad() - MathUtils.PI / 4f) * width / 1.8f;
            pos.y -= MathUtils.sin(scratch.angleRad() - MathUtils.PI / 4f) * height / 1.8f;

            model.createParticle(pos, vel, dim, lifespan, start, end);
        }
    }

    private void fireBullets(WorldModel model, Vector2 direction)
    {
        if (cooldown <= 0)
        {
            AudioSystem.playSound(AudioSystem.SoundEffect.LASER);

            Vector2 dir = new Vector2(direction.x, direction.y);

            Vector2 bulletPos = new Vector2();
            bulletPos.x = position.x + 0.005f;
            bulletPos.y = position.y + height / 2f;

            float speed = 25f;
            model.createBullet( bulletPos,
                                speed,
                                dir.angle());

            float dif = 1.5f;
            for (int i = 0; i < 2; i++)
            {
                model.createBullet( bulletPos,
                                    speed,
                                dir.angle() + dif * (i + 1));
                model.createBullet( bulletPos,
                                    speed,
                                dir.angle() - dif * (i + 1));
            }
            cooldown = currentWeaponCooldown;
        }
    }

    private void applyRespawnEffects(WorldModel model)
    {
        AudioSystem.playSound(AudioSystem.SoundEffect.RESPAWN);

        model.applyRadialForce(position, 50, 3.2f);
        model.applyRadialForce(position, 25, 2, Color.GOLD);

        int segments = 512;
        Vector2 vel = new Vector2();
        Vector2 dim = new Vector2(2.f, 0.1f);
        for(int i = 0; i < segments; i++)
        {
            float speed = MathUtils.random(28f, 48f);

            float angle = (float)i/(float)segments * 360f;
            vel.set(MathUtils.cosDeg(angle) * speed * MathUtils.random(0.4f, 1.f), MathUtils.sinDeg(angle) * speed);



            model.createParticle(position,
                                 vel,
                                 dim,
                                 3.f,
                                 new Color().fromHsv(angle, 1.f, 1.f),
                                 new Color().fromHsv(angle * 1.618f, 1.f, 1.f));
        }
    }

    public void draw(SpriteBatch s)
    {
        scratch.set(velocity.x, velocity.y);

        s.setColor(Color.WHITE);
        s.draw(tex,
                position.x - width / 2f, position.y - height / 2f,
                width / 2, height / 2,
                width, height,
                1f, 1f,
                scratch.angle());
    }

    public Rectangle getBoundingBox()
    {
        return new Rectangle(position.x - width / 2f, position.y - height / 2f,
                width / 2f, height / 2f);
    }

    @Override
    public void dispose()
    {

    }

    public void kill(WorldModel model)
    {
        AudioSystem.playSound(AudioSystem.SoundEffect.PLAYER_DEATH);
        isAlive = false;

        respawnTimer = RESPAWN_TIME;

        int particles = 512;
        for (int i = 0; i < particles; i++)
        {
            //particle angle
            float pAngle = (float) i / (float) particles * 360f;

            Vector2 dim = new Vector2(0.5f, 0.1f);

            float speed = MathUtils.random(28f, 48f);

            model.createParticle(
                    position,
                    new Vector2(
                            MathUtils.cosDeg(pAngle) * speed,
                            MathUtils.sinDeg(pAngle) * speed),
                    dim,
                    MathUtils.random(0.4f, 2f),
                    Color.MAGENTA,
                    Color.WHITE);

            speed = MathUtils.random(24f, 36f);

            model.createParticle(
                    position,
                    new Vector2(
                            MathUtils.cosDeg(pAngle) * speed,
                            MathUtils.sinDeg(pAngle) * speed),
                    dim,
                    MathUtils.random(0.4f, 2f),
                    Color.CYAN,
                    Color.WHITE);
        }
        model.applyRadialForce(position, 100, 3, Color.CYAN);
        model.applyRadialForce(position, 50,  2, Color.YELLOW);
        model.applyRadialForce(position, 25,  1, Color.RED);
        model.killAllEntities();
    }
}
