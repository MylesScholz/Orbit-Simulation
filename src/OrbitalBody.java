import javax.vecmath.Vector3d;

public class OrbitalBody {
	
	// 0 = Rectangular, 1 = Euler's, 2 = Range-Kutta
	static int integrationMethod = 0;
	
	String name;
	float mass;
	
	// x = 0, y = 1, z = 2, stored as double
	Vector3d posVect = new Vector3d();
	Vector3d velVect = new Vector3d();
	Vector3d accVect = new Vector3d();
	
	void setName(String newName){
		name = newName;
	}
	
	void setMass(float newMass){
		mass = newMass;
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
		
		posVect.get(currentPos);
		velVect.get(currentVel);
		accVect.get(currentAcc);
		
		//TODO change to integrator choice
		
		// Integrates Acceleration to Velocity
		currentVel[0] = NumericalIntegration.integrateRect(currentVel[0], currentAcc[0], deltaTime);
		currentVel[1] = NumericalIntegration.integrateRect(currentVel[1], currentAcc[1], deltaTime);
		currentVel[2] = NumericalIntegration.integrateRect(currentVel[2], currentAcc[2], deltaTime);
	
		velVect.set(currentVel);
	
		// Integrates Velocity to Position
		
		currentPos[0] = NumericalIntegration.integrateRect(currentPos[0], currentVel[0], deltaTime);
		currentPos[1] = NumericalIntegration.integrateRect(currentPos[1], currentVel[1], deltaTime);
		currentPos[2] = NumericalIntegration.integrateRect(currentPos[2], currentVel[2], deltaTime);		

		posVect.set(currentPos);
	
		
	}
	
}
