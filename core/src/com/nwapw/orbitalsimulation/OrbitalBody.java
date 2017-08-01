package com.nwapw.orbitalsimulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class OrbitalBody {
	
	// 0 = Rectangular, 1 = Euler's, 2 = Range-Kutta
	static int integrationMethod = 0;
	
	String name;
	
	String mostPullingBodyName;
	float mostPullingBodyAcc;
	
	float mass;
	float radius;
	
	static float[] currentPos = new float[3];
	float[] currentVel = new float[3];
	float[] currentAcc = new float[3];

	// x = 0, y = 1, z = 2, stored as float
	Vector3 oldPosVect = new Vector3();
	Vector3 posVect = new Vector3();
	Vector3 velVect = new Vector3();
	Vector3 accVect = new Vector3();
	
	Sprite sprite;
	
	float spriteWidth = 10;
	
	Texture texture;

	void setName(String newName){
		this.name = newName;
	}
	
	void setMass(float newMass){
		this.mass = newMass;
	}

	void setRadius(float newRadius) { 
		this.radius = newRadius;
	}
	
	void setTexture(Texture newTexture){
		this.texture = newTexture;
	}	
	
	void setOldPosition(float x, float y, float z) {
		oldPosVect.set(x,y,z);
	}
	
	void setPosition(float x, float y, float z){
		posVect.set(x,y,z);	
	}	
	
	void setVelocity(float x, float y, float z){
		velVect.set(x,y,z);
	}
	
	void setAcceleration(float x, float y, float z){
		accVect.set(x,y,z);
	}

	void setBody(String newName, float newMass, float[] position, float[] velocity) {
		name = newName;
		mass = newMass;
		posVect.set(position);	
		velVect.set(velocity);	
	}
	void setSpriteWidth(float newSpriteWidth){
		this.spriteWidth = newSpriteWidth;
	}
	
	
	// TODO create integrator choice picker
	float integratorChoice(float value, float deltaTime) {
		if (integrationMethod == 0){
			//rectangular method
		}
		
		
		return value;
	}
	
	void iterateVelThenPos(float deltaTime) {
		oldPosVect.set(currentPos);
		
		float[] currentPos = {posVect.x, posVect.y, posVect.z};
		float[] currentVel = {velVect.x, velVect.y, velVect.z};
		float[] currentAcc = {accVect.x, accVect.y, accVect.z};
		
		//TODO change to integrator choice
		
		// Integrates Acceleration to Velocity
		/*
		if (this.name == "Planet #1") {
			System.out.println("BEFORE " + currentPos[0]);
		}
		*/
		currentVel[0] = NumericalIntegration.integrateRect(currentVel[0], currentAcc[0], deltaTime);
		currentVel[1] = NumericalIntegration.integrateRect(currentVel[1], currentAcc[1], deltaTime);
		currentVel[2] = NumericalIntegration.integrateRect(currentVel[2], currentAcc[2], deltaTime);
	
		velVect.set(currentVel);

		// Integrates Velocity to Position
		
		currentPos[0] = NumericalIntegration.integrateRect(currentPos[0], currentVel[0], deltaTime);
		currentPos[1] = NumericalIntegration.integrateRect(currentPos[1], currentVel[1], deltaTime);
		currentPos[2] = NumericalIntegration.integrateRect(currentPos[2], currentVel[2], deltaTime);		
		
		// TODO quickfix bug, find reason why
		//evenBodyBug();
		posVect.set(currentPos);
		/*
		if (this.name == "Planet #1") {
			System.out.println("AFTER  " + currentPos[0]);
			System.out.println("AFTER1 " + posVect.getX());
		}		 
		*/
	}
	
	//public static void evenBodyBug () {
	//	if (RunSimulation.listOfBodies.size() % 2 == 0){		
	//		System.out.println("Even Body Bug");
	//		currentPos[0] *= -1;
	//		currentPos[1] *= -1;
	//		currentPos[2] *= -1;
	//	}
	//}
}