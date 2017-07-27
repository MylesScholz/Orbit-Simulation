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
	
	
	boolean newPlanet = false;
	int placedPositionX;
	int placedPositionY;
	
	OrbitalBody placedPlanet = new OrbitalBody();
	OrbitalBody planet = new OrbitalBody();
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	Texture img1;
	Texture img2;
	
<<<<<<< HEAD
	static boolean leftClickDown;
	static boolean leftClickUp;
	
	@Override
	public void create () {	
		/*
		listOfBodies.add(planet);
		planet.setName("Planet #1");
		planet.setMass(1);
		planet.setPosition(100, 100, 100);
		planet.setVelocity(200, 0, 0);
		*/
		listOfBodies.add(sun);
		sun.setName("Sun");
		sun.setMass(100000);
		sun.setPosition(0, 0, 0);
		sun.setVelocity(0, 0, 0);
=======
	private OrthographicCamera cam;
	
	
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
		
		
>>>>>>> 4bc68aba807b606322ea52a29af5653dd24b0539
		
		batch = new SpriteBatch();
		img1 = new Texture("planets/planet18.png");
		img2 = new Texture("stars/mainsequence/star_orange01.png");
		

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		
		//cam = new OrthographicCamera(30, 30 * (h / w));
	}

	public void place () {
		
		placedPositionX = Gdx.input.getX();
		placedPositionY = Gdx.input.getY();
		
		if (Gdx.input.isButtonPressed(0) && newPlanet == false) {
			System.out.println("Debug1");
			listOfBodies.add(placedPlanet);
			planet.setName("Placed Planet");
			planet.setMass(1);			
			planet.setPosition(placedPositionX, placedPositionY, 0);
			newPlanet = true;
		}
		else if (!Gdx.input.isButtonPressed(0) && newPlanet == true) {
			System.out.println("Debug2");
			planet.setVelocity(Gdx.input.getX() - placedPositionX + 200, Gdx.input.getY() - placedPositionY + 00, 0);
			newPlanet = false;
		}
	}
	
	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
<<<<<<< HEAD
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float planetX = (float) planet.posVect.getX() + 200;
		float planetY = (float) planet.posVect.getY() + 200;

		float sunX = (float) sun.posVect.getX() + 200;
		float sunY = (float) sun.posVect.getY() + 200;
=======
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
>>>>>>> 4bc68aba807b606322ea52a29af5653dd24b0539
		
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
		
		if (iterationCounter <= numOfIterations){
			timeCounter += deltaTime;
			OrbitalPhysics.iterateSimulation(deltaTime);	
			place();
			// DEBUG
<<<<<<< HEAD
			if ((iterationCounter % 25) == 0){
	
				System.out.println(iterationCounter);
				System.out.println("ppx: " + planet.posVect.getX());
				System.out.println("spx: " + sun.posVect.getY());
				System.out.println("dis: " + Vector3.distBetween(planet.posVect, sun.posVect));
				System.out.println("");
				
				//System.out.println(planet.posVect.getX());
=======
			if (iterationCounter % numOfIterations/100 == 0){

>>>>>>> 4bc68aba807b606322ea52a29af5653dd24b0539
				/*
				System.out.println(planet.name);
				System.out.println("t: " + timeCounter);
				System.out.println("p: " + planet.posVect.getX());
				System.out.println("v: " + planet.velVect.getX());
				System.out.println("a: " + planet.accVect.getX());
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