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
	String predictedMostPullingBodyName;
	float mostPullingBodyAcc;
	float predictedMostPullingBodyAcc;
	
	float mass;
	float radius;
	// x = 0, y = 1, z = 2, stored as float	
	Vector3 currentPos = new Vector3();
	Vector3 currentVel = new Vector3();
	Vector3 currentAcc = new Vector3();
	
	
	//Leap Frog
	Vector3 backhalfstepVel = new Vector3();
	Vector3 predictedbackhalfstepVel = new Vector3();
	//Vector3 forwardhalfstepVel = new Vector3();
	//Vector3 predictedforwardhalfstepVel = new Vector3();
	
	Vector3 prevstepPos = new Vector3();
	Vector3 predictedprevstepPos = new Vector3();
	
	

	// x = 0, y = 1, z = 2, stored as float
	Vector3 posVect = new Vector3();
	Vector3 oldPosVect = new Vector3();
	Vector3 predictedOldPosVect = new Vector3();
	Vector3 predictedPosVect = new Vector3();
	Vector3 velVect = new Vector3();
	Vector3 predictedVelVect = new Vector3();
	Vector3 accVect = new Vector3();

	ArrayList<Float> cometTailX = new ArrayList<Float>();
	ArrayList<Float> cometTailY = new ArrayList<Float>();

	Vector3 predictedAccVect = new Vector3();
	
	Sprite sprite;
	
	float spriteWidth = 10;
	
	boolean gravity = true;
	boolean predictedGravity = true;
	boolean removed = false;
	
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
	
	void setPosition(float x, float y, float z){
		posVect.set(x,y,z);	
	}	
	
	void setOldPosition(float x, float y, float z) {
		oldPosVect.set(x,y,z);
	}
	
	void setPredictedOldPosition(float x, float y, float z) {
		predictedOldPosVect.set(x,y,z);
	}
	
	void setPredictedPosition(float x, float y, float z) {
		predictedPosVect.set(x,y,z);
	}
	
	void setVelocity(float x, float y, float z){
		velVect.set(x,y,z);
	}
	
	void setPredictedVelocity(float x, float y, float z) {
		predictedVelVect.set(x,y,z);
	}
	
	void setAcceleration(float x, float y, float z){
		accVect.set(x,y,z);
	}
	
	void setPredictedAcceleration(float x, float y, float z) {
		predictedAccVect.set(x,y,z);
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
	
	void setGravity(boolean setGravity) {
		this.gravity = setGravity;
	}
	
	void setPredictedGravity(boolean setPredictedGravity) {
		this.predictedGravity = setPredictedGravity;
	}
	
	void setRemoved(boolean setRemoved) {
		this.removed = setRemoved;
	}
	
	// TODO create integrator choice picker
	float integratorChoice(float value, float deltaTime) {
		if (integrationMethod == 0){
			//rectangular method
		}
		return value;
	}
	
	void integrateEuler(float deltaTime, int i, OrbitalBody currentBody) {
		
		currentPos.set(posVect.x, posVect.y, posVect.z);
		currentVel.set(velVect.x, velVect.y, velVect.z);
		currentAcc.set(accVect.x, accVect.y, accVect.z);
		
		oldPosVect.set(currentPos);
		predictedOldPosVect.set(currentPos);
		
		// Integrates Acceleration to Velocity
		
		backhalfstepVel.set(velVect);
		currentVel = currentVel.add(currentAcc.scl(deltaTime));
		velVect.set(currentVel);
		predictedVelVect.set(currentVel);

		OrbitalPhysics.cowellsFormulation(i, currentBody);
		
		// Integrates Velocity to Position
		currentPos = currentPos.add(backhalfstepVel.scl(deltaTime));
		posVect.set(currentPos);

	}
	
	void integrateLeapfrogPos(float deltaTime){
		oldPosVect.set(posVect); // for drawing orbit lines
		predictedOldPosVect.set(posVect);
		posVect.set(posVect.add(backhalfstepVel.scl(deltaTime)));
		predictedPosVect.set(posVect.add(backhalfstepVel.scl(deltaTime)));
	}
	
	void integrateLeapfrogVel(float deltaTime){		
		backhalfstepVel.set(velVect);
		velVect.set(backhalfstepVel.add(accVect.scl(deltaTime)));
		predictedVelVect.set(backhalfstepVel.add(accVect.scl(deltaTime)));
	}	
	
	void integratePredictedEuler(float deltaTime, int i, OrbitalBody currentBody) {
		
		currentPos.set(predictedPosVect.x, predictedPosVect.y, predictedPosVect.z);
		currentVel.set(predictedVelVect.x, predictedVelVect.y, predictedVelVect.z);
		currentAcc.set(predictedAccVect.x, predictedAccVect.y, predictedAccVect.z);
		
		predictedOldPosVect.set(currentPos);
		
		// Integrates Acceleration to Velocity

		predictedbackhalfstepVel.set(predictedVelVect);
		currentVel = currentVel.add(currentAcc.scl(deltaTime));
		predictedVelVect.set(currentVel);

		OrbitalPhysics.predictedCowellsFormulation(i, currentBody);
		
		// Integrates Velocity to Position
		currentPos = currentPos.add(predictedbackhalfstepVel.scl(deltaTime));
		predictedPosVect.set(currentPos);
	}
	
	void integratePredictedLeapfrogPos(float deltaTime){
		predictedOldPosVect.set(predictedPosVect); // for drawing orbit lines
		//prevstepPos.set(predictedPosVect);
		predictedPosVect.set(predictedPosVect.add(predictedbackhalfstepVel.scl(deltaTime)));
	}	
	
	void integratePredictedLeapfrogVel(float deltaTime){		
		predictedbackhalfstepVel.set(predictedVelVect);
		predictedVelVect.set(predictedbackhalfstepVel.add(predictedAccVect.scl(deltaTime)));		
	}		
}