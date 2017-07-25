import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFrame;

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
	static int gravConst = 10;
	
    private final int DELAY = 30;
    private final int INITIAL_DELAY = 150;    
    private Timer timer;
	
	public static void main(String [] args)
	{
		JFrame frame = new JFrame("Orbit Simulation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Test p = new Test();
		frame.add(p);
		frame.setSize(400, 400);
		frame.setVisible(true);
		
		OrbitalBody planet = new OrbitalBody();
		listOfBodies.add(planet);
		
		
		planet.setName("PLANET");
		planet.setMass(100);
		planet.setRadius(5);
		planet.setPosition(100,100);
		planet.setVelocity(-10, 10);

		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		
		sun.setName("SUN");
		sun.setMass(10000);
		sun.setRadius(10);
		sun.setPosition(0,0);
		
		for (int x=0; x< 1500; x++){
            if (!checkCollision(planet, sun)) {
                float deltaTime = (float) 0.01;
                iterateSimulation(deltaTime);
                System.out.println("Name: " + planet.name + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("X Position: " + planet.xPosition);
                System.out.println("Y Position: " + planet.yPosition);
                System.out.println("X Acceleration: " + planet.xAcceleration);
                System.out.println("Y Acceleration: " + planet.yAcceleration);
                System.out.println("Distance to Sun: " + distBetweenTwoBodies(planet.xPosition, planet.yPosition, sun.xPosition, sun.yPosition));
            }
			
		}
	}

	private static void iterateSimulation(float deltaTime) {
		
		
		for (int i=0; i < listOfBodies.size(); i++){
			
			/*1. Iterate net forces & acceleration for each body*/
			
			float sumOfXAcc = 0;
			float sumOfYAcc = 0;
			
			for (int j=0; j < listOfBodies.size();j++){
				if (j != i){
					sumOfXAcc = (float) (gravConst * listOfBodies.get(j).mass * (listOfBodies.get(j).xPosition - listOfBodies.get(i).xPosition) / Math.pow(distBetweenOneDimension(listOfBodies.get(i).xPosition, listOfBodies.get(j).xPosition),3));
					sumOfYAcc = (float) (gravConst * listOfBodies.get(j).mass * (listOfBodies.get(j).yPosition - listOfBodies.get(i).yPosition) / Math.pow(distBetweenOneDimension(listOfBodies.get(i).yPosition, listOfBodies.get(j).yPosition),3));
					//sumOfXAcc *= -1;
					//sumOfYAcc *= -1;
					//System.out.println( listOfBodies.get(i).name);
					//System.out.println("SumOfXAcc " + sumOfXAcc);
					//System.out.println("SumOfYAcc " + sumOfYAcc);
				}
			}
			
			
			listOfBodies.get(i).setAcceleration(sumOfXAcc, sumOfYAcc);
			
			
			/*2. Iterate velocities*/
			listOfBodies.get(i).iterateVelocity(deltaTime);

			/*3. Calculate new positions*/
			listOfBodies.get(i).iteratePosition(deltaTime);
		}
		
	
		
	}
	
	public static float distBetweenOneDimension(float bodyOnePos, float bodyTwoPos){
		float distance = (float) Math.sqrt(Math.pow(bodyOnePos, 2) + Math.pow(bodyTwoPos, 2));
		return distance;
	}
	
	public static float distBetweenTwoBodies(float bodyOneX, float bodyOneY, float bodyTwoX, float bodyTwoY){
		float distance = (float) Math.sqrt((bodyOneX - bodyTwoX)*(bodyOneX - bodyTwoX) + (bodyOneY - bodyTwoY)*(bodyOneY - bodyTwoY));
		return distance;
	}

	public static boolean checkCollision(OrbitalBody body1, OrbitalBody body2) {
        if (distBetweenTwoBodies(body1.xPosition, body2.yPosition, body2.xPosition, body2.yPosition) <= body1.radius + body2.radius) {
            return true;
        } else {
            return false;
        }
    }
	
	
}
