package com.golddaniel.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.golddaniel.core.springmass.SpringMassGrid;
import com.golddaniel.core.world.WorldModel;
import com.golddaniel.entities.Boid;
import com.golddaniel.entities.Bouncer;
import com.golddaniel.entities.Cuber;
import com.golddaniel.entities.Entity;


/**
 * TODO: this is currently how we edit levels, please change this
 */
public class LevelBuilder
{

    private static WorldModel m;

    public static void buildLevel1(AssetManager assets)
    {
        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 14;
        float worldHeight = 8;

        float levelTime = 50;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);
        Array<Entity> toAdd;

        int time = 2;
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(-worldWidth/2f, worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(-worldWidth/2f, -worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth/2f, worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth/2f, -worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(-worldWidth/2f, worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(-worldWidth/2f, -worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth/2f, worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);
        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth/2f, -worldHeight/2f)));
        toSpawn.put(time+=2, toAdd);


        time += 8;
        boolean left = false;
        for(float pos = 0; pos < worldHeight / 2f; pos += 0.1f)
        {
            toAdd = new Array<Entity>();
            float x = -worldWidth/2f;
            if(!left)
            {

                x *= -1;
            }
            left = !left;

            toAdd.add(new Boid(new Vector2(x, worldHeight / 2f - pos)));
            toAdd.add(new Boid(new Vector2(x, pos - worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(x, worldHeight / 2f - pos)));
            toAdd.add(new Boid(new Vector2(x, pos - worldHeight / 2f)));

            toSpawn.put(time, toAdd);
            time++;
        }

        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);

