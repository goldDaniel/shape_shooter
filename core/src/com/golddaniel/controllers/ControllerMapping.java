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

import com.badlogic.gdx.controllers.Controller;

/**
 *
 * @author wrksttn
 */
public class ControllerMapping
{
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

/**
 *
 * @author wrksttn
 */

    // Buttons
    public static final int X = 0;
    public static final int CIRCLE = 2;
    public static final int TRIANGLE = 3;
    public static final int SQUARE = 1;
    public static final int CENTRE_BUTTON = 4;
    public static final int L1 = 5;
    public static final int R1 = 6;
    public static final int SELECT = 7;
    public static final int START = 8;
    public static final int DPAD_UP = 9;
    public static final int DPAD_DOWN = 10;
    public static final int DPAD_LEFT = 11;
    public static final int DPAD_RIGHT = 12;
    public static final int L3 = 13;
    public static final int R3 = 14;


    // Axes
    /**
     * left stick vertical axis, -1 if up, 1 if down
     **/
    public static final int L_STICK_VERTICAL_AXIS = 0;
    /**
     * left stick horizontal axis, -1 if left, 1 if right
     **/
    public static final int L_STICK_HORIZONTAL_AXIS = 1;
    /**
     * right stick vertical axis, -1 if up, 1 if down
     **/
    public static final int R_STICK_VERTICAL_AXIS = 2;
    /**
     * right stick horizontal axis, -1 if left, 1 if right
     **/
    public static final int R_STICK_HORIZONTAL_AXIS = 3;
    /**
     * left trigger, -1 if not pressed, 1 if pressed, 0 is initial value
     **/
    public static final int L2 = 4;
    /**
     * right trigger, -1 if not pressed, 1 if pressed, 0 is initial value
     **/
    public static final int R2 = 5;
    
    /**
    * Different names:
    * - Microsoft PC-joystick driver
    *
    * @param controller
    * @return whether the {@link Controller} is an Xbox controller
    */
    public static boolean isXboxController(Controller controller) {
            return controller.getName().matches("(.*)(X-?[Bb]ox|Microsoft PC-joystick)(.*)");
    }
}
