package com.nwapw.orbitalsimulation;

public class NumericalIntegration {
	/* Used in order to integrate Acceleration -> Velocity -> Position
	 * More complex algorithms -> more accurate
	 * Smaller deltas -> more accurate
	 */
	
	/*
	 * Euler's Method
	 * First-Order - not very accurate
	 */
	static float integrateRect(float initial, float slope, float delta) {
		initial += slope * delta;
		//System.out.println("initial " + initial + " slope " + slope + " final " + result);
		return initial;
	}
		
	/*
	 * RK4 (Range-Kutta)
	 * 4th Order - reduces error
	 * more accurate but creates systematc errors
	 */
	static float integrateRK4(float initial, float slope, float delta) {
		float nextStep;
		
		float k1 = initial;
		float k2 = initial * k1 * delta/2;
		float k3 = initial * k2 * delta/2;
		float k4 = initial * k3 * delta;
		nextStep = initial + (delta/6) * (k1 + 2*k1 + 2*k3 + k4);
		
		
		return nextStep;		
	}
	

	/*
	 * Leapfrog
	 * 2nd Order
	 * Symplectic - "conserves" energy
	 */
	
	
}
