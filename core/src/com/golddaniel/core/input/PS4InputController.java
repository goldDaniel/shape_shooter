package com.golddaniel.core.input;

import com.badlogic.gdx.controllers.Controller;
import com.golddaniel.entities.Player;

public class PS4InputController extends PlayerInputController
{
    private Controller controller;

    protected PS4InputController(Player player, Controller controller)
    {
        super(player);
        this.controller = controller;
    }

    @Override
    public void update()
    {

        float moveX = verify(controller.getAxis(PS4Map.AXIS_LEFT_HORIZONTAL));
        float moveY = verify(-controller.getAxis(PS4Map.AXIS_LEFT_VERTICAL));

        float shootX = verify(controller.getAxis(PS4Map.AXIS_RIGHT_HORIZONTAL));
        float shootY = verify(-controller.getAxis(PS4Map.AXIS_RIGHT_VERTICAL));


        player.getMoveDir().set(moveX, moveY);
        player.getShootDir().set(shootX, shootY);
    }

    //validates our deadzone values for analog sticks
    private float verify(float val)
    {
        if(val < 0) val = -val;

        if(val < 0.1f) val = 0;

        return val;
    }
}
