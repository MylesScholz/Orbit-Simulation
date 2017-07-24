import java.util.HashMap;
import java.util.Map;

public class OrbitalPhysics {
	public static void main(String [] args)
	{
		OrbitalBody planet = new OrbitalBody();
		planet.setName("planet");
		planet.setMass(1);
		planet.setPosition(100,200);
		
		
		OrbitalBody sun = new OrbitalBody();
		sun.setName("sun");
		sun.setMass(10000);
		sun.setPosition(0,0);
		
		System.out.println(sun.name);
		System.out.println(sun.mass);
		System.out.println(sun.xPosition);
	}

	
	private float iterateAcceleration() {
		
		/*Cowell's Method*/
		float acceleration = 0;
		
		
		return acceleration;
	}
	
	
}
