import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Vector3d;

public class OrbitalBody {
	
	/* 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	String name;
	float mass;
	
	//https://docs.oracle.com/cd/E17802_01/j2se/javase/technologies/desktop/java3d/forDevelopers/j3dapi/javax/vecmath/Vector3d.html
	// Index 0 = x, 1 = y, 2 = z
	Vector3d posVect = new Vector3d();
	Vector3d velVect = new Vector3d();
	Vector3d accVect = new Vector3d();
	

	

	

	
	void setName(String newName){
		name = newName;
	}

	
	void setMass(float newMass){
		mass = newMass;
	}

	void setRadius(float newRadius) { radius = newRadius; }
	
	void setPosition(float newXPos, float newYPos){
		xPosition = newXPos;
		yPosition = newYPos;
	}
	
	void setVelocity(float newXVel, float newYVel){
		xVelocity = newXVel;
		yVelocity = newYVel;
	}
	void setAcceleration(float newXAcc, float newYAcc){
		xAcceleration = newXAcc;
		yAcceleration = newYAcc;
	}
	
	
	void iterateVelocity(float time) {
		//System.out.println("INITIAL X VELOCITY: " + xVelocity);
		//System.out.println("INITIAL Y VELOCITY: " + yVelocity);
		xVelocity = xVelocity + xAcceleration * time;
		yVelocity = yVelocity + yAcceleration * time;
		//System.out.println("FINAL X VELOCITY: " + xVelocity);
		//System.out.println("FINAL Y VELOCITY: " + yVelocity);
	}
	
	void iteratePosition(float time) {
		xPosition = xPosition + xVelocity * time;
		yPosition = yPosition + yVelocity * time;
	}
	float posVect() {
		float distance = (float) Math.sqrt(xPosition*xPosition + yPosition*yPosition);	
		return distance;
	}
	
}
