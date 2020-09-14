package com.golddaniel.core.input;

import com.badlogic.gdx.InputAdapter;
import com.golddaniel.entities.Player;

/**
 * Handles our player input & updates the player data accordingly
 */
public abstract class PlayerInputController extends InputAdapter
{
    protected Player player;

    public PlayerInputController(Player player)
    {
        this.player = player;
    }

    public abstract void update();
}
