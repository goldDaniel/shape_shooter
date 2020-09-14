package com.golddaniel.core.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.golddaniel.core.Main;

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
