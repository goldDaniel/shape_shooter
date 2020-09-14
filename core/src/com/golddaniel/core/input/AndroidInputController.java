package com.golddaniel.core.input;

import com.badlogic.gdx.math.Vector2;
import com.golddaniel.entities.Player;

/**
 * controls player input for android platform
 */
public class AndroidInputController extends PlayerInputController
{
    public static Vector2 moveDir = new Vector2();
    public static Vector2 shootDir =  new Vector2();

    public AndroidInputController(Player player)
    {
        super(player);
    }

    @Override
    public void update()
    {
        player.getMoveDir().set(moveDir);
        player.getShootDir().set(shootDir);
    }
}
