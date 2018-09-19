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

/**
 *
 * @author wrksttn
 */
public class DS4Mapping
{
    // Buttons
    public static final int X;
    public static final int CIRCLE;
    public static final int TRIANGLE;
    public static final int SQUARE;
    public static final int PS_BUTTON;
    public static final int L1;
    public static final int R1;
    public static final int SHARE;
    public static final int OPTIONS;
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
    public static final int L2;
    /**
     * right trigger, -1 if not pressed, 1 if pressed, 0 is initial value
     **/
    public static final int R2;
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

    static 
    {
        CIRCLE = 1;
        SQUARE = 3;
        X = 0;
        TRIANGLE = 2;
        L1 = 4;
        R1 = 5;
        PS_BUTTON = 10;
        OPTIONS = 9;
        SHARE = 8;
        DPAD_UP = 10;
        DPAD_DOWN = 12;
        DPAD_LEFT = 13;
        DPAD_RIGHT = 11;
        L2 = 6; // postive value
        R2 = 7; // negative value
        L_STICK_VERTICAL_AXIS = 1; // Down = -1, up = 1
        L_STICK_HORIZONTAL_AXIS = 0; // Left = -1, right = 1
        R_STICK_VERTICAL_AXIS = 4; // Assume same story
        R_STICK_HORIZONTAL_AXIS = 3;
        L3 = 11;
        R3 = 12;
    }
}
