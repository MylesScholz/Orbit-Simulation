package com.nwapw.orbitalsimulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class OrbitalBody {
	
	// 0 = Rectangular, 1 = Euler's, 2 = Range-Kutta
	static int integrationMethod = 0;
	
	String name;
	
	float mass;
	float radius;
	
	// x = 0, y = 1, z = 2, stored as double
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
	
	void setPosition(double x, double y, double z){
		posVect.set(x,y,z);	
	}	
	
	void setVelocity(double x, double y, double z){
		velVect.set(x,y,z);
	}
	
	void setAcceleration(double x, double y, double z){
		accVect.set(x,y,z);
	}

	void setBody(String newName, float newMass, double[] position, double[] velocity) {
		name = newName;
		mass = newMass;
		posVect.set(position);	
		velVect.set(velocity);	
	}
	void setSpriteWidth(float newSpriteWidth){
		this.spriteWidth = newSpriteWidth;
	}
	
	
	// TODO create integrator choice picker
	double integratorChoice(double value, double deltaTime) {
		if (integrationMethod == 0){
			//rectangular method
		}
		
		
		return value;
	}
	
	void iterateVelThenPos(float deltaTime) {
		double[] currentPos = new double[3];
		double[] currentVel = new double[3];
		double[] currentAcc = new double[3];
		
		currentPos = posVect.get();
		currentVel = velVect.get();
		currentAcc = accVect.get();
		
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
		if (RunSimulation.listOfBodies.size() % 2 == 0){		
			currentPos[0] *= -1;
			currentPos[1] *= -1;
			currentPos[2] *= -1;
		}
		
		posVect.set(currentPos);
		/*
		if (this.name == "Planet #1") {
			System.out.println("AFTER  " + currentPos[0]);
			System.out.println("AFTER1 " + posVect.getX());
		}		
		*/
		

	}
	
	
}