package com.nwapw.orbitalsimulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import com.badlogic.gdx.math.Vector3;

public class OrbitalPhysics {
	
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	static Vector3 sumOfAcc = new Vector3();
	static Vector3 predictedSumOfAcc = new Vector3();
	static Vector3 calculatedAcc = new Vector3();
	static Vector3 predictedCalculatedAcc = new Vector3();
	
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	static boolean indexOutOfBounds = false;
	
		
	
	static void iterateSimulation(float deltaTime) {
		// 1. Calculate net force and acceleration from acting on each body.
		for (int i=0; i < listOfBodies.size(); i++) {			
			if (listOfBodies.get(i).gravity) {
				OrbitalBody currentBody = listOfBodies.get(i);
				sumOfAcc.set(0,0,0);
				listOfBodies.get(i).mostPullingBodyAcc = 0;
				for (int j = 0; j < listOfBodies.size() ; j++){
					if (listOfBodies.get(j).gravity) {
						if (j != i){
							OrbitalBody pullingBody = listOfBodies.get(j);
							calculatedAcc.set(0,0,0);
						
							if (perturbationCalculationMethod == 0){ // Cowell's Formulation
								calculatedAcc = cowellsFormulation(currentBody, pullingBody);
								
								if (calculatedAcc.len() >= listOfBodies.get(i).mostPullingBodyAcc){
									listOfBodies.get(i).mostPullingBodyAcc = calculatedAcc.len();
									listOfBodies.get(i).mostPullingBodyName = listOfBodies.get(j).name;
								}
								sumOfAcc.add(calculatedAcc);
							}
							/*
							else if {
								// TODO: Encke's Method, Variation of Parameters, etc.
							}
							*/
						}
					}
				}
				// 2. Iterate and integrate for velocity and then position.

				currentBody.setAcceleration(sumOfAcc.x, sumOfAcc.y, sumOfAcc.z);			
				currentBody.iterateVelThenPos(deltaTime);			
			}
		}	
		checkAllCollisions();
	}	
	
	static Vector3 cowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		Vector3 currentPos = currentBody.posVect;
		Vector3 pullingPos = pullingBody.posVect;
		
		
		Vector3 diffOfPosVect = new Vector3();
		diffOfPosVect.add(pullingPos);
		diffOfPosVect.scl(-1);
		diffOfPosVect.add(currentPos);
		Vector3 calculatedAcc = new Vector3(0,0,0);	
		calculatedAcc.add(diffOfPosVect);
		
		calculatedAcc.scl((float) (-1*gravConst * pullingBody.mass / Math.pow(currentPos.dst(pullingPos), 3)));	
		//calculatedAcc.scl(1/currentBody.mass);
		
