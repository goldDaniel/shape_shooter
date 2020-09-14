package com.golddaniel.core.input;

import com.badlogic.gdx.InputAdapter;
import com.golddaniel.entities.Player;

public abstract class PlayerInputController extends InputAdapter
{
    protected Player player;

    public PlayerInputController(Player player)
    {
        this.player = player;
    }

    public abstract void update();
}
