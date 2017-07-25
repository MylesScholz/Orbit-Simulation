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
	static int gravConst = 1;
	
    private final int DELAY = 30;
    private final int INITIAL_DELAY = 150;    
    private Timer timer;
	
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
		
		
		planet.setName("PLANET~~~~~~~~~~~~~~~~~~~~~~~");
		planet.setMass(1);
		planet.setPosition(100,100);
		planet.setVelocity((float) -10,0);
		
		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		
		sun.setName("SUN===========================");
		sun.setMass(1000);
		sun.setPosition(0,0);
		
		float timeCounter = 0;
		
		for (int x=0; x< 10000000; x++){
			float deltaTime = (float) 0.00001;
			timeCounter += deltaTime;
			iterateSimulation(deltaTime);
			
			if (x % 10000 == 0){
				System.out.println("Time: " + timeCounter);
				System.out.println(planet.xPosition);
				
			/*
			System.out.println("PLANET~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("X Position: " + planet.xPosition);
			System.out.println("Y Position: " + planet.yPosition);
			System.out.println("X Velocity: " + planet.xVelocity);
			System.out.println("Y Velocity: " + planet.yVelocity);
			System.out.println("X Acceleration: " + planet.xAcceleration);
			System.out.println("Y Acceleration: " + planet.yAcceleration);
			*/
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
					OrbitalBody currentBody = listOfBodies.get(i);
					OrbitalBody pullingBody = listOfBodies.get(j);
					
					//sumOfXAcc = (float) ((gravConst * pullingBody.mass * (pullingBody.xPosition - currentBody.xPosition) / Math.pow(distBetweenOneDimension(currentBody.xPosition, pullingBody.xPosition),3)));
					
					sumOfXAcc = (float) (-1.0 * gravConst * pullingBody.mass);
					sumOfXAcc /= Math.pow((pullingBody.xPosition - currentBody.xPosition), 2);
					
					//sumOfYAcc = (float) ((gravConst * pullingBody.mass * (pullingBody.yPosition - currentBody.yPosition) / Math.pow(distBetweenOneDimension(currentBody.yPosition, pullingBody.yPosition),3)));
					sumOfYAcc = (float) (-1.0 * gravConst * pullingBody.mass);
					sumOfYAcc /= Math.pow((pullingBody.yPosition - currentBody.yPosition), 2);
					
					
					
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
	
	
}
