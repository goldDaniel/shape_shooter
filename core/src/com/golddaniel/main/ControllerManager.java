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

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author wrksttn
 */
public class ControllerManager implements ControllerListener
{
    public static Controller controller = null;
    
    public ControllerManager()
    {
        for(Controller c : Controllers.getControllers())
        {
            connected(c);
        }
    }
    
    public static boolean isControllerConnected(Controller controller)
    {
        return Controllers.getControllers().contains(controller, true);
    }
    
    @Override
    public final void connected(Controller cntrlr)
    {
        System.out.println("CONTROLLER CONNECTED");
        if(controller == null)
        {
            controller = cntrlr;
        }
    }

    @Override
    public void disconnected(Controller cntrlr)
    {
        System.out.println("CONTROLLER DISCONNECTED");
        if(controller == cntrlr)
        {
            controller = null;
        }
    }

    @Override
    public boolean buttonDown(Controller cntrlr, int i)
    {
        System.out.println(i);
        return true;
    }

    @Override
    public boolean buttonUp(Controller cntrlr, int i)
    {
        
        return true;
    }

    @Override
    public boolean axisMoved(Controller cntrlr, int i, float f)
    {
        return true;
    }

    @Override
    public boolean povMoved(Controller cntrlr, int i, PovDirection pd)
    {
        return true;
    }

    @Override
    public boolean xSliderMoved(Controller cntrlr, int i, boolean bln)
    {
        return true;
    }

    @Override
    public boolean ySliderMoved(Controller cntrlr, int i, boolean bln)
    {
        return true;
    }

    @Override
    public boolean accelerometerMoved(Controller cntrlr, int i, Vector3 vctr)
    {
        return true;
    }
}
