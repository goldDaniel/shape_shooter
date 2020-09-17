package com.golddaniel.core.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.entities.Player;

public class DesktopInputController extends PlayerInputController implements ControllerListener
{

    private PlayerInputController currentController;

    public DesktopInputController(Player player)
    {
        super(player);
        Controllers.addListener(this);

        if(Controllers.getControllers().size > 0)
        {
            currentController = new PS4InputController(player, Controllers.getControllers().get(0));
        }
        else
        {
            currentController = new KeyboardInputController(player, InputConfig.DEFAULT);
        }
    }

    @Override
    public boolean keyDown (int keycode)
    {
        return currentController.keyDown(keycode);
    }

    @Override
    public boolean keyUp (int keycode)
    {
        return currentController.keyUp(keycode);
    }

    @Override
    public void update()
    {
        currentController.update();
    }

    @Override
    public void connected(Controller controller)
    {
        currentController = new PS4InputController(player, controller);
    }

    @Override
    public void disconnected(Controller controller)
    {
        currentController = new KeyboardInputController(player, InputConfig.DEFAULT);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
