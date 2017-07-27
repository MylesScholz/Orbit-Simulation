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
		}	
	}	
	
	static Vector3 cowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		

		Vector3 currentPos = currentBody.posVect;
		Vector3 pullingPos = pullingBody.posVect;
		
		Vector3 diffOfPosVect = new Vector3();
		diffOfPosVect.add(pullingPos);	
		currentPos.scale(-1);
		diffOfPosVect.add(currentPos);
		
		Vector3 calculatedAcc = new Vector3();	
		calculatedAcc.add(diffOfPosVect);

		calculatedAcc.scale(-1*gravConst * pullingBody.mass / Math.pow(diffOfPosVect.length(), 3));	
		/*
		if (currentBody.name == "Planet #1"){
			System.out.println(calculatedAcc);
		}
		*/
		return calculatedAcc;
	
	}
	static void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
	}
	
	
	/*
    public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
		Vector3d diffOfPosVect = new Vector3d();
		diffOfPosVect = body1.posVect;
        diffOfPosVect.sub(body2.posVect);
        if (diffOfPosVect.length() <= body1.radius + body2.radius) {
            return true;
        } else {
            return false;
        }
    }
    */
}