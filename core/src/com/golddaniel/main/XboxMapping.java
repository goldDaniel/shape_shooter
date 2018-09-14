package com.golddaniel.main;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.SharedLibraryLoader;

public class XboxMapping {
	// Buttons
	public static final int A;
	public static final int B;
	public static final int X;
	public static final int Y;
	public static final int GUIDE;
	public static final int L_BUMPER;
	public static final int R_BUMPER;
	public static final int BACK;
	public static final int START;
	public static final int DPAD_UP;
	public static final int DPAD_DOWN;
	public static final int DPAD_LEFT;
	public static final int DPAD_RIGHT;
	public static final int L3;
	public static final int R3;


	// Axes
	/**
	 * left trigger, -1 if not pressed, 1 if pressed, 0 is initial value
	 **/
	public static final int L_TRIGGER;
	/**
	 * right trigger, -1 if not pressed, 1 if pressed, 0 is initial value
	 **/
	public static final int R_TRIGGER;
	/**
	 * left stick vertical axis, -1 if up, 1 if down
	 **/
	public static final int L_STICK_VERTICAL_AXIS;
	/**
	 * left stick horizontal axis, -1 if left, 1 if right
	 **/
	public static final int L_STICK_HORIZONTAL_AXIS;
	/**
	 * right stick vertical axis, -1 if up, 1 if down
	 **/
	public static final int R_STICK_VERTICAL_AXIS;
	/**
	 * right stick horizontal axis, -1 if left, 1 if right
	 **/
	public static final int R_STICK_HORIZONTAL_AXIS;

	static {
            if (SharedLibraryLoader.isWindows) 
            {
                A = 0;
                B = 1;
                X = 2;
                Y = 3;
                GUIDE = -1;
                L_BUMPER = 4;
                R_BUMPER = 5;
                BACK = 6;
                START = 7;
                DPAD_UP = 10;
                DPAD_DOWN = 12;
                DPAD_LEFT = 13;
                DPAD_RIGHT = 11;
                L_TRIGGER = 2; // postive value
                R_TRIGGER = 2; // negative value
                L_STICK_VERTICAL_AXIS = 1; // Down = -1, up = 1
                L_STICK_HORIZONTAL_AXIS = 0; // Left = -1, right = 1
                R_STICK_VERTICAL_AXIS = 3; // Assume same story
                R_STICK_HORIZONTAL_AXIS = 2;
                L3 = 8;
                R3 = 9;
            } 
            else if (SharedLibraryLoader.isLinux) 
            {
                A = 0;
                B = 1;
                X = 3;
                Y = 4;
                GUIDE = 8;
                L_BUMPER = 6;
                R_BUMPER = 7;
                BACK = 6;
                START = 11;
                DPAD_UP = 7;
                DPAD_DOWN = 7;
                DPAD_LEFT = 6;
                DPAD_RIGHT = 6;
                L_TRIGGER = 2;
                R_TRIGGER = 5;
                L_STICK_VERTICAL_AXIS = 1;
                L_STICK_HORIZONTAL_AXIS = 0;
                R_STICK_VERTICAL_AXIS = 3;
                R_STICK_HORIZONTAL_AXIS = 2;
                L3 = 13;
                R3 = 14;
            } 
            else if (SharedLibraryLoader.isMac) 
            {
                A = 11;
                B = 12;
                X = 13;
                Y = 14;
                GUIDE = 10;
                L_BUMPER = 8;
                R_BUMPER = 9;
                BACK = 5;
                START = 4;
                DPAD_UP = 0;
                DPAD_DOWN = 1;
                DPAD_LEFT = 2;
                DPAD_RIGHT = 3;
                L_TRIGGER = 0;
                R_TRIGGER = 1;
                L_STICK_VERTICAL_AXIS = 3;
                L_STICK_HORIZONTAL_AXIS = 2;
                R_STICK_VERTICAL_AXIS = 5;
                R_STICK_HORIZONTAL_AXIS = 4;
                L3 = -1;
                R3 = -1;
            }
            else 
            {
                A = -1;
                B = -1;
                X = -1;
                Y = -1;
                GUIDE = -1;
                L_BUMPER = -1;
                R_BUMPER = -1;
                L_TRIGGER = -1;
                R_TRIGGER = -1;
                BACK = -1;
                START = -1;
                DPAD_UP = -1;
                DPAD_DOWN = -1;
                DPAD_LEFT = -1;
                DPAD_RIGHT = -1;
                L_STICK_VERTICAL_AXIS = -1;
                L_STICK_HORIZONTAL_AXIS = -1;
                R_STICK_VERTICAL_AXIS = -1;
                R_STICK_HORIZONTAL_AXIS = -1;
                L3 = -1;
                R3 = -1;
            }
	}

	/**
	 * Different names:
	 * - Microsoft PC-joystick driver
	 *
	 * @return whether the {@link Controller} is an Xbox controller
	 */
	public static boolean isXboxController(Controller controller) {
		return controller.getName().matches("(.*)(X-?[Bb]ox|Microsoft PC-joystick)(.*)");
	}
}