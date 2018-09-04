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
package com.golddaniel.main;

import bloom.Bloom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;

/**
 *
 * @author wrksttn
 */
public class WorldRenderer
{
    FitViewport viewport;
    PerspectiveCamera cam;
    SpriteBatch s;
    
    TextureRegion tex;
    
    Bloom bloom;
    
    float angle;
    
    public boolean doBloom = true;
    
    
    public WorldRenderer()
    {
        cam = new PerspectiveCamera(165f, Globals.WIDTH, Globals.HEIGHT);
        viewport = new FitViewport(Globals.WIDTH, Globals.HEIGHT, cam);
        viewport.apply();
        cam.position.z = 105f;
        cam.far = 200;
        
        tex = new TextureRegion(new Texture("texture.png"));
        
        s = new SpriteBatch();
        
        bloom = new Bloom(viewport, 1f);
        bloom.setBloomIntesity(6f);
        bloom.setTreshold(0.4f);
    }
    
    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    public void draw(WorldModel model)
    {
        cam.position.x = model.WORLD_WIDTH/2f;
        cam.position.y = model.WORLD_HEIGHT/2f;
       
        Vector3 rotation = cam.direction;
        
        float rotX = -0.02f;
        float rotY = -0.01f;
        if(model.getEntityType(Player.class).size > 0)
        {
            
            float ratio = model.player.position.x/model.WORLD_WIDTH;
           
            float desiredPos = model.WORLD_WIDTH*2f/3f - 
                    model.WORLD_WIDTH*1f/3f * ratio;
            
            cam.position.x = MathUtils.lerp(cam.position.x, desiredPos, 0.025f);
            
            rotX += 0.04f* model.player.getBoundingBox().x/model.WORLD_WIDTH;
            rotation.x = rotX;

            
            rotY += 0.02f* model.player.getBoundingBox().y/model.WORLD_HEIGHT;
            rotation.y = rotY;
        }
        else
        {
            rotation.x = MathUtils.lerp(rotation.x, 0, 0.025f);
            rotation.y = MathUtils.lerp(rotation.y, 0, 0.025f);
            if(abs(rotation.x) < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                rotation.x = 0;
            }
            if(abs(rotation.y) < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                rotation.y = 0;
            }
            
            cam.position.x = MathUtils.lerp(cam.position.x, model.WORLD_WIDTH/2f, 0.025f);
            cam.position.y = MathUtils.lerp(cam.position.y, model.WORLD_HEIGHT/2f, 0.025f);
            if(abs(cam.position.x) < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                cam.position.x = 0;
            }
            if(abs(cam.position.y) < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                cam.position.y = 0;
            }
        }
        
        cam.update();
        
        s.setProjectionMatrix(cam.combined);
       
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        bloom.setClearColor(0.2f, 0.2f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if(doBloom) bloom.capture();
        
        s.begin();
       
        s.setColor(Color.BLACK);
        s.draw(tex, 0, 0, model.WORLD_WIDTH, model.WORLD_HEIGHT);
        s.setColor(Color.WHITE);
        
        //draw grid on bottom
        Array<PhysicsGrid> gArr = model.getEntityType(PhysicsGrid.class);
        for(PhysicsGrid g : gArr)
        {
            g.draw(s);
        }
       
        
        for(Entity e : model.getAllEntities())
        {   
            if(!(e instanceof Player || e instanceof PhysicsGrid))
                e.draw(s);
        }
        //draw player on top
        if(model.player != null)
        {
            model.player.draw(s);
        }
        
        for(Particle p : model.getAllParticles())
        {
            p.draw(s);
        }
        
        s.end();
        if(doBloom)bloom.render();
    }
    
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        viewport.apply();   
        cam.update();
    }
}
