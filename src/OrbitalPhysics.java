import java.util.HashMap;
import java.util.Map;

public class OrbitalPhysics {
	public static void main(String [] args)
	{
	
	}
	private float iterateVelocity(float initVelocity, float acceleration, float time) {
		float velocity = initVelocity + 1/2 * acceleration * time;
		return velocity;
	}
	
	private float iterateAcceleration(HashMap body) {
		
		/*Cowell's Method*/
		float acceleration = 0;
		
		
		return acceleration;
	}
	
	
}
