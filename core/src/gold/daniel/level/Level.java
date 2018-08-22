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
package gold.daniel.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Timer;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Entity;
import com.golddaniel.entities.Player;
import com.golddaniel.main.Globals;
import com.golddaniel.main.MessageListener;
import com.golddaniel.main.Messenger;
import com.golddaniel.main.PhysicsGrid;
import com.golddaniel.main.WorldModel;

/**
 * Helps manage worldModel
 * @author wrksttn
 */
public class Level implements MessageListener
{
    
    float stateTime;
    
    float respawnTimer;
    
    int lives;
    int score;
    
    WorldModel model;
    
    ArrayMap<Integer, Array<Entity>> toSpawn;
    
    boolean respawning;
    
    public Level()
    {
        Messenger.addListener(Messenger.EVENT.BOUNCER_DEAD, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.TRACKER_DEAD, (MessageListener)this);
        
        score = 0;
        respawnTimer = 60;
        lives = 3;
        respawning = false;
        
        toSpawn = new ArrayMap<Integer, Array<Entity>>();
        model = new WorldModel(Globals.WIDTH*1.5f - 64, Globals.HEIGHT*1.5f - 64);
        
        buildLevel();
    }
    
    private void buildLevel()
    {
        Vector2 size = new Vector2(model.WORLD_WIDTH, model.WORLD_HEIGHT);
        int spacing = 64;
        model.addEntity(new PhysicsGrid(size, (int)size.x/spacing, (int)size.y/spacing));
        model.addEntity(new Player(model));
        
        int time = 2;
        
        float numSpawning = 5f;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = 0;
            pos.y = (float)(i + 1f)/(float)(1 + numSpawning)*model.WORLD_HEIGHT/2;
            
            toSpawn.get(time).add(
                    new Bouncer(
                        pos, 
                        new Vector2(1, 0)));
        }
        time += 1;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = model.WORLD_WIDTH - 64;
            pos.y = model.WORLD_HEIGHT - (float)(i + 1f)/(float)(numSpawning + 1f)*model.WORLD_HEIGHT/2;
            
            toSpawn.get(time).add(
                    new Bouncer(
                        pos, 
                        new Vector2(-1, 0)));
        }
        
        time += 4;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = ((float)i+1f)/(float)(numSpawning + 1f)*model.WORLD_WIDTH;
            pos.y = model.WORLD_HEIGHT - 64;
            
            toSpawn.get(time).add(
                    new Bouncer(
                        pos, 
                        new Vector2(0, -1)));
        }
        time += 6;
        numSpawning = 128;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = 32;
            pos.y = model.WORLD_HEIGHT * ((float)i + 1)/((float)numSpawning + 1f);
            
            toSpawn.get(time).add(new Boid(pos));
        }
        
        time += 8;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = model.WORLD_WIDTH - 32;
            pos.y = model.WORLD_HEIGHT * ((float)i + 1f)/((float)numSpawning + 1f);
            
            toSpawn.get(time).add(new Boid(pos));
        }
        
        time += 10;
        numSpawning = 5f;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = model.WORLD_WIDTH/4f * ((float)i + 1f)/((float)numSpawning + 1f);
            pos.y = model.WORLD_HEIGHT/2f + 
                    model.WORLD_HEIGHT/2f * ((float)i + 1f)/((float)numSpawning + 1f);
            
            Vector2 dir = new Vector2();
            dir.x = MathUtils.cos(-45f);
            dir.y = MathUtils.sin(-45f);
            toSpawn.get(time).add(new Bouncer(pos.cpy(), dir.cpy()));
        }
        
        time += 3;
        numSpawning = 5f;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = model.WORLD_WIDTH*3f/4f + 
                    model.WORLD_WIDTH/4f * ((float)i + 1f)/((float)numSpawning + 1f);
                   
            pos.y = model.WORLD_HEIGHT -  
                    model.WORLD_HEIGHT/2f * ((float)i + 1f)/((float)numSpawning + 1f);
            
            Vector2 dir = new Vector2();
            dir.x = -0.5f;
            dir.y = -0.5f;
            toSpawn.get(time).add(new Bouncer(pos.cpy(), dir.cpy()));
        }
        
        time += 4;
        numSpawning = 10f;
        toSpawn.put(time, new Array<Entity>());
        for (int i = 0; i < numSpawning; i++)
        {
            Vector2 pos = new Vector2();
            pos.x = model.WORLD_WIDTH * ((float)i + 1f)/((float)numSpawning + 1f);
            pos.y = 64f;
            
            Vector2 dir = new Vector2();
            dir.x = 0;
            dir.y = 1;
            toSpawn.get(time).add(new Bouncer(pos.cpy(), dir.cpy()));
        }
        
        time += 5;
        numSpawning = 10f;
        for (int i = 0; i < 20; i++)
        {
            time += 1;
            toSpawn.put(time, new Array<Entity>());
            
            for (int a = 0; a < numSpawning; a++)
            {
                toSpawn.get(time).add(new Boid(
                    new Vector2(32, 32)));
                toSpawn.get(time).add(new Boid(
                    new Vector2(model.WORLD_WIDTH - 32, 32)));
                toSpawn.get(time).add(new Boid(
                    new Vector2(32, model.WORLD_HEIGHT - 32)));
                toSpawn.get(time).add(new Boid(
                    new Vector2(model.WORLD_WIDTH - 32, model.WORLD_HEIGHT - 32)));
            }
        }
        
    }   
    
    public void update(float delta)
    {
        
        if(model.getEntityType(Player.class).size == 0)
        {
            respawnPlayer();
        }
        else
        {
            stateTime += delta;
            respawnTimer -= delta;
        }
        
        if(respawnTimer <= 0)
        {
            respawnTimer = 0;
        }
        else
        {
            //add entities at specified time
            if(toSpawn.containsKey((int)stateTime))
            {
                for(Entity entity : toSpawn.get((int)(stateTime)))
                {
                    model.addEntity(entity);
                }
                toSpawn.removeKey((int)stateTime);
            }
            model.update(delta);
        }
    }
    
    private void respawnPlayer()
    {
        if(!respawning)
        {
            lives--;
            respawning = true;
            new Timer().scheduleTask(new Timer.Task()
            {
                @Override
                public void run()
                {
                    model.addEntity(new Player(model));
                    respawning = false;
                }
            }, 2);
        }
    }
    
    public WorldModel getModel()
    {
        return model;
    }
    
    public int getRemainingTime()
    {
        return (int)respawnTimer;
    }
    
    public int getScore()
    {
        return score;
    }

    @Override
    public void onNotify(Messenger.EVENT event)
    {
        if(event == Messenger.EVENT.BOUNCER_DEAD)
        {
            score += 100;
        }
        else if(event == Messenger.EVENT.TRACKER_DEAD)
        {
            score += 50;
        }
    }
}
