package com.nwapw.orbitalsimulation;

public class NumericalIntegration {
	/* Used in order to integrate Acceleration -> Velocity -> Position
	 * More complex algorithms -> more accurate
	 * Smaller deltas -> more accurate
	 */
	
	// Rectangular Method
	static float integrateRect(float initial, float slope, float delta) {
		initial += slope * delta;
		//System.out.println("initial " + initial + " slope " + slope + " final " + result);
		return initial;
	}
	
	// Euler's Method
	
	
	
	// Range-Kutta Method (RK4)
	
}
