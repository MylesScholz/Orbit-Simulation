package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
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
	
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	final static int gravConst = 100;
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	private OrthographicCamera cam;
	
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	
	final static float deltaTime = (float) 0.01;
	final static int numOfIterations = 1000000;
	
	int iterationCounter = 0;
	double timeCounter = 0;
	
	boolean newPlanet = false;
	int placedPositionX;
	int placedPositionY;
	
	OrbitalBody placedPlanet = new OrbitalBody();
	OrbitalBody planet = new OrbitalBody();
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	Texture img1;
	Texture img2;
	
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
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float planetX = (float) planet.posVect.getX() + 200;
		float planetY = (float) planet.posVect.getY() + 200;

		float sunX = (float) sun.posVect.getX() + 200;
		float sunY = (float) sun.posVect.getY() + 200;
		
		//cam.update();
		//batch.setProjectionMatrix(cam.combined);
		
		
		batch.begin();
		
		batch.draw(img1, planetX, planetY, 10, 10);
		batch.draw(img2, sunX, sunY, 50, 50);
		
		batch.end();	

		OrbitalPhysics.passList(listOfBodies);
		
		if (iterationCounter <= numOfIterations){
			timeCounter += deltaTime;
			OrbitalPhysics.iterateSimulation(deltaTime);	
			place();
			// DEBUG
			if ((iterationCounter % 25) == 0){
	
				System.out.println(iterationCounter);
				System.out.println("ppx: " + planet.posVect.getX());
				System.out.println("spx: " + sun.posVect.getY());
				System.out.println("dis: " + Vector3.distBetween(planet.posVect, sun.posVect));
				System.out.println("");
				
				//System.out.println(planet.posVect.getX());
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