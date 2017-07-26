package com.nwapw.orbitsimulation;

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
	SpriteBatch batch;
	Texture img;
	SpriteBatch batch2;
	Texture img2;
	
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
		

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		cam = new OrthographicCamera(30, 30 * (h / w));

		cam.position.set(1000, 1000, 1000);
		cam.update();

		batch = new SpriteBatch();
		
	}

	@Override
	public void render () {
		
		handleInput();
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		
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
	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			cam.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			cam.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.translate(-3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cam.translate(0, -3, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cam.translate(0, 3, 0);
		}


		cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
	}
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		batch2.dispose();
		img2.dispose();
	}
}
