package com.golddaniel.core.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.golddaniel.entities.Player;

/**
 * controls player input for desktop platforms
 */
public class KeyboardInputController extends PlayerInputController
{
    private IntMap<Boolean> inputMap;
    private InputConfig inputConfig;

    public KeyboardInputController(Player player, InputConfig inputConfig)
    {
        super(player);
        this.inputConfig = inputConfig;

        inputMap = new IntMap<Boolean>();

        inputMap.put(inputConfig.MOVE_LEFT, false);
        inputMap.put(inputConfig.MOVE_RIGHT, false);
        inputMap.put(inputConfig.MOVE_UP, false);
        inputMap.put(inputConfig.MOVE_DOWN, false);

        inputMap.put(inputConfig.SHOOT_LEFT, false);
        inputMap.put(inputConfig.SHOOT_RIGHT, false);
        inputMap.put(inputConfig.SHOOT_UP, false);
        inputMap.put(inputConfig.SHOOT_DOWN, false);
    }

    public void update()
    {
        Vector2 moveDir = player.getMoveDir().setZero();

        if(inputMap.get(inputConfig.MOVE_LEFT))
        {
            moveDir.x -= 1;
        }
        if(inputMap.get(inputConfig.MOVE_RIGHT))
        {
            moveDir.x += 1;
        }
        if(inputMap.get(inputConfig.MOVE_UP))
        {
            moveDir.y += 1;
        }
        if(inputMap.get(inputConfig.MOVE_DOWN))
        {
            moveDir.y -= 1;
        }

        Vector2 shootDir = player.getShootDir().setZero();
        if(inputMap.get(inputConfig.SHOOT_LEFT))
        {
            shootDir.x -= 1;
        }
        if(inputMap.get(inputConfig.SHOOT_RIGHT))
        {
            shootDir.x += 1;
        }
        if(inputMap.get(inputConfig.SHOOT_UP))
        {
            shootDir.y += 1;
        }
        if(inputMap.get(inputConfig.SHOOT_DOWN))
        {
            shootDir.y -= 1;
        }

        if(moveDir.len2() > 0) moveDir.nor();
        if(shootDir.len2() > 0) shootDir.nor();
    }


    @Override
    public boolean keyDown (int keycode)
    {
        inputMap.put(keycode, true);
        return false;
    }

    @Override
    public boolean keyUp (int keycode)
    {
        inputMap.put(keycode, false);
        return false;
    }
}
