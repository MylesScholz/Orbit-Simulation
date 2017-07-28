package com.nwapw.orbitalsimulation;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.plaf.synth.SynthSeparatorUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LibGDXTools {
	
	static Random random = new Random();
	
	static OrbitalBody bodyInitialize(String name, float mass){
		
		OrbitalBody body = new OrbitalBody();	
		
		body.setName(name);
		body.setMass(mass);
		
		RunSimulation.listOfBodies.add(body);
		
		Texture bodyTexture = bodyTextureChooser(mass);

		body.setTexture(bodyTexture);
		
		body.setSpriteWidth(spriteWidthCalculator(mass));
		
		return body;
	}	
	static OrbitalBody bodyInitialize(String name, float mass, double posX, double posY, double velX, double velY){
		OrbitalBody body = new OrbitalBody();
		
		body.setName(name);
		body.setMass(mass);
		
		body.posVect.x = posX;
		body.posVect.y = posY;
		
		body.velVect.x = velX;
		body.velVect.y = velY;
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
		
		return body;
	}	
	
	static OrbitalBody bodyInitialize(String name, float mass, double posX, double posY, double velX, double velY, float spriteWidth){
		
		OrbitalBody body = new OrbitalBody();
		
		body.setName(name);
		body.setMass(mass);
		
		body.posVect.x = posX;
		body.posVect.y = posY;
		
		body.velVect.x = velX;
		body.velVect.y = velY;
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
		
		body.spriteWidth = spriteWidth;
		
		return body;
	}	
	static void bodyCreate() {
		
	}
	
	static void bodyRender() {
		
	}
		
	static Texture bodyTextureChooser(double mass) {
		Texture newTexture;
		//TODO randomly select from available sprites
		if (mass < 100){ // body is a "planet"		
			
			//int listLength = RunSimulation.availablePlanetTextures.size();
			//int randTexture = random.nextInt(listLength);
			
			//newTexture = RunSimulation.availablePlanetTextures.get(randTexture);
			//RunSimulation.availablePlanetTextures.remove(randTexture);

			newTexture = new Texture("planets/planet18.png");
		}
		else if (mass < 150000){ // body is a "main sequence star"
			newTexture = new Texture("stars/mainsequence/star_orange01.png");
		}
		else { // body is a "giant" star
			newTexture = new Texture("stars/giant/star_blue_giant01.png");
		}
		return newTexture;
	}
	
	static float spriteWidthCalculator(double mass){
		
		float length = 1;;
		
		if (mass < 100){ // body is a "planet"
			length = 20;
		}
		else if (mass < 1000){ // body is a "main sequence star"
			length = 50;
		}
		else { // body is a "giant" star
			length = 60;
		}
		
		return length;
	}
	
}
