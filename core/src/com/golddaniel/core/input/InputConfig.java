package com.golddaniel.core.input;

import com.badlogic.gdx.Input;

/**
 * holds the keyboard controls for player input
 * Use Gdx.input.keys when constructing
 */
public class InputConfig
{
    public static final InputConfig DEFAULT = new InputConfig(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S,
                                                              Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN);

    public final int MOVE_LEFT;
    public final int MOVE_RIGHT;
    public final int MOVE_UP;
    public final int MOVE_DOWN;

    public final int SHOOT_LEFT;
    public final int SHOOT_RIGHT;
    public final int SHOOT_UP;
    public final int SHOOT_DOWN;

    public InputConfig(int left, int right, int up, int down,
                       int sLeft, int sRight, int sUp, int sDown)
    {
        MOVE_LEFT = left;
        MOVE_RIGHT = right;
        MOVE_UP = up;
        MOVE_DOWN = down;

        SHOOT_LEFT = sLeft;
        SHOOT_RIGHT = sRight;
        SHOOT_UP = sUp;
        SHOOT_DOWN = sDown;
    }
}
