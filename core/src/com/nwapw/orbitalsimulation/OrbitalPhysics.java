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
	static Vector3 calculatedAcc = new Vector3();
	
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	final static float deltaTime = (float) 0.01;
	final static int numOfIterations = 1000000;
	
	
	
	static void iterateSimulation(float deltaTime) {

       

		// 1. Calculate net force and acceleration from acting on each body.
		

		for (int i=0; i < listOfBodies.size(); i++){
			
			OrbitalBody currentBody = listOfBodies.get(i);
			sumOfAcc.set(0,0,0);
			
			listOfBodies.get(i).mostPullingBodyAcc = 0;
			
			for (int j = 0; j < listOfBodies.size() ; j++){
				
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
			
			// 2. Iterate and integrate for velocity and then position.

			currentBody.setAcceleration(sumOfAcc.x, sumOfAcc.y, sumOfAcc.z);			
			currentBody.iterateVelThenPos(deltaTime);
			
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
		
		calculatedAcc.scl((float) (-1*gravConst * pullingBody.mass / (Math.pow(currentPos.dst(pullingPos), 3) * currentBody.mass)));	
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
	static void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
	}
	
    public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
		//float distance = Math.sqrt((Math.pow(body1.posVect.x - body2.posVect.x, 2))+(Math.pow(body1.posVect.y - body2.posVect.y, 2)));
        float distance = body1.posVect.dst(body2.posVect);
    	
    	if (distance*RunSimulation.zF <= body1.radius*RunSimulation.zF + body2.radius*RunSimulation.zF) {
            return true;
        } else {
            return false;
        }
    }

    public static void checkAllCollisions() {
        
    	for (int i = 0; i < listOfBodies.size(); i++) {
            for (int j = 0; j < listOfBodies.size(); j++) {
            	
                if (i != j && checkCollision(listOfBodies.get(i), listOfBodies.get(j))) {
                    if (listOfBodies.get(i).mass < listOfBodies.get(j).mass) {
                        listOfBodies.remove(i);
                    } else if (listOfBodies.get(i).mass > listOfBodies.get(j).mass) {
                        listOfBodies.remove(j);
                    } else {
                        listOfBodies.get(i).mass += listOfBodies.get(j).mass;
                        listOfBodies.get(i).velVect.add(listOfBodies.get(j).velVect);
                        listOfBodies.get(i).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(i).radius, 2) * 2));
                        listOfBodies.get(i).spriteWidth = listOfBodies.get(i).radius * 2;
                    }
                    
                    checkAllCollisions();
                    break;
                    
                }
            }

        }
        
        
    }
}