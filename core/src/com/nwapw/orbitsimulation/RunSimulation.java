package com.nwapw.orbitsimulation;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RunSimulation extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	SpriteBatch batch2;
	Texture img2;
	
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	final static float deltaTime = (float) 0.01;
	final static int numOfIterations = 1000000;
	
	int iterationCounter = 0;
	double timeCounter = 0;
	
	OrbitalBody planet = new OrbitalBody();	
	OrbitalBody sun = new OrbitalBody();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("earth.png");
		
		batch2 = new SpriteBatch();
		img2 = new Texture("sun.jpg");
		
		listOfBodies.add(planet);
		planet.setName("Planet #1");
		planet.setMass(1);
		planet.setPosition(100, 100, 100);
		planet.setVelocity(200, 0, 0);

		
		
		listOfBodies.add(sun);
		sun.setName("Sun");
		sun.setMass(10000);
		sun.setPosition(0, 0, 0);
		sun.setVelocity(0, 0, 0);
		

		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		float planetX = (float) planet.posVect.getX();
		float planetY = (float) planet.posVect.getY();
		
		batch.begin();
		batch2.begin();
		
		batch.draw(img, planetX, planetY, 60, 50);
		batch.end();	
		
		batch2.draw(img2, 0, 0, 100, 100);
		batch2.end();
		
		OrbitalPhysics.passList(listOfBodies);
		
		if (iterationCounter <= 10000){
			timeCounter += deltaTime;
			OrbitalPhysics.iterateSimulation(deltaTime);	
			
			// DEBUG
			if (iterationCounter % numOfIterations/100 == 0){
				
				//System.out.println(planet.posVect.getX());
				
				System.out.println(planet.name);
				System.out.println("t: " + timeCounter);
				System.out.println("p: " + planet.posVect.getX());
				System.out.println("v: " + planet.velVect.getX());
				System.out.println("a: " + planet.accVect.getX());
				System.out.println("");
				
			}				
		}	
		
		iterationCounter += 1;
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		batch2.dispose();
		img2.dispose();
	}
}
