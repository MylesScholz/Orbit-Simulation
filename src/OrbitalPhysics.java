import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrbitalPhysics {
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList();
	
	public static void main(String [] args)
	{
		
		
		
		
		OrbitalBody planet = new OrbitalBody();
		listOfBodies.add(planet);
		
		
		planet.setName("planet");
		planet.setMass(1);
		planet.setPosition(100,200);
		
		
		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		
		sun.setName("sun");
		sun.setMass(10000);
		sun.setPosition(0,0);
		
		System.out.println(sun.name);
		System.out.println(sun.mass);
		System.out.println(sun.xPosition);
		
		for (int x=0; x< 10; x++){
			float deltaTime = (float) 0.001;
			
			
		}
	}

	private void iterateSimulation(float deltaTime) {
		
		
		for (int i=0; i < listOfBodies.size(); i++){
			
			/*1. Iterate net forces & acceleration for each body*/
			float newXAcc = calculateBodyAcceleration();
			float newYAcc = calculateBodyAcceleration();
			listOfBodies.get(i).setAcceleration(newXAcc, newYAcc);
			
			
			/*2. Iterate velocities*/
			listOfBodies.get(i).iterateVelocity(deltaTime);
			
			/*3. Calculate new positions*/
			listOfBodies.get(i).iteratePosition(deltaTime);
			
			
		}
		
		
		
		
		
		
		
	}
	
	
	private float calculateBodyAcceleration() {
		
		/*Cowell's Method*/
		float acceleration = 0;
		
		
		return acceleration;
	}
	
	
}
