import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

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
	
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList();
  
	final static int gravConst = 1;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	
	public static void main(String [] args)
	{
	
		/*
		JFrame frame = new JFrame("Title");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Test p = new Test();
		frame.add(p);
		frame.setSize(400, 400);
		frame.setVisible(true);
		*/
		
		OrbitalBody planet = new OrbitalBody();	
		listOfBodies.add(planet);
		planet.setName("Planet #1");
		planet.setMass(1);
		planet.setPosition(50, 100, 75);
		planet.setVelocity(-10, 0, 0);
		
		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		// Position and velocity vectors {0,0,0} by default
		sun.setName("Sun");
		sun.setMass(1000);
		
		
		float timeCounter = 0;
		
		for (int x=0; x< 100000; x++){
			
			// DEBUG
			if (x % 1000 == 0){
				System.out.println("t: " + timeCounter);
				System.out.println("p: " + planet.posVect);
				System.out.println("v: " + planet.velVect);
				System.out.println("a: " + planet.accVect);
				System.out.println("");

			}	
			
			float deltaTime = (float) 0.001;
			timeCounter += deltaTime;
			iterateSimulation(deltaTime);
		}
	}

	
	static void iterateSimulation(float deltaTime) {
		
		// 1. Calculate net force and acceleration from acting on each body.
		
		
		
		for (int i=0; i < listOfBodies.size(); i++){
			
			OrbitalBody currentBody = listOfBodies.get(i);
			Vector3d sumOfAcc = new Vector3d();
			
			for (int j = 0; j < listOfBodies.size() ; j++){
				
				if (j != i){
					OrbitalBody pullingBody = listOfBodies.get(j);
									
					if (perturbationCalculationMethod == 0){ // Cowell's Formulation
						Vector3d calculatedAcc = cowellsFormulation(currentBody, pullingBody);
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
			listOfBodies.get(i).setAcceleration(sumOfAcc.getX(), sumOfAcc.getY(), sumOfAcc.getZ());			
			currentBody.iterateVelThenPos(deltaTime);
		}	
	}	
	
	static Vector3d cowellsFormulation(OrbitalBody currentBody, OrbitalBody pullingBody) {
		
		Vector3d currentPos = currentBody.posVect;
		Vector3d pullingPos = pullingBody.posVect;
		
		Vector3d diffOfPosVect = new Vector3d();
		diffOfPosVect.add(pullingPos);	
		currentPos.scale(-1);
		diffOfPosVect.add(currentPos);
		
		Vector3d calculatedAcc = new Vector3d();
		
		calculatedAcc.add(diffOfPosVect);
		calculatedAcc.scale(gravConst * pullingBody.mass / Math.pow(diffOfPosVect.length(), 3));
		
		return calculatedAcc;
	
	}
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
}