		//System.out.println(calculatedAcc);
		/*
		float px1 = currentPos.x;
		float py1 = currentPos.y;
		float px2 = pullingPos.x;
		float py2 = pullingPos.y;
		
		float dx = pullingPos.x - currentPos.x;
		float dy = pullingPos.y - currentPos.y;
		
		float d = (float) Math.sqrt(dx*dx + dy*dy);
		
		float f = gravConst * currentBody.mass * pullingBody.mass / (d*d);
		
		float theta = (float) Math.atan2(dy, dx);
		float fx = (float) Math.cos(theta) * f;
		float fy = (float) Math.sin(theta) * f;
		
		calculatedAcc.x = fx;
		calculatedAcc.y = fy;
		*/
		return calculatedAcc;
	}
	
	static void predictedIterateSimulation(float deltaPredictionTime) {
		for (int i=0; i < listOfBodies.size(); i++) {			
			if (listOfBodies.get(i).predictedGravity) {
				OrbitalBody currentBody = listOfBodies.get(i);
				predictedSumOfAcc.set(0,0,0);
				listOfBodies.get(i).predictedMostPullingBodyAcc = 0;
				for (int j = 0; j < listOfBodies.size() ; j++){
					if (listOfBodies.get(j).predictedGravity) {
						if (j != i){
							OrbitalBody pullingBody = listOfBodies.get(j);
							predictedCalculatedAcc.set(0,0,0);
							if (perturbationCalculationMethod == 0){ // Cowell's Formulation
								predictedCalculatedAcc = predictedCowellsFormulation(currentBody, pullingBody);				
								if (predictedCalculatedAcc.len() >= listOfBodies.get(i).predictedMostPullingBodyAcc){
									listOfBodies.get(i).predictedMostPullingBodyAcc = predictedCalculatedAcc.len();
									listOfBodies.get(i).predictedMostPullingBodyName = listOfBodies.get(j).name;
								}
								predictedSumOfAcc.add(predictedCalculatedAcc);
							}
						}
					}
				}
				currentBody.setPredictedAcceleration(predictedSumOfAcc.x, predictedSumOfAcc.y, predictedSumOfAcc.z);			
				currentBody.predictedIterateVelThenPos(deltaPredictionTime);			
			}
		}	
		predictAllCollisions();
	}	
	
	static Vector3 predictedCowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		Vector3 currentPos = currentBody.predictedPosVect;
		Vector3 pullingPos = pullingBody.predictedPosVect;
		Vector3 diffOfPosVect = new Vector3();
		
		diffOfPosVect.add(pullingPos);
		diffOfPosVect.scl(-1);
		diffOfPosVect.add(currentPos);
		Vector3 calculatedAcc = new Vector3(0,0,0);	
		calculatedAcc.add(diffOfPosVect);
		
		calculatedAcc.scl((float) (-1*gravConst * pullingBody.mass / Math.pow(currentPos.dst(pullingPos), 3)));	
		return calculatedAcc;
	}
	
	static void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
	}
	
	static boolean passIndexError() {
		return indexOutOfBounds;
	}
		
    public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
		//float distance = Math.sqrt((Math.pow(body1.posVect.x - body2.posVect.x, 2))+(Math.pow(body1.posVect.y - body2.posVect.y, 2)));
        float distance = body1.posVect.dst(body2.posVect);
    	
    	if (distance*RunSimulation.zF*1.2f <= body1.radius*RunSimulation.zF + body2.radius*RunSimulation.zF) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void checkAllCollisions() {
    	for (int i = 0; i < listOfBodies.size(); i++) {
    		if (listOfBodies.get(i).gravity) {
    			for (int j = 0; j < listOfBodies.size(); j++) {
    				if(listOfBodies.get(j).gravity) {
    					if (i != j && checkCollision(listOfBodies.get(i), listOfBodies.get(j))) {
    						if (listOfBodies.get(i).mass < listOfBodies.get(j).mass) {
    							// Conservation of Momentum
    							Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
    							sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
    							listOfBodies.get(j).velVect.set(sumVel);
    							
    							listOfBodies.get(j).mass += listOfBodies.get(i).mass;                      
    							listOfBodies.get(j).radius = (int) Math.sqrt((listOfBodies.get(j).mass * 10) / Math.PI);
    							listOfBodies.get(j).spriteWidth = listOfBodies.get(j).radius * 2;
    							
    							// listOfBodies.get(j).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(i).radius, 2) * 2));
    							// listOfBodies.get(j).spriteWidth = listOfBodies.get(i).radius * 2;
    							listOfBodies.remove(i);
    							
    							if (RunSimulation.passIndex() >= listOfBodies.size()) {
    								System.out.println("Index Out Of Bounds");
    								indexOutOfBounds = true;
    							} else {
    								indexOutOfBounds = false;
    							}
    						} else if (listOfBodies.get(i).mass > listOfBodies.get(j).mass) {
    							// Conservation of Momentum
    							Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
    							sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
    							listOfBodies.get(i).velVect.set(sumVel);
    							
    							listOfBodies.get(i).mass += listOfBodies.get(j).mass;                   
    							listOfBodies.get(i).radius = (int) Math.sqrt((listOfBodies.get(i).mass * 10) / Math.PI);
    							listOfBodies.get(i).spriteWidth = listOfBodies.get(i).radius * 2;
    							
    							//listOfBodies.get(i).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(j).radius, 2) * 2));
    							// listOfBodies.get(i).spriteWidth = listOfBodies.get(j).radius * 2;
    							listOfBodies.remove(j);
    							
    							if (RunSimulation.passIndex() >= listOfBodies.size()) {
    								System.out.println("Index Out Of Bounds");
    								indexOutOfBounds = true;
    							} else {
    								indexOutOfBounds = false;
    							}
    						} else {
    							// Conservation of Momentum
    							Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
    							sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
    							
    							listOfBodies.get(i).velVect.set(sumVel);
    							
    							listOfBodies.get(i).mass += listOfBodies.get(j).mass;
    							
    							//listOfBodies.get(i).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(i).radius, 2) * 2));
    							//listOfBodies.get(i).spriteWidth = listOfBodies.get(i).radius * 2;
    							listOfBodies.remove(j);
    							
    							if (RunSimulation.passIndex() >= listOfBodies.size()) {
    								System.out.println("Index Out Of Bounds");
    								indexOutOfBounds = true;
    							} else {
    								indexOutOfBounds = false;
    							}
    						}	
    						checkAllCollisions();
    						break;
    					}
    				}
    			}
    		}
    	}
    }

    public static boolean predictCollision(OrbitalBody body1, OrbitalBody body2) {
		float distance = body1.predictedPosVect.dst(body2.predictedPosVect);
    	
    	if (distance*RunSimulation.zF*1.2f <= body1.radius*RunSimulation.zF + body2.radius*RunSimulation.zF) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void predictAllCollisions() {
    	for (int i = 0; i < listOfBodies.size(); i++) {
    		if (listOfBodies.get(i).predictedGravity && !listOfBodies.get(i).removed) {
    			for (int j = 0; j < listOfBodies.size(); j++) {
    				if(listOfBodies.get(j).predictedGravity && !listOfBodies.get(j).removed) {
    					if (i != j && predictCollision(listOfBodies.get(i), listOfBodies.get(j))) {
    						if (listOfBodies.get(i).mass < listOfBodies.get(j).mass) {
    							listOfBodies.get(i).setRemoved(true);
    							System.out.println("Body Removed: " + listOfBodies.get(i).name);
    						} else if (listOfBodies.get(i).mass > listOfBodies.get(j).mass) {
    							System.out.println("Body Removed: " + listOfBodies.get(j).name);
    							listOfBodies.get(j).setRemoved(true);
    						} else {
    							System.out.println("Body Removed: " + listOfBodies.get(j).name);
    							listOfBodies.get(j).setRemoved(true);
    						}	
    						predictAllCollisions();
    						break;
    					}
    				}
    			}
    		}
    	}
    }
}
    