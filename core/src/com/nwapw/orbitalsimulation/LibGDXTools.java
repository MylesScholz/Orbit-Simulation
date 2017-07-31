package com.nwapw.orbitalsimulation;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LibGDXTools {
	
	static Random random = new Random();
	
	static OrbitalBody bodyInitialize(String name, float mass, float posX, float posY, float velX, float velY){
		
		OrbitalBody body = new OrbitalBody();	
		
		body.setName(name);
		body.setMass(mass);
		
		RunSimulation.listOfBodies.add(body);
		
		Texture bodyTexture = bodyTextureChooser(mass);

		body.setTexture(bodyTexture);
		
		
		float radius = spriteRadiusCalculator(mass);
		body.setRadius(radius);
		body.setSpriteWidth(radius*5);
		
		return body;
	}	
	
	static OrbitalBody bodyInitialize(String name, float mass, float radius, float posX, float posY, float velX, float velY){
		OrbitalBody body = new OrbitalBody();
		
		body.setName(name);
		body.setMass(mass);
		
		body.setRadius(radius);

		body.posVect.x = (float) posX;
		body.posVect.y = (float) posY;
		
		body.velVect.x = velX;
		body.velVect.y = velY;
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
		
		return body;
	}	
	
	static OrbitalBody bodyInitialize(String name, float mass, float radius, float posX, float posY, float velX, float velY, float spriteWidth){
		
		OrbitalBody body = new OrbitalBody();
		
		body.setName(name);
		body.setMass(mass);
		body.setRadius(radius);

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
		
	static Texture bodyTextureChooser(float mass) {
		Texture newTexture;
		//TODO randomly select from available sprites
		if (mass < 100){ // body is a "planet"		
			
			int listLength = RunSimulation.availablePlanetTextures.size();
			int randTexture = random.nextInt(listLength);
			
			newTexture = RunSimulation.availablePlanetTextures.get(randTexture);
			//RunSimulation.availablePlanetTextures.remove(randTexture);
			
			//newTexture = new Texture("planets/planet18.png");
		}
		else if (mass < 150000){ // body is a "main sequence star"
            int listLength = RunSimulation.availableStarTextures.size();
            int randTexture = random.nextInt(listLength);

            newTexture = RunSimulation.availableStarTextures.get(randTexture);
            //RunSimulation.availablePlanetTextures.remove(randTexture);
		}
		else { // body is a "giant" star
			newTexture = new Texture("stars/giant/star_blue_giant01.png");
		}
		
		RunSimulation.runningTextures.add(newTexture);
		
		return newTexture;
	}
	
	static float spriteRadiusCalculator(float mass){
		

		float length = 1;
		/*
		//length = (float) Math.pow(3*mass/(4*Math.PI), 1/3);
		length = (float) (3*mass/(4*Math.PI));
		
		
		System.out.println("mass: " + mass);
		System.out.println("length: " + length);
		*/
		
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
