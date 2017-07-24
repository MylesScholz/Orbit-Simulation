
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
	
	/* x or y velocity*/
	private float iterateVelocity(float initVelocity, float acceleration, float time) {
		float velocity = initVelocity + 1/2 * acceleration * time;
		return velocity;
	}
	
}