        m = model;
    }


    public static void buildLevel2(AssetManager assets)
    {

        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 10;
        float worldHeight = 10;

        float levelTime = 70;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);
        Array<Entity> toAdd = new Array<Entity>();

        for(float pos = -worldHeight/2f + 0.5f; pos < worldHeight/2f - 0.5f; pos += 2)
        {
            toAdd.add(new Bouncer(new Vector2(-worldWidth/2f, pos), new Vector2(1, 0)));
        }

        toSpawn.put(4, toAdd);

        toAdd = new Array<Entity>();
        for(float pos = -worldWidth/2f + 0.5f; pos < worldWidth/2f - 0.5f; pos += 1.5f)
        {
            toAdd.add(new Bouncer(new Vector2(pos, worldHeight/2f), new Vector2(0, 1)));
        }
        toSpawn.put(12, toAdd);

        int time = 16;
        for(int i = 0; i < 10; i++)
        {
            toAdd = new Array<Entity>();
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(worldWidth /2f, -worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(worldWidth /2f, worldHeight/2f)));
            toSpawn.put(time++, toAdd);
        }

        time = 34;
        toAdd = new Array<Entity>();
        for(float pos = -worldWidth/2f + 0.5f; pos < worldWidth/2f - 0.5f; pos += 2f)
        {
            toAdd.add(new Bouncer(new Vector2(pos, -worldHeight/2f), new Vector2(0, 1)));
        }
        toSpawn.put(time, toAdd);


        time = 42;
         for(int i = 0; i < 3; i++)
         {
             toAdd = new Array<Entity>();
             toAdd.add(new Cuber(new Vector2(-worldWidth/2f, worldHeight/2f)));
             toAdd.add(new Cuber(new Vector2(-worldWidth/2f, -worldHeight/2f)));
             toAdd.add(new Cuber(new Vector2(worldWidth/2f, worldHeight/2f)));
             toAdd.add(new Cuber(new Vector2(worldWidth/2f, -worldHeight/2f)));

             toSpawn.put(time, toAdd);

            time += 4;
         }


        for(int i = 0; i < 14; i++)
        {
            toAdd = new Array<Entity>();
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(worldWidth /2f, -worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight/2f)));
            toAdd.add(new Boid(new Vector2(worldWidth /2f, worldHeight/2f)));
            toSpawn.put(time++, toAdd);
        }


        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);

        m = model;
    }

    public static void buildLevel3(AssetManager assets)
    {
        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 7;
        float worldHeight = 10;

        float levelTime = 50;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);
        Array<Entity> toAdd = new Array<Entity>();

        toAdd.add(new Bouncer(new Vector2(-worldWidth/2f, worldHeight/2f), new Vector2(1f, -1f)));
        toAdd.add(new Bouncer(new Vector2(-worldWidth/2f, -worldHeight/2f), new Vector2(1f, 1f)));
        toAdd.add(new Bouncer(new Vector2(worldWidth/2f, worldHeight/2f), new Vector2(-1f, -1f)));
        toAdd.add(new Bouncer(new Vector2(worldWidth/2f, -worldHeight/2f), new Vector2(-1f, 1f)));
        toSpawn.put(4, toAdd);

        toAdd = new Array<Entity>();

        toAdd.add(new Bouncer(new Vector2(-worldWidth/2f, worldHeight/4f), new Vector2(1f, -1f)));
        toAdd.add(new Bouncer(new Vector2(-worldWidth/2f, -worldHeight/4f), new Vector2(1f, 1f)));
        toAdd.add(new Bouncer(new Vector2(worldWidth/2f, worldHeight/4f), new Vector2(-1f, -1f)));
        toAdd.add(new Bouncer(new Vector2(worldWidth/2f, -worldHeight/4f), new Vector2(-1f, 1f)));
        toSpawn.put(8, toAdd);


        int time = 14;
        for(float pos = -worldWidth / 2f; pos < worldWidth /2f; pos += 0.25f)
        {
            toAdd = new Array<Entity>();
            toAdd.add(new Bouncer(new Vector2(pos, worldHeight/2f), new Vector2(0, -1f)));
            pos += 0.2f;
            toAdd.add(new Bouncer(new Vector2(pos, worldHeight/2f), new Vector2(0, -1f)));
            toSpawn.put(time++, toAdd);
        }

        time += 2;
        for(float pos = -worldWidth / 2f; pos < worldWidth /2f; pos += 0.25f)
        {
            toAdd = new Array<Entity>();
            toAdd.add(new Bouncer(new Vector2(pos, worldHeight/2f), new Vector2(0, -1f)));
            pos += 0.2f;
            toAdd.add(new Bouncer(new Vector2(pos, worldHeight/2f), new Vector2(0, -1f)));
            toSpawn.put(time++, toAdd);
        }

        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);

        m = model;
    }

    public static void buildLevel4(AssetManager assets)
    {
        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 7;
        float worldHeight = 10;

        float levelTime = 50;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);
        Array<Entity> toAdd = new Array<Entity>();


        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);


        m = model;
    }

    public static void buildLevel5(AssetManager assets)
    {
        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 7;
        float worldHeight = 10;

        float levelTime = 50;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);
        Array<Entity> toAdd = new Array<Entity>();


        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);

        m = model;
    }

    public static void buildLevel6(AssetManager assets)
    {
        resetWorldModel();

        ArrayMap<Integer, Array<Entity>> toSpawn = new ArrayMap<Integer, Array<Entity>>();

        float worldWidth = 14;
        float worldHeight = 8;

        float levelTime = 100;

        WorldModel model = new WorldModel(worldWidth, worldHeight, toSpawn, levelTime);

        for (int i = 0; i < 20; i += 4)
        {
            Array<Entity> toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector2(worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));

            toSpawn.put(i, toAdd);
        }

        Array<Entity> toAdd = new Array<Entity>();

        for (int i = 0; i < worldHeight / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector2(-worldWidth / 2f, -worldHeight / 2f + 2 * (i + 1)),
                    new Vector2(1, 0)));
        }
        toSpawn.put(24, toAdd);
        toAdd = new Array<Entity>();

        for (int i = 0; i < worldHeight / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector2(worldWidth / 2f, -worldHeight / 2f + 2 * (i)),
                    new Vector2(-1, 0)));
        }
        toSpawn.put(32, toAdd);

        toAdd = new Array<Entity>();


        for (int i = 0; i < worldWidth / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector2(-worldWidth / 2f + 2 * (i), -worldHeight / 2f),
                    new Vector2(0, 1)));
        }
        toSpawn.put(42, toAdd);

        for (int i = 0; i < worldWidth / 2; i++)
        {
            toAdd.add(new Bouncer(new Vector2(-worldWidth / 2f + 2 * (i), worldHeight / 2f),
                    new Vector2(0, -1)));
        }
        toSpawn.put(46, toAdd);

        for (int i = 46; i < 75; i += 2)
        {
            toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector2(worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));

            toSpawn.put(i, toAdd);
        }


        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth / 2f, worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));

        toSpawn.put(78, toAdd);


        toAdd = new Array<Entity>();
        toAdd.add(new Cuber(new Vector2(worldWidth / 2f, worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
        toAdd.add(new Cuber(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));

        toSpawn.put(85, toAdd);

        for (int i = 88; i < 100; i++)
        {
            toAdd = new Array<Entity>();

            toAdd.add(new Boid(new Vector2(worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(worldWidth / 2f, -worldHeight / 2f)));
            toAdd.add(new Boid(new Vector2(-worldWidth / 2f, -worldHeight / 2f)));

            toSpawn.put(i, toAdd);
        }

        SpringMassGrid g = new SpringMassGrid(
                new Vector2(model.WORLD_WIDTH,
                        model.WORLD_HEIGHT),
                0.2f);

        model.setGrid(g);

        m = model;
    }


    public static WorldModel getWorldModel()
    {
        return m;
    }

    public static void resetWorldModel()
    {
        if(m != null) m.dispose();
        m = null;
    }

    public static boolean isLoaded()
    {
        return m != null;
    }

}
