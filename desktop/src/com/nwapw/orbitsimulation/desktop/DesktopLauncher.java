package com.nwapw.orbitsimulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nwapw.orbitsimulation.RunSimulaion;
import com.nwapw.orbitsimulation.RunSimulation;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RunSimulation(), config);
	}
}
