package com.golddaniel.main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.golddaniel.main.Main;

public class DesktopLauncher
{
	public static void main (String[] arg)
    {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "!!!!!!!!!!!!!!!SHAPE SHOOTER!!!!!!!!!!!!!!!";

		config.width = 1024;
		config.height = 576;

		new LwjglApplication(new Main(), config);
	}
}
