package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class RunSimulation extends ApplicationAdapter {
		
	// Constant for the force of gravity, affects how much bodies accelerate
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
	
	// Cycle through focus
	int n = 0;
	// Prevents overpressing keys
	int nDuration = 0;
	
	int placedPlanetCounter = 0;
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	
	boolean newPlanet = false;
	int clickPositionX;
	int clickPositionY;
	int unclickPositionX;
	int unclickPositionY;
	
	OrbitalBody planet = new OrbitalBody();	
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	private OrthographicCamera cam;
	
	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
	
	
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();
	
	BitmapFont font;
	
	
	@Override
	public void create () {
		
		font = new BitmapFont();
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
		
		
		LibGDXTools.bodyInitialize("#1", 1, 5, 70, 70, 30, 0, 10);
		LibGDXTools.bodyInitialize("#2", 1, 5, 90, 90, 50, 0, 10);	
		LibGDXTools.bodyInitialize("#3", 1, 5, 110, 110, 50, 0, 10);	
		LibGDXTools.bodyInitialize("#4", 1, 5, 130, 130, 60, 0, 10);	
		LibGDXTools.bodyInitialize("#5", 1, 5, 150, 150, 70, 0, 10);	
		LibGDXTools.bodyInitialize("Star", 10000, 20, 0, 0, 0, 0, 50);
		

		batch = new SpriteBatch();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		
		//cam = new OrthographicCamera(30, 30 * (h / w));

		
	}
	
	public void place () {		
		if (Gdx.input.isButtonPressed(0) && newPlanet == false) {
			clickPositionX = Gdx.input.getX();
			clickPositionY = Gdx.input.getY();
			newPlanet = true;
		}
		else if (!Gdx.input.isButtonPressed(0) && newPlanet == true) {
			
			unclickPositionX = Gdx.input.getX();
			unclickPositionY = Gdx.input.getY();
			
			int randomMass = 1 + (int)(Math.random() * 50);
			int randomRadius = randomMass * 5;
			
			String planetName = "New Planet " + placedPlanetCounter;	
			placedPlanetCounter++;
			
			LibGDXTools.bodyInitialize(planetName, randomMass, randomRadius, clickPositionX - 300, (clickPositionY - 250) * -1, unclickPositionX - clickPositionX, (unclickPositionY - clickPositionY) * -1);
			
			//OrbitalBody.evenBodyBug();
			
			newPlanet = false;
		}
	}
	
	@Override
	public void render () {
	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		int listLength = listOfBodies.size();

		if(Gdx.input.isKeyPressed(Input.Keys.N)){
			n++;
			n = n % listLength;
			nDuration++;
			if (nDuration > 1 && n != 0){
				n--;
			}
			
			System.out.println("n: " + n);
	    }
		else {
			nDuration = 0;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.S)){
			listOfBodies.get(n).posVect.set(100, 100, 100);
			listOfBodies.get(n).velVect.set(0, 0, 0);
	    }
		
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
        	listOfBodies.get(n).velVect.y += 5;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        	listOfBodies.get(n).velVect.y -= 5;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        	listOfBodies.get(n).velVect.x -= 5;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	listOfBodies.get(n).velVect.x += 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
        	listOfBodies.get(n).velVect.set(0,0,0);
        }
		
		batch.begin();
		
		font.draw(batch, "Orbital Simulation", 10, 20);
		
		font.draw(batch, "(n) focus  (s) set pos/vel to [100, 100]  (arrow keys) vel  (0) vel = 0", 155, 40);

		
		
		String printNumOfBodies = "Number of bodies: " + String.valueOf(listOfBodies.size());
		String printDeltaTime = "dt: " + String.valueOf(deltaTime);
		String printIterationStep = "step: " + String.valueOf(iterationCounter);
		String printFocusPlanet = "focus: " + listOfBodies.get(n).name;
		font.draw(batch, printNumOfBodies, 155, 20);
		font.draw(batch, printDeltaTime, 300, 20);
		font.draw(batch, printIterationStep, 360, 20);
		font.draw(batch, printFocusPlanet, 440, 20);
		for (int i = 0; i < listOfBodies.size(); i++) {

			OrbitalBody renderBody = listOfBodies.get(i);

			float spriteX = (float) renderBody.posVect.getX() + 300;
			float spriteY = (float) renderBody.posVect.getY() + 250;
			
			font.draw(batch, renderBody.name, spriteX + 10, spriteY);
			
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
			place();
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