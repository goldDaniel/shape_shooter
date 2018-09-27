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
package com.golddaniel.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author wrksttn
 */
public class InputController implements ControllerListener
{
    public static Controller controller = null;
    
    public InputController()
    {
        for(Controller c : Controllers.getControllers())
        {
            connected(c);
        }
    }
    
    public float getAxis(int axisCode)
    {
        float result = 0;
        
        if(ControllerMapping.isXboxController(controller))
        {
            switch(axisCode)
            {
                case ControllerMapping.L_STICK_HORIZONTAL_AXIS:
                    result = controller.getAxis(XboxMapping.L_STICK_HORIZONTAL_AXIS);
                    break;
                case ControllerMapping.L_STICK_VERTICAL_AXIS:
                    result = controller.getAxis(XboxMapping.L_STICK_VERTICAL_AXIS);
                    break;
                case ControllerMapping.R_STICK_HORIZONTAL_AXIS:
                    result = controller.getAxis(XboxMapping.R_STICK_HORIZONTAL_AXIS);
                    break;
                case ControllerMapping.R_STICK_VERTICAL_AXIS:
                    result = controller.getAxis(XboxMapping.R_STICK_VERTICAL_AXIS);
                    break;
                case ControllerMapping.L2:
                    result = controller.getAxis(XboxMapping.L_TRIGGER);
                    break;
                case ControllerMapping.R2:
                    result = controller.getAxis(XboxMapping.R_TRIGGER);
                    break;
            }
        }
        else
        {
            switch(axisCode)
            {
                case ControllerMapping.L_STICK_HORIZONTAL_AXIS:
                    result = controller.getAxis(DS4Mapping.L_STICK_HORIZONTAL_AXIS);
                    break;
                case ControllerMapping.L_STICK_VERTICAL_AXIS:
                    result = controller.getAxis(DS4Mapping.L_STICK_VERTICAL_AXIS);
                    break;
                case ControllerMapping.R_STICK_HORIZONTAL_AXIS:
                    result = controller.getAxis(DS4Mapping.R_STICK_HORIZONTAL_AXIS);
                    break;
                case ControllerMapping.R_STICK_VERTICAL_AXIS:
                    result = controller.getAxis(DS4Mapping.R_STICK_VERTICAL_AXIS);
                    break;
                case ControllerMapping.L2:
                    result = controller.getAxis(DS4Mapping.L2);
                    break;
                case ControllerMapping.R2:
                    result = controller.getAxis(DS4Mapping.R2);
                    break;
            }
        }
        return result;
    }
    
    public boolean buttonDown(int buttonCode)
    {
        boolean result = false;
        
        if(ControllerMapping.isXboxController(controller))
        {
            switch(buttonCode)
            {
                //FACE BUTTONS==============================================
                case ControllerMapping.X:
                    result = controller.getButton(XboxMapping.A);
                    break;
                case ControllerMapping.SQUARE:
                    result = controller.getButton(XboxMapping.X);
                    break;
                case ControllerMapping.CIRCLE:
                    result = controller.getButton(XboxMapping.B);
                    break;
                case ControllerMapping.TRIANGLE:
                    result = controller.getButton(XboxMapping.Y);
                    break;
                case ControllerMapping.CENTRE_BUTTON:
                    result = controller.getButton(XboxMapping.GUIDE);
                    break;
                case ControllerMapping.SELECT:
                    result = controller.getButton(XboxMapping.BACK);
                    break;
                case ControllerMapping.START:
                    result = controller.getButton(XboxMapping.START);
                    break;
                //==========================================================
                    
                    
                //D-PAD=====================================================
                case ControllerMapping.DPAD_DOWN:
                    result = controller.getButton(XboxMapping.DPAD_DOWN);
                    break;
                case ControllerMapping.DPAD_LEFT:
                    result = controller.getButton(XboxMapping.DPAD_LEFT);
                    break;
                case ControllerMapping.DPAD_UP:
                    result = controller.getButton(XboxMapping.DPAD_UP);
                    break;
                case ControllerMapping.DPAD_RIGHT:
                    result = controller.getButton(XboxMapping.DPAD_RIGHT);
                    break;
                //==========================================================
                    
                    
                //BUMPERS===================================================
                case ControllerMapping.L1:
                    result = controller.getButton(XboxMapping.L_BUMPER);
                    break;
                case ControllerMapping.R1:
                    result = controller.getButton(XboxMapping.R_BUMPER);
                    break;
                //==========================================================
                    
                    
                //STICK BUTTONS=============================================
                case ControllerMapping.L3:
                    result = controller.getButton(XboxMapping.L3);
                    break;
                case ControllerMapping.R3:
                    result = controller.getButton(XboxMapping.R3);
                    break;
                //==========================================================
                
            }
        }
        else
        {
            switch(buttonCode)
            {
               //FACE BUTTONS==============================================
                case ControllerMapping.X:
                    result = controller.getButton(DS4Mapping.X);
                    break;
                case ControllerMapping.SQUARE:
                    result = controller.getButton(DS4Mapping.SQUARE);
                    break;
                case ControllerMapping.CIRCLE:
                    result = controller.getButton(DS4Mapping.CIRCLE);
                    break;
                case ControllerMapping.TRIANGLE:
                    result = controller.getButton(DS4Mapping.TRIANGLE);
                    break;
                case ControllerMapping.CENTRE_BUTTON:
                    result = controller.getButton(DS4Mapping.PS_BUTTON);
                    break;
                case ControllerMapping.SELECT:
                    result = controller.getButton(DS4Mapping.SHARE);
                    break;
                case ControllerMapping.START:
                    result = controller.getButton(DS4Mapping.OPTIONS);
                    break;
                //==========================================================


                //D-PAD=====================================================
                case ControllerMapping.DPAD_DOWN:
                    result = controller.getButton(DS4Mapping.DPAD_DOWN);
                    break;
                case ControllerMapping.DPAD_LEFT:
                    result = controller.getButton(DS4Mapping.DPAD_LEFT);
                    break;
                case ControllerMapping.DPAD_UP:
                    result = controller.getButton(DS4Mapping.DPAD_UP);
                    break;
                case ControllerMapping.DPAD_RIGHT:
                    result = controller.getButton(DS4Mapping.DPAD_RIGHT);
                    break;
                //==========================================================


                //BUMPERS===================================================
                case ControllerMapping.L1:
                    result = controller.getButton(DS4Mapping.L1);
                    break;
                case ControllerMapping.R1:
                    result = controller.getButton(DS4Mapping.R1);
                    break;
                //==========================================================


                //STICK BUTTONS=============================================
                case ControllerMapping.L3:
                    result = controller.getButton(DS4Mapping.L3);
                    break;
                case ControllerMapping.R3:
                    result = controller.getButton(DS4Mapping.R3);
                    break;
            //========================================================== 
            }
            
        }
        
        return result;
    }
    
    @Override
    public final void connected(Controller cntrlr)
    {
        if(ControllerMapping.isXboxController(cntrlr))
        {
            Gdx.app.log("INPUT", "XBOX CONTROLLER CONNECTED");
        }
        else
        {
            Gdx.app.log("INPUT", "OTHER CONTROLLER CONNECTED");
        }
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
