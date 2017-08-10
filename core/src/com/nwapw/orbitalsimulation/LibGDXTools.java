package com.nwapw.orbitalsimulation;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

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
	
	static void bodyCreate(String name, float mass, float posX, float posY, float velX, float velY) {
		OrbitalBody body = new OrbitalBody();
		body.setMass(mass);
		body.setName(name);
		
		body.setRadius((int) Math.sqrt(mass * 10/ Math.PI));
		body.spriteWidth = (int) Math.sqrt(mass * 10/ Math.PI) * 2;

		body.posVect.x = posX;
		body.posVect.y = posY;

		body.velVect.x = velX;
		body.velVect.y = velY;

		body.setGravity(true);
		body.setPredictedGravity(true);
		body.setRemoved(false);
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
	}
	
	static void bodyCreatePlanet(float posX, float posY, float velX, float velY) {
		OrbitalBody body = new OrbitalBody();
		float mass = 1 + (int)(Math.random() * 4);
		body.setMass(mass);
		body.setName(LibGDXTools.nameGen());
		
		body.setRadius((int) Math.sqrt(((mass * 10) / Math.PI)));
		body.spriteWidth = (int) Math.sqrt(((1 + (int)(Math.random() * 4)) * 10) / Math.PI) * 2;

		body.posVect.x = posX;
		body.posVect.y = posY;

		body.velVect.x = velX;
		body.velVect.y = velY;

		body.setGravity(false);
		body.setPredictedGravity(true);
		body.setRemoved(false);
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
	}
	
	static void bodyCreateSun(float posX, float posY, float velX, float velY) {
		OrbitalBody body = new OrbitalBody();
		float mass = 10000 + (int)(Math.random() * 40000);
		body.setMass(mass);
		body.setName(LibGDXTools.nameGen());
		
		body.setRadius((int) Math.sqrt(mass / Math.PI));
		body.spriteWidth = (int) Math.sqrt(mass / Math.PI) * 2;

		body.posVect.x = posX;
		body.posVect.y = posY;

		body.velVect.x = velX;
		body.velVect.y = velY;

		body.setGravity(false);
		body.setPredictedGravity(true);
		body.setRemoved(false);
		
		RunSimulation.listOfBodies.add(body);
		body.setTexture(bodyTextureChooser(mass));
	}
	
	static void bodyRender() {
		
	}

	static String nameGen() {
	    Random rand = new Random();
        String[] consonants = new String[29];
        String[] vowels = new String[20];
        String[] syllables = new String[580];
        String[] suffix = new String[20];
        int iteration = -1;
        String name = "";

        consonants[0] = "b";
        consonants[1] = "c";
        consonants[2] = "d";
        consonants[3] = "f";
        consonants[4] = "g";
        consonants[5] = "h";
        consonants[6] = "j";
        consonants[7] = "k";
        consonants[8] = "l";
        consonants[9] = "m";
        consonants[10] = "n";
        consonants[11] = "p";
        consonants[12] = "qu";
        consonants[13] = "r";
        consonants[14] = "s";
        consonants[15] = "t";
        consonants[16] = "v";
        consonants[17] = "w";
        consonants[18] = "x";
        consonants[19] = "y";
        consonants[20] = "z";
        consonants[21] = "ch";
        consonants[22] = "ph";
        consonants[23] = "kn";
        consonants[24] = "wr";
        consonants[25] = "sh";
        consonants[26] = "wh";
        consonants[27] = "th";
        consonants[28] = "ng";

        
        
        vowels[0] = "a";
        vowels[1] = "e";
        vowels[2] = "i";
        vowels[3] = "o";
        vowels[4] = "u";
        vowels[5] = "y";
        vowels[6] = "oo";
        vowels[7] = "ee";
        vowels[8] = "ea";
        vowels[9] = "ie";
        vowels[10] = "ei";
        vowels[11] = "ue";
        vowels[12] = "oi";
        vowels[13] = "ir";
        vowels[14] = "ei";
        vowels[15] = "an";
        vowels[16] = "en";
        vowels[17] = "in";
        vowels[18] = "un";
        vowels[19] = "yn";
        
        
        suffix[0] = "Alpha ";
        suffix[1] = "Beta ";
        suffix[2] = "Gamma ";
        suffix[3] = "Delta ";
        suffix[4] = "Epsilon ";
        suffix[5] = "Zeta ";
        suffix[6] = "Eta ";
        suffix[7] = "Theta ";
        suffix[8] = "Iota ";     
        suffix[9] = "Kappa ";
        suffix[10] = "Lambda ";
        suffix[11] = "Mu ";
        suffix[12] = "Nu ";
        suffix[13] = "Malahupitin ";
        suffix[14] = "Pi ";    
        suffix[15] = "Zeta ";
        suffix[16] = "Eta ";
        suffix[17] = "Theta ";
        suffix[18] = "Iota ";     
        suffix[19] = "Kappa ";       
        
        
        for (int i = 0; i < consonants.length; i++) {
            for (int j = 0; j < vowels.length; j++) {
                iteration++;
                syllables[iteration] = consonants[i] + vowels[j];
            }
        }
       
       
        int randomSyllableLength = (int) Math.floor(Math.abs(Math.random() - Math.random()) * (1 + 5 - 1) + 1);
        
        String hyphenSpaceInput[] = new String[randomSyllableLength];
        
        hyphenSpaceInput[randomSyllableLength-1] = "";
        for (int j = 0; j < randomSyllableLength - 1; j++){
        	hyphenSpaceInput[j] = "";
        	if (Math.random() < 0.1){     		
        		if (Math.random() < 0.4){
        			hyphenSpaceInput[j] = "-";
        		}
        		else {
        			hyphenSpaceInput[j] = " ";
        		}
        		
        	}
        }
        
        
        for (int i = 0; i < randomSyllableLength; i++) {
            name += syllables[rand.nextInt(580)];
            name += hyphenSpaceInput[i];
            
        }

        name = name.substring(0,1).toUpperCase() + name.substring(1);

        if (Math.random() < 0.02){
        	int randInt =  random.nextInt(19) + 0;
        	name = suffix[randInt] + name;   	
        }
        return name;
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
		else if (mass < 2000000){ // body is a "main sequence star"
            int listLength = RunSimulation.availableStarTextures.size();
            int randTexture = random.nextInt(listLength);

            newTexture = RunSimulation.availableStarTextures.get(randTexture);
            //RunSimulation.availablePlanetTextures.remove(randTexture);
		}
		else { // body is a blackhole
			newTexture = new Texture("blackhole.png");

			
		}
		
		RunSimulation.runningTextures.add(newTexture);
		
		return newTexture;
	}
	
	static float spriteRadiusCalculator(float mass){
		
		float length = 1;

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
	static String underlineCalculation(String title) {
		String border = "_";
		for(int i = 0; i < title.length(); i++){
			border += "_";
		}
		return border;
	}
	static float calculateDefaultZoom(float spriteWidth){		
		float zF = 0;
		

		if (spriteWidth < 30) {
			zF = 10 - 1f*spriteWidth;
		}		
		else if (spriteWidth < 100){
			zF = 2f;
		}
		else if (spriteWidth < 500){
			zF = 1f;
		}
		else if (spriteWidth < 1000){
			zF = 0.5f;
		}
		else if (spriteWidth < 5000){
			zF = 0.25f;
		}
		else if (spriteWidth < 10000) {
			zF = 0.1f;
		}
		else if (spriteWidth < 50000) {
			zF = 0.1f;
		}
		else {
			zF = 0.01f;
		}

		
		return zF;
	}

}
