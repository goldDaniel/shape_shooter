package com.golddaniel.main;

import com.badlogic.gdx.utils.SharedLibraryLoader;

public class PS4Map
{


    public static final int AXIS_LEFT_HORIZONTAL;
    public static final int AXIS_LEFT_VERTICAL;
    public static final int AXIS_RIGHT_HORIZONTAL;
    public static final int AXIS_RIGHT_VERTICAL;

    static
    {
        if (SharedLibraryLoader.isWindows)
        {
            AXIS_LEFT_HORIZONTAL = 3;
            AXIS_LEFT_VERTICAL = 2;

            AXIS_RIGHT_HORIZONTAL = 1;
            AXIS_RIGHT_VERTICAL = 0;
        }
        else if(SharedLibraryLoader.isLinux)
        {
            AXIS_LEFT_HORIZONTAL = 0;
            AXIS_LEFT_VERTICAL = 1;

            AXIS_RIGHT_HORIZONTAL = 3;
            AXIS_RIGHT_VERTICAL = 4;
        }
        else
        {
            AXIS_LEFT_VERTICAL = -1;
            AXIS_LEFT_HORIZONTAL = -1;
            AXIS_RIGHT_HORIZONTAL = -1;
            AXIS_RIGHT_VERTICAL = -1;
        }
    }

}