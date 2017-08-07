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
	
	final static int numOfIterations = 1000000;
	
	
	
	static void iterateSimulation(float deltaTime) {

       
		for (int i = 0; i < listOfBodies.size(); i++){
			listOfBodies.get(i).integrateLeapfrogPos(deltaTime);
		}
		
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
				}
			}
			currentBody.setAcceleration(sumOfAcc.x, sumOfAcc.y, sumOfAcc.z);					
		}
		
		for (int i = 0; i < listOfBodies.size(); i++){
			listOfBodies.get(i).integrateLeapfrogVel(deltaTime);			
		}
		
		
	/*
		for (int i = 0; i < listOfBodies.size(); i++){
			listOfBodies.get(i).integrateEuler(deltaTime);
		}
	*/	
		if (RunSimulation.collisionsOn) {
		 checkAllCollisions();
		}
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

		return calculatedAcc;
	
	}
	static void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
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
        int n = RunSimulation.n;
    	for (int i = 0; i < listOfBodies.size(); i++) {
            for (int j = 0; j < listOfBodies.size(); j++) {
            	
                if (i != j && checkCollision(listOfBodies.get(i), listOfBodies.get(j))) {
                //	System.out.println("Collision!");
                	
                    if (listOfBodies.get(i).mass < listOfBodies.get(j).mass) {
                    	
                    	// Conservation of Momentum
                    	Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
                    	sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
                    	listOfBodies.get(j).velVect.set(sumVel);
                    	
                    	listOfBodies.get(j).mass += listOfBodies.get(i).mass;                      
                    	listOfBodies.get(j).radius = (int) Math.sqrt((listOfBodies.get(j).mass * 10) / Math.PI);
                    	listOfBodies.get(j).spriteWidth = (int) Math.sqrt((listOfBodies.get(j).mass * 10) / Math.PI) * 2;
                    	
                    	// listOfBodies.get(j).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(i).radius, 2) * 2));
                        // listOfBodies.get(j).spriteWidth = listOfBodies.get(i).radius * 2;

                    	
                    	String focusName = listOfBodies.get(j).name;
                    	boolean focusDestroyed = false;
                    	if (i == n){
                    		focusDestroyed = true;
                    	}
                    	
                    	
                    	listOfBodies.remove(i);
                    	
                    	
                    	if (focusDestroyed == true){
	                    	for (int k = 0; k < listOfBodies.size(); k++) {
	                    		if (focusName == listOfBodies.get(k).name){
	                    			RunSimulation.n = k;
	                    		}
	                    		
	                    	}
                    	}
                    
                    	
                    } else if (listOfBodies.get(i).mass > listOfBodies.get(j).mass) {
                    	
                    	// Conservation of Momentum
                    	Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
                    	sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
                    	listOfBodies.get(i).velVect.set(sumVel);
                        
                    	listOfBodies.get(i).mass += listOfBodies.get(j).mass;                   
                    	listOfBodies.get(i).radius = (int) Math.sqrt((listOfBodies.get(i).mass * 10) / Math.PI);
                    	listOfBodies.get(i).spriteWidth = (int) Math.sqrt((listOfBodies.get(i).mass * 10) / Math.PI) * 2;
                    	
                    	//listOfBodies.get(i).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(j).radius, 2) * 2));
                       // listOfBodies.get(i).spriteWidth = listOfBodies.get(j).radius * 2;

                    	
                    	String focusName = listOfBodies.get(i).name;
                       	boolean focusDestroyed = false;
                    	if (j == n){
                    		focusDestroyed = true;
                    	}
                    	
                    	listOfBodies.remove(j);
                    	
                    	if (focusDestroyed == true){
	                    	for (int k = 0; k < listOfBodies.size(); k++) {
	                    		if (focusName == listOfBodies.get(k).name){
	                    			RunSimulation.n = k;
	                    			
	                    		}
	                    		
	                    	}
                    	}
                    	
                    } else {
                    	
                    	// Conservation of Momentum
                    	Vector3 sumVel = listOfBodies.get(i).velVect.scl(listOfBodies.get(i).mass).add(listOfBodies.get(j).velVect.scl(listOfBodies.get(j).mass));
                    	sumVel.scl(1/(listOfBodies.get(i).mass + listOfBodies.get(j).mass));
                   
                    	listOfBodies.get(i).velVect.set(sumVel);
                    	
                        listOfBodies.get(i).mass += listOfBodies.get(j).mass;

                        //listOfBodies.get(i).radius += Math.round(Math.sqrt(Math.pow(listOfBodies.get(i).radius, 2) * 2));
                        //listOfBodies.get(i).spriteWidth = listOfBodies.get(i).radius * 2;
                        if (n >= listOfBodies.size()) {
                            n -= n;
                        }
                        if (listOfBodies.get(n) == listOfBodies.get(j)){
                    		n = i;
                    	}
                    	String focusName = listOfBodies.get(i).name;
                       	boolean focusDestroyed = false;
                    	if (j == n){
                    		focusDestroyed = true;
                    	}
                    	
                    	listOfBodies.remove(j);
                    	
                    	if (focusDestroyed == true){
	                    	for (int k = 0; k < listOfBodies.size(); k++) {
	                    		if (focusName == listOfBodies.get(k).name){
	                    			RunSimulation.n = k;
	                    		}
	                    		
	                    	}
                    	}
                    }
                    
                    checkAllCollisions();
                    break;
                    
                }
            }

        }
        
        
    }
}