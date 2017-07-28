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

public class OrbitalPhysics {
	
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	final static float deltaTime = (float) 0.01;
	final static int numOfIterations = 1000000;
	
	static void iterateSimulation(float deltaTime) {

        checkAllCollisions();

		// 1. Calculate net force and acceleration from acting on each body.
		

		for (int i=0; i < listOfBodies.size(); i++){
			
			OrbitalBody currentBody = listOfBodies.get(i);
			Vector3 sumOfAcc = new Vector3();

			for (int j = 0; j < listOfBodies.size() ; j++){
				
				if (j != i){
					
					OrbitalBody pullingBody = listOfBodies.get(j);

					if (perturbationCalculationMethod == 0){ // Cowell's Formulation
						Vector3 calculatedAcc = cowellsFormulation(currentBody, pullingBody);
						
						
						sumOfAcc.add(calculatedAcc);
						//System.out.println("sum of acc: " + sumOfAcc.print());
						//System.out.println("");
					}
					/*
					else if {
						// TODO: Encke's Method, Variation of Parameters, etc.
					}
					*/
				}
			}
			
			// 2. Iterate and integrate for velocity and then position.

			currentBody.setAcceleration(sumOfAcc.getX(), sumOfAcc.getY(), sumOfAcc.getZ());			
			currentBody.iterateVelThenPos(deltaTime);
			
			//System.out.println(currentBody.posVect.print());
			//System.out.println(currentBody.velVect.print());
			//System.out.println(currentBody.accVect.print());
			//System.out.println("");
		}	
	}	
	
	static Vector3 cowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		

		Vector3 currentPos = currentBody.posVect;
		Vector3 pullingPos = pullingBody.posVect;

		Vector3 diffOfPosVect = new Vector3();
		diffOfPosVect.add(pullingPos);	
		
		if (!(RunSimulation.listOfBodies.size() % 2 == 0)) {
			currentPos.scale(-1);
		}
		
		diffOfPosVect.add(currentPos);
		
		Vector3 calculatedAcc = new Vector3();	
		calculatedAcc.add(diffOfPosVect);

		calculatedAcc.scale(-1*gravConst * pullingBody.mass / Math.pow(diffOfPosVect.length(), 3));	
		
		double[] nanCheckList = calculatedAcc.get();

		for (int n = 0; n < 3; n++){
			Double testNum = (double) nanCheckList[n];
			Boolean testBool = testNum.isNaN();
			if (testBool == true) {
				if (n == 0){
					calculatedAcc.x = 0;
				}
				if (n == 1){
					calculatedAcc.y = 0;
				}
				if (n == 2){
					calculatedAcc.z = 0;
				}
			}	
			
		}

		//System.out.println("COW>CalcAcc: " + calculatedAcc.print());
		return calculatedAcc;
	
	}
	static void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
	}
	
    public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
		Vector3 diffOfPosVect = new Vector3();
        diffOfPosVect.add(body1.posVect);
        diffOfPosVect.add(body2.posVect);
        if (diffOfPosVect.length() <= body1.radius + body2.radius) {
            return true;
        } else {
            return false;
        }
    }

    public static void checkAllCollisions() {
        
    	for (int i = 0; i < listOfBodies.size(); i++) {
            for (int j = 0; j < listOfBodies.size(); j++) {
                if (i != j && checkCollision(listOfBodies.get(i), listOfBodies.get(j))) {
                    if (listOfBodies.get(i).mass <= listOfBodies.get(j).mass) {
                      //  listOfBodies.remove(i);
                    } else {
                      //  listOfBodies.remove(j);
                    }
                }
            }

        }
        
        
    }
}