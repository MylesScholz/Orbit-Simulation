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
	final static int numOfIterations = 10000000;
	
	// To debug
	double timeCounter = 0;
	int iterationCounter = 0;
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	
<<<<<<< HEAD
	//boolean newPlanet = false;
	//int placedPositionX;
	//int placedPositionY;
=======
>>>>>>> d89e8f10ab2e72841971bb786400c3ffa30be02b
	
	OrbitalBody planet = new OrbitalBody();	
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	Texture img1;
	Texture img2;
	
	private OrthographicCamera cam;
	
	
<<<<<<< HEAD
		listOfBodies.add(planet);
		planet.setName("Planet #1");
		planet.setMass(1000);
		planet.setPosition(100, 100, 100);
		planet.setVelocity(10, -10, 0);

		listOfBodies.add(sun);
		sun.setName("Sun");
		sun.setMass(1000);
		sun.setPosition(0, 0, 0);
		sun.setVelocity(-10, 10, 0);
=======
	Texture textures;
	
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
	
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
		
		
		LibGDXTools.bodyInitialize("Planet", 1, 100, 100, 200, 0, 20);
		LibGDXTools.bodyInitialize("Sun", 100000, 0, 0, 0, 0, 100);
		//LibGDXTools.bodyInitialize("Planet 2", 1, -100, -100, -200, 0);
		
		
>>>>>>> d89e8f10ab2e72841971bb786400c3ffa30be02b
		
		batch = new SpriteBatch();
		img1 = new Texture("planets/planet18.png");
		img2 = new Texture("stars/mainsequence/star_orange01.png");
		

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		
		//cam = new OrthographicCamera(30, 30 * (h / w));

		
	}

	@Override
	public void render () {
	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		for (int i = 0; i < listOfBodies.size(); i++) {
			
			OrbitalBody renderBody = listOfBodies.get(i);
			
			float spriteX = (float) renderBody.posVect.getX()*2 + 200;
			float spriteY = (float) renderBody.posVect.getY()*2 + 200;
			
			float spriteWidth = renderBody.spriteWidth;
			//System.out.println(renderBody.name + " " + spriteWidth);
			Texture spriteTexture = renderBody.texture;
			
			batch.draw(spriteTexture, spriteX, spriteY, spriteWidth, spriteWidth);
			
		}
				
		//cam.update();
		//batch.setProjectionMatrix(cam.combined);
		
		//batch.draw(img1, planetX, planetY, 10, 10);
		//batch.draw(img2, sunX, sunY, 50, 50);
		
		batch.end();	

		OrbitalPhysics.passList(listOfBodies);
		
		if (iterationCounter <= 10000){
			timeCounter += deltaTime;
			OrbitalPhysics.iterateSimulation(deltaTime);	
			
			// DEBUG
<<<<<<< HEAD
			if ((iterationCounter % 1) == 0){
				
				System.out.println(iterationCounter);
				System.out.println("Planet Position X: " + planet.posVect.getX());
				System.out.println("Sun Position Y: " + sun.posVect.getY());
				System.out.println("Distance: " + Vector3.distBetween(planet.posVect, sun.posVect));
				System.out.println("");
						
				//System.out.println(planet.posVect.getX());
=======
			if (iterationCounter % numOfIterations/100 == 0){

>>>>>>> d89e8f10ab2e72841971bb786400c3ffa30be02b
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
		img1.dispose();
		img2.dispose();
	}
}