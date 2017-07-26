
public class Vector3 {
	double x;
	double y;
	double z;
	
	double getX() {
		return x;
	}
	
	double getY() {
		return y;
	}
	
	double getZ() {
		return z;
	}	
	
	
	double length() {
		double length = Math.sqrt(x*x + y*y + z*z);		
		return length;
	}
	
	void add(Vector3 otherVector) {
		this.x += otherVector.x;
		this.y += otherVector.y;
		this.z += otherVector.z;
	}
	
	void scale(double scalar){
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
	}
	
	double[] get() {
		double[] values = {x, y, z};
		return values;
	}
	
	
	void set(Vector3 newValues) {
		this.x = newValues.x;
		this.y = newValues.y;
		this.z = newValues.z;
	}
	
	void set(double[] newValues) {
		this.x = newValues[0];
		this.y = newValues[1];
		this.z = newValues[2];
	}
	
	void set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
