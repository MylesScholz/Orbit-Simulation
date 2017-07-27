package com.nwapw.orbitalsimulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LibGDXTools {
	
	static void bodyCreate() {
		
	}
	
	static void bodyRender() {
		
	}
	
	
	static Texture bodyTextureChooser(double mass) {
		
		Texture texture;
		
		//TODO randomly select from available sprites
		if (mass < 100){ // body is a "planet"
			texture = new Texture("planets/planet18.png");
		}
		else if (mass < 1000){ // body is a "main sequence star"
			texture = new Texture("star/mainsequence/star_orange01.png");
		}
		else { // body is a "giant" star
			texture = new Texture("star/giant/star_blue_giant01.png");
		}
		
		return texture;
		
	}
	
	static double bodySizeCalculator(double mass){
		
		double length = 1;;
		
		if (mass < 100){ // body is a "planet"
			length = 50;
		}
		else if (mass < 1000){ // body is a "main sequence star"
			length = 200;
		}
		else { // body is a "giant" star
			length = 300;
		}
		
		return length;
	}
	
}
