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
	
	static float[] currentPos = new float[3];
	float[] currentVel = new float[3];
	float[] currentAcc = new float[3];

	// x = 0, y = 1, z = 2, stored as float
	Vector3 posVect = new Vector3();
	Vector3 oldPosVect = new Vector3();
	Vector3 predictedOldPosVect = new Vector3();
	Vector3 predictedPosVect = new Vector3();
	Vector3 velVect = new Vector3();
	Vector3 predictedVelVect = new Vector3();
	Vector3 accVect = new Vector3();
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
	
	void iterateVelThenPos(float deltaTime) {
		
		float[] currentPos = {posVect.x, posVect.y, posVect.z};
		float[] currentVel = {velVect.x, velVect.y, velVect.z};
		float[] currentAcc = {accVect.x, accVect.y, accVect.z};
		
		oldPosVect.set(currentPos);
		predictedOldPosVect.set(currentPos);
		
		//TODO change to integrator choice
		
		// Integrates Acceleration to Velocity
		
		currentVel[0] = NumericalIntegration.integrateRect(currentVel[0], currentAcc[0], deltaTime);
		currentVel[1] = NumericalIntegration.integrateRect(currentVel[1], currentAcc[1], deltaTime);
		currentVel[2] = NumericalIntegration.integrateRect(currentVel[2], currentAcc[2], deltaTime);
	
		velVect.set(currentVel);
		predictedVelVect.set(currentVel);

		// Integrates Velocity to Position
		
		currentPos[0] = NumericalIntegration.integrateRect(currentPos[0], currentVel[0], deltaTime);
		currentPos[1] = NumericalIntegration.integrateRect(currentPos[1], currentVel[1], deltaTime);
		currentPos[2] = NumericalIntegration.integrateRect(currentPos[2], currentVel[2], deltaTime);		
		
		posVect.set(currentPos);
		predictedPosVect.set(currentPos);
	}
	
	void predictedIterateVelThenPos(float deltaTime) {
		
		float[] currentPos = {predictedPosVect.x, predictedPosVect.y, predictedPosVect.z};
		float[] currentVel = {predictedVelVect.x, predictedVelVect.y, predictedVelVect.z};
		float[] currentAcc = {predictedAccVect.x, predictedAccVect.y, predictedAccVect.z};
		
		predictedOldPosVect.set(currentPos);
		
		//TODO change to integrator choice
		
		// Integrates Acceleration to Velocity
		
		currentVel[0] = NumericalIntegration.integrateRect(currentVel[0], currentAcc[0], deltaTime);
		currentVel[1] = NumericalIntegration.integrateRect(currentVel[1], currentAcc[1], deltaTime);
		currentVel[2] = NumericalIntegration.integrateRect(currentVel[2], currentAcc[2], deltaTime);
	
		predictedVelVect.set(currentVel);

		// Integrates Velocity to Position
		
		currentPos[0] = NumericalIntegration.integrateRect(currentPos[0], currentVel[0], deltaTime);
		currentPos[1] = NumericalIntegration.integrateRect(currentPos[1], currentVel[1], deltaTime);
		currentPos[2] = NumericalIntegration.integrateRect(currentPos[2], currentVel[2], deltaTime);		
		
		predictedPosVect.set(currentPos);
	}
}