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

public class OrbitalPhysics{
	static ArrayList<OrbitalBody> listOfBodies = new ArrayList();
	static int gravConst = 1;
	
    private final int DELAY = 30;
    private final int INITIAL_DELAY = 150;    
    private Timer timer;
	
	public static void main(String [] args)
	{	
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
 		JFrame frame = new JFrame("Oribital Simulation");
		frame.setVisible(true);
		frame.setSize(1000, 1000);
 		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
 		Test p = new Test();
		frame.add(p);
		frame.setVisible(true);
		
		OrbitalBody planet = new OrbitalBody();
		listOfBodies.add(planet);
		
		planet.setName("PLANET~~~~~~~~~~~~~~~~~~~~~~~");
		planet.setMass(1);
		planet.setPosition(5,5);
		planet.setVelocity(-5,5);
		
		OrbitalBody sun = new OrbitalBody();
		listOfBodies.add(sun);
		
		sun.setName("SUN===========================");
		sun.setMass(10000);
		sun.setPosition(0,0);
		
		p.passList(listOfBodies);
		
		Thread iterate = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int x = 0; x < 10000; x ++){
					float deltaTime = (float) 0.1;
					iterateSimulation(deltaTime);
					frame.repaint();
					//p.paintImmediately(500,500,900,900);
					System.out.println(x);
					System.out.println("PLANET~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.out.println("X Position: " + planet.xPosition);
					System.out.println("Y Position: " + planet.yPosition);
					System.out.println("X Position Old: " + planet.getXOldPosition());
					System.out.println("Y Position Old: " + planet.getYOldPosition());
					System.out.println("X Velocity: " + planet.xVelocity);
					System.out.println("Y Velocity: " + planet.yVelocity);
					System.out.println("X Acceleration: " + planet.xAcceleration);
					System.out.println("Y Acceleration: " + planet.yAcceleration);
				}
			}
		});
		iterate.start();
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
	}
