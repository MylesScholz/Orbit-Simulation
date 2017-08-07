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
	// x = 0, y = 1, z = 2, stored as float	
	Vector3 currentPos = new Vector3();
	Vector3 currentVel = new Vector3();
	Vector3 currentAcc = new Vector3();
	
	
	//Leap Frog
	Vector3 backhalfstepVel = new Vector3();
	Vector3 forwardhalfstepVel = new Vector3();
	
	Vector3 prevstepPos = new Vector3();
	
	

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
	
	void integrateEuler(float deltaTime) {
		
		currentPos.set(posVect.x, posVect.y, posVect.z);
		currentVel.set(velVect.x, velVect.y, velVect.z);
		currentAcc.set(accVect.x, accVect.y, accVect.z);
		
		oldPosVect.set(currentPos);
		
		// Integrates Acceleration to Velocity
		currentVel = currentVel.add(currentAcc.scl(deltaTime));
		velVect.set(currentVel);

		// Integrates Velocity to Position
		currentPos = currentPos.add(currentVel.scl(deltaTime));
		posVect.set(currentPos);

	}
	
	void integrateLeapfrog(float deltaTime){
		
		prevstepPos.set(posVect);
		oldPosVect.set(prevstepPos); // for drawing orbit lines

		backhalfstepVel.set(velVect); // velocity is actually one half dt behind position
		
		Vector3 nextstepPos = prevstepPos.add(backhalfstepVel.scl(deltaTime));
	

		forwardhalfstepVel = backhalfstepVel.add(accVect.scl(deltaTime));
			
		posVect.set(nextstepPos);		
		velVect.set(forwardhalfstepVel);
		
	}	
	void integrateLeapfrogPos(float deltaTime){
		oldPosVect.set(posVect); // for drawing orbit lines
		prevstepPos.set(posVect);
		posVect.set(prevstepPos.add(backhalfstepVel.scl(deltaTime)));
		
	}	
	void integrateLeapfrogVel(float deltaTime){		
		backhalfstepVel.set(velVect);
		velVect.set(backhalfstepVel.add(accVect.scl(deltaTime)));
		
		
	}	
}