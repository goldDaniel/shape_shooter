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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.golddaniel.controllers.ControllerMapping;
import com.golddaniel.controllers.InputController;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Particle;
import com.golddaniel.entities.Player;
import com.golddaniel.entities.RapidFire;

/**
 * Helps manage worldModel
 * @author wrksttn
 */
public class LevelModel implements MessageListener
{
    public static enum LEVEL_TYPE
    {
        PASSIVE,
        TIME_ATTACK,
        ENDLESS,
    }
    
    private LEVEL_TYPE levelType;
    
    float remaingingTime;
    float stateTime;   
    
    ArrayMap<Integer, Array<Entity>> toSpawn;
    WorldModel worldModel;
    
    Timer respawnTimer = new Timer();
    
    public LevelModel(FileHandle levelFile)
    {
        Messenger.addListener(Messenger.EVENT.BOUNCER_DEAD, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.TRACKER_DEAD, (MessageListener)this);
        
        toSpawn = new ArrayMap<Integer, Array<Entity>>();
        
        buildLevel(levelFile);
    }
    
    private void buildLevel(FileHandle levelFile)
    {
        XmlReader reader = new XmlReader();
        Element root = reader.parse(levelFile);
       
        final String TIME_KEY = "TIME";
        final String ENDLESS_KEY = "ENDLESS";
        final String PASSIVE_KEY = "PASSIVE";
        
        //LEVEL TYPE=====================================
        String typeLevel = root.getChildByName("level").get("type");
        if(typeLevel.equals(TIME_KEY))
        {
            this.levelType = LEVEL_TYPE.TIME_ATTACK;
        }
        else if(typeLevel.equals(ENDLESS_KEY))
        {
            this.levelType = LEVEL_TYPE.ENDLESS;
        }
        else if(typeLevel.equals(PASSIVE_KEY))
        {
            this.levelType = LEVEL_TYPE.PASSIVE;
        }
        //================================================
    
        
        //TIME ATTACK SPECIFIC=============================
        if(this.levelType == LEVEL_TYPE.TIME_ATTACK)
        {
            float startTime = root.getChildByName("level").getFloat("time");
            remaingingTime = startTime;
            
            Element spawnElement = root.getChildByName("spawn");
            Array<Element> enemyElements = spawnElement.getChildrenByName("enemy");
        
            for(Element el : enemyElements)
            {
                int spawnTime = el.getInt("time");

                final String BOUNCER_KEY = "BOUNCER";
                final String BOID_KEY    = "BOID";
                final String CUBER_KEY   = "CUBER";

                //PARSE SPAWN POSITION================================
                Vector2 pos = new Vector2();
                String posString = el.get("position");
                String[] stringCoords = posString.split(",");

                pos.x = Float.parseFloat(stringCoords[0]);
                pos.y = Float.parseFloat(stringCoords[1]);
                //====================================================

                if(!toSpawn.containsKey(spawnTime))
                {
                    toSpawn.put(spawnTime, new Array<Entity>());
                }

                String enemyType = el.get("type");
                if(enemyType.equals(BOUNCER_KEY))
                {
                    String dirString = el.get("direction");
                    String[] dirVec = dirString.split(",");

                    Vector2 dir = new Vector2();
                    dir.x = Float.parseFloat(dirVec[0]);
                    dir.y = Float.parseFloat(dirVec[1]);


                    Bouncer b = new Bouncer(pos, dir);

                    toSpawn.get(spawnTime).add(b);
                }
                else if(enemyType.equals(BOID_KEY))
                {
                    Boid b = new Boid(pos);

                    toSpawn.get(spawnTime).add(b);
                }
                else if(enemyType.equals(CUBER_KEY))
                {
                    Cuber c = new Cuber(pos);
                    
                    toSpawn.get(spawnTime).add(c);
                }
            }
        }
        //================================================
        
        //for the physics grid
        float widthScale = root.getChildByName("level").getFloat("widthScale");
        float heightScale = root.getChildByName("level").getFloat("heightScale");
        
        worldModel = 
                new WorldModel(Globals.WIDTH*widthScale, 
                               Globals.HEIGHT*heightScale);
        
        int gridSpacing = root.getChildByName("level").getInt("gridSpacing");
        
        PhysicsGrid g = new PhysicsGrid(
                            new Vector2(worldModel.WORLD_WIDTH, worldModel.WORLD_HEIGHT), 
                            (int)worldModel.WORLD_WIDTH / gridSpacing, 
                            (int)worldModel.WORLD_HEIGHT/ gridSpacing);
        
        worldModel.addEntity(g);
        worldModel.addEntity(new Player(worldModel)); 
    }
    
    public void update(float delta)
    {
        stateTime += delta;
        remaingingTime -= delta;
        
        if(worldModel.getPlayer() == null)
        {
            if(respawnTimer.isEmpty())
            {
                respawnTimer.scheduleTask(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        worldModel.addEntity(new Player(worldModel));
                        
                        int particles = 128;
                        for(int i = 0; i < particles; i++)
                        {
                            float angle = (float)i/(float)particles*360f;
                            angle += MathUtils.random(-2.5f, 2.5f);

                            worldModel.createParticle(
                                new Vector2(
                                        worldModel.WORLD_WIDTH/2f,
                                        worldModel.WORLD_HEIGHT/2f), 
                                angle, 
                                MathUtils.random(0.1f, .3f), 
                                Globals.WIDTH*2,
                                Color.PINK.cpy(), 
                                Color.CYAN.cpy(),
                                Particle.TYPE.NORMAL);
                            
                            worldModel.createParticle(
                                new Vector2(
                                        worldModel.WORLD_WIDTH/2f,
                                        worldModel.WORLD_HEIGHT/2f), 
                                angle, 
                                MathUtils.random(0.1f, .3f), 
                                Globals.WIDTH,
                                Color.LIME.cpy(), 
                                Color.YELLOW.cpy(),
                                Particle.TYPE.SPIN);
                        }
                    }
                }, 0.8f);
            }
        }
        
        if(levelType == LEVEL_TYPE.TIME_ATTACK)
        {
            if(toSpawn.containsKey((int)stateTime))
            {
                Array<Entity> toAdd = toSpawn.get((int)stateTime);
                for(Entity e : toAdd)
                {
                    worldModel.addEntity(e);
                    
                    
                }
                toSpawn.removeKey((int)stateTime);        
            }
        }
        
        if(InputController.controller != null)
        {
            if(InputController.controller.getButton(ControllerMapping.L1))
            {
                if(worldModel.getEntityType(RapidFire.class).size == 0)
                {
                    Vector2 pos = new Vector2(
                        MathUtils.random(worldModel.WORLD_WIDTH/4f, 
                                          worldModel.WORLD_WIDTH * 3f/4f), 
                        MathUtils.random(worldModel.WORLD_HEIGHT/4f, 
                                         worldModel.WORLD_HEIGHT * 3f/4f));
            
                    worldModel.addEntity(new RapidFire(pos));
                }
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            Vector2 pos = new Vector2(
                        MathUtils.random(worldModel.WORLD_WIDTH/4f, 
                                          worldModel.WORLD_WIDTH * 3f/4f), 
                        MathUtils.random(worldModel.WORLD_HEIGHT/4f, 
                                         worldModel.WORLD_HEIGHT * 3f/4f));
            
            worldModel.addEntity(new RapidFire(pos));
        }
        
        worldModel.update(delta);
    }
    
    public WorldModel getWorldModel()
    {
        return worldModel;
    }
    

    public void killAll()
    {
        for(Entity e : worldModel.getAllEntities())
        {
            if(!(e instanceof Player))
            {
                e.kill(worldModel);
            }
        }
    }
    
    public int getRemainingTime()
    {
        int result = 0;
        if(remaingingTime > 0)
        {
            result = MathUtils.round(remaingingTime);
        }
        return result;
    }
    
    @Override
    public void onNotify(Messenger.EVENT event)
    {
        if(event == Messenger.EVENT.BOUNCER_DEAD)
        {    
        }
        else if(event == Messenger.EVENT.TRACKER_DEAD)
        {    
        }
    }
    
    public int getScore()
    {
        return worldModel.getScore();
    }
}
