package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class RunSimulation extends ApplicationAdapter {
		
	// The force of gravity, affects how bodies accelerate
	final static int gravConst = 100; 

	// TODO Switches methods of calculating perturbations
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	//
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	
	// Specifies time used to calculate numerical integration
	// TODO Adaptive step-size control
	final static float deltaTime = (float) 0.01;
	
	// The max number of iterations that the simulation runs
	final static int numOfIterations = 100000000;
	
	// To debug
	double timeCounter = 0;
	int iterationCounter = 0;
	int dataDivision = 1000;
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	
	//boolean newPlanet = false;
	//int placedPositionX;
	//int placedPositionY;
	
	OrbitalBody planet = new OrbitalBody();	
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	private OrthographicCamera cam;
	
	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
	
	
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();
	
	@Override
	public void create () {
		
		for (int i = 1; i < 8; i++){
			String planetFileName = "planets/planet" + i + ".png";
			textures = new Texture(planetFileName);
			availablePlanetTextures.add(textures);
		}
		
		for (int i = 10; i < 21; i++){
			String planetFileName = "planets/planet" + i + ".png";
			textures = new Texture(planetFileName);
			availablePlanetTextures.add(textures);
		}	

		// INITIALIZE IN ORDER OF MASS SMALLEST TO LARGEST
		// Name, Mass, posx, posy, velx, vely, spritewidth
		
		LibGDXTools.bodyInitialize("Pl2", 10, 70, 70, 30, 0, 10);
		LibGDXTools.bodyInitialize("Pl1", 10, 90, 90, 40, 0, 10);	
		LibGDXTools.bodyInitialize("Pl1", 10, 110, 110, 50, 0, 10);	
		LibGDXTools.bodyInitialize("Pl1", 10, 130, 130, 60, 0, 10);	
		LibGDXTools.bodyInitialize("Pl1", 10, 150, 150, 70, 0, 10);	
		LibGDXTools.bodyInitialize("Sun", 10000, 0, 0, 0, 0, 50);
		

		batch = new SpriteBatch();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		
		//cam = new OrthographicCamera(30, 30 * (h / w));

		
	}

	@Override
	public void render () {
	
		//Gdx.gl.glClearColor(0, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		for (int i = 0; i < listOfBodies.size(); i++) {

			OrbitalBody renderBody = listOfBodies.get(i);

			float spriteX = (float) renderBody.posVect.getX() + 200;
			float spriteY = (float) renderBody.posVect.getY() + 200;
					
			float spriteWidth = renderBody.spriteWidth;

			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, spriteX, spriteY, spriteWidth, spriteWidth);
			
		}
		//cam.update();
		//batch.setProjectionMatrix(cam.combined);
		
		batch.end();	

		OrbitalPhysics.passList(listOfBodies);
		
		if (iterationCounter <= numOfIterations){
			timeCounter += deltaTime;
			OrbitalPhysics.iterateSimulation(deltaTime);	
			
			// DEBUG
			if ((iterationCounter % dataDivision) == 0){
						
				//System.out.println(planet.posVect.getX());
				/*
				System.out.println(planet.name);
				System.out.println(iterationCounter);
				System.out.println("px: " + planet.posVect.getX());
				System.out.println("py: " + planet.posVect.getY());
				System.out.println("vx: " + planet.velVect.getX());
				System.out.println("vy: " + planet.velVect.getY());
				System.out.println("ax: " + planet.accVect.getX());
				System.out.println("ay: " + planet.accVect.getY());
				System.out.println("");
				*/
			}				
		}	
		
		iterationCounter += 1;
		
	}

	@Override
	public void dispose () {
		batch.dispose();
		
		for (int i = 0; i < runningTextures.size(); i++){
			runningTextures.get(i).dispose();
		}
		
	}
}