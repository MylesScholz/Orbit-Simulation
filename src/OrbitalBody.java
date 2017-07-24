import java.util.ArrayList;
import java.util.List;

public class OrbitalBody {
	

	
	String name;
	
	float mass;
	
	float xPosition;
	float yPosition;
	
	float xVelocity;
	float yVelocity;
	
	float xAcceleration;
	float yAcceleration;
	

	
	void setName(String newName){
		name = newName;
	}

	
	void setMass(float newMass){
		mass = newMass;
	}
	
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
		xVelocity = xVelocity + xAcceleration * time;
		yVelocity = yVelocity + yAcceleration * time;
	}
	
	void iteratePosition(float time) {
		xPosition = xPosition + xVelocity * time;
		yPosition = yPosition + yPosition * time;
	}
	float posVect() {
		float distance = Math.sqrt(xPosition*xPosition + yPosition*yPosition);	
		return distance;
	}
	
}
