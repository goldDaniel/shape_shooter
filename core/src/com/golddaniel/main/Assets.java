package com.golddaniel.main;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets<T>
{
    AssetManager manager;

    public final String TEXTURE_SQUARE = "texture.png";

    public Assets()
    {
        manager = new AssetManager();

        manager.load(TEXTURE_SQUARE, Texture.class);

    }

    public T get(String id)
    {
        return manager.get(id);
    }

    public float getProgress()
    {
        return manager.getProgress();
    }

    public boolean update()
    {
        return manager.update();
    }
}
