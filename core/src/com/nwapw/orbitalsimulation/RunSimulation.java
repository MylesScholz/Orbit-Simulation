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
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class RunSimulation extends ApplicationAdapter implements InputProcessor {
		
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
	
	int placedPlanetCounter = 0;
	int placedSunCounter = 0;
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	
	boolean focusShift;
	boolean pauseIteration;
	boolean deleteBody;
	
	// Booleans for mouse input edge detection
	boolean newPlanet = false;
	boolean newSun = false;
	// Mouse position variables for new bodies
	int clickLeftPositionX;
	int clickLeftPositionY;
	int unclickLeftPositionX;
	int unclickLeftPositionY;
	int clickRightPositionX;
	int clickRightPositionY;
	int unclickRightPositionX;
	int unclickRightPositionY;
	
	
	OrbitalBody planet = new OrbitalBody();	
	OrbitalBody sun = new OrbitalBody();
	
	SpriteBatch batch;
	Texture backgroundTexture;
	
	private OrthographicCamera cam;
	float camX = 0;
	float camY = 0;
	float sourceX = 0;
	float sourceY = 0;
	
	
	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
    static ArrayList<Texture> availableStarTextures = new ArrayList<Texture>();

    static String[] starColors = new String[5];
	
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();
	
	BitmapFont font;
	String printPosVelAcc = "";
	
	
	boolean pauseState = false;
	
	@Override
	public void create () {
		
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
		
		
        starColors[0] = "blue";
        starColors[1] = "orange";
        starColors[2] = "red";
        starColors[3] = "white";
        starColors[4] = "yellow";

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

        for (int i = 0; i < 5; i++){
            for (int j = 1; j < 5; j++) {
                String starFileName = "stars/mainsequence/star_" + starColors[i] + "0" + j + ".png";
                textures = new Texture(starFileName);
                availableStarTextures.add(textures);
            }
        }

		// INITIALIZE IN ORDER OF MASS SMALLEST TO LARGEST
		// Name, Mass, posx, posy, velx, vely, spritewidth
		

        LibGDXTools.bodyInitialize("Sun", 10000, 25, 0, 0,0, 0, 50);
		

		LibGDXTools.bodyInitialize("Star 1", 10000, 25, 100, 100, 0, 0, 50);
		LibGDXTools.bodyInitialize("Star 2", 10000, 25, -100, -100, 0, 0, 50);

		
		batch = new SpriteBatch();

		int i = 1 + (int)(Math.random() * 8); 
        String backgroundFileName = "backgrounds/" + i + ".jpg";
        backgroundTexture = new Texture(backgroundFileName);
        
		
		//backgroundTexture = new Texture("background/3.jpg");
		
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		//backgroundTexture.setTextureWrap(TextureWrap.GL_REPEAT);
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		
		cam = new OrthographicCamera(30, 30 * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);

		
		camX = 0;
		camY = 0;
		
	}
	
	public void place() {		
		
		if (Gdx.input.isButtonPressed(0) && !newPlanet) {
			clickLeftPositionX = (int) (Gdx.input.getX() - cam.viewportWidth/2f + camX);
			clickLeftPositionY = (int) (Gdx.input.getY() - cam.viewportHeight/2f + camY);
			newPlanet = true;
		}
		
		else if (!Gdx.input.isButtonPressed(0) && newPlanet) {
			
			unclickLeftPositionX = (int) (Gdx.input.getX() - cam.viewportWidth/2f + camX);
			unclickLeftPositionY = (int) (Gdx.input.getY() - cam.viewportHeight/2f + camY);
			
			int randomMass = 1 + (int)(Math.random() * 4);
			int randomRadius = randomMass * 5;
			
			String planetName = "New Planet " + placedPlanetCounter;	
			placedPlanetCounter++;
			
			LibGDXTools.bodyInitialize(planetName, randomMass, randomRadius, clickLeftPositionX , -(clickLeftPositionY), unclickLeftPositionX - clickLeftPositionX, -(unclickLeftPositionY - clickLeftPositionY), randomRadius * 2);
			newPlanet = false;
		}
		
		if (Gdx.input.isButtonPressed(1) && !newSun) {
			clickRightPositionX = Gdx.input.getX();
			clickRightPositionY = Gdx.input.getY();
			newSun = true;
		}
		
		else if (!Gdx.input.isButtonPressed(1) && newSun) {
			
			unclickRightPositionX = Gdx.input.getX();
			unclickRightPositionY = Gdx.input.getY();
			
			int randomMass = 10000 + (int)(Math.random() * 40000);
			int randomRadius = randomMass / 800;
			
			String sunName = "New Sun " + placedSunCounter;	
			placedSunCounter++;
			
			LibGDXTools.bodyInitialize(sunName, randomMass, randomRadius, clickRightPositionX - cam.viewportWidth/4f, -(clickRightPositionY - cam.viewportHeight/4f), unclickRightPositionX - clickRightPositionX, -(unclickRightPositionY - clickRightPositionY), randomRadius * 2);
			newSun = false;
		}
	}
	

	
	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		int listLength = listOfBodies.size();

		if(Gdx.input.isKeyPressed(Input.Keys.N) && !focusShift){
			n++;
			if (n % listLength == 0) {
				n -= n;
			}
			focusShift = true;
	    }
		
		else if(!Gdx.input.isKeyPressed(Input.Keys.N) && focusShift){
			focusShift = false;
	    }
		
		if(Gdx.input.isKeyPressed(Input.Keys.B)){
			if (n % listLength == 0) {
				n -= n;
			}
			listOfBodies.get(n).posVect.set(100, 100, 100);
			listOfBodies.get(n).velVect.set(0, 0, 0);
	    }

		if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && !deleteBody){
			if (n % listLength == 0) {
				n -= n;
			}
			listOfBodies.remove(n);
			deleteBody = true;
	    }
		
		else if(!Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && deleteBody){
			deleteBody = false;
	    }
		
		if(Gdx.input.isKeyPressed(Input.Keys.P) && !pauseIteration){
			if (pauseState == false){
				pauseState = true;
			}
			else {
				pauseState = false;
			}
			pauseIteration = true;
	    }
		
		else if(!Gdx.input.isKeyPressed(Input.Keys.P) && pauseIteration){
			pauseIteration = false;
	    }
		
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)){
        	if (n % listLength == 0) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.y += 3;
        }
		
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
        	if (n % listLength == 0) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.y -= 3;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
        	if (n % listLength == 0) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.x -= 3;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
        	if (n % listLength == 0) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.x += 3;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.M)){
        	if (n % listLength == 0) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.set(0,0,0);
        }

        if (listOfBodies.size() == 0){
        	LibGDXTools.bodyInitialize("Star", 10000, 25, 0.001f, 0.001f, 0.001f, 0.001f, 40);
		}

        
		batch.begin();
		batch.draw(backgroundTexture, -cam.viewportWidth/2 + camX, -cam.viewportHeight/2 + camY, (int) camX, (int) -camY, (int) cam.viewportWidth, (int) cam.viewportHeight);
		
		
		for (int i = 0; i < listOfBodies.size(); i++) {

			OrbitalBody renderBody = listOfBodies.get(i);

            float spriteWidth = renderBody.spriteWidth;

			float spriteX = (float) renderBody.posVect.x - (spriteWidth / 2);
			float spriteY = (float) renderBody.posVect.y - (spriteWidth / 2);
			
			font.draw(batch, renderBody.name, spriteX + 10, spriteY);

			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, spriteX, spriteY, spriteWidth, spriteWidth);

			
		}

		System.out.println(camX);
		System.out.println(camY);
		System.out.println("");
		float focusX = (float) listOfBodies.get(n).posVect.x - (listOfBodies.get(n).spriteWidth / 2);
		float focusY = (float) listOfBodies.get(n).posVect.y - (listOfBodies.get(n).spriteWidth  / 2);		
		float moveX = (camX - focusX) * 1/3;
		float moveY = (camY - focusY) * 1/3;
		camX -= moveX;
		camY -= moveY;
		sourceX -= moveX;
		sourceY -= moveY;
		
		//System.out.println("moveX " + moveX);
		//System.out.println("moveY " + moveY);
		//System.out.println("");

		cam.position.set(camX, camY, 0);
		
		
		
		
		//System.out.println("Width " + cam.viewportWidth);
		//System.out.println("Height " + cam.viewportHeight);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		float frameX = camX - 470;
		float frameY = camY - 250;
		
		font.draw(batch, "Orbital Simulation", frameX + 15, frameY + 20);
		
		font.draw(batch, "(p) pause (n) focus  (d) delete (s) set pos/vel (arrow keys) vel  (0) vel = 0", frameX + 155, frameY + 40);
		
		
		if (iterationCounter % 6 == 0 || pauseState == true ) {
			printPosVelAcc = "";
	
			printPosVelAcc += "Most Pull: " + listOfBodies.get(n).mostPullingBodyName + " ";
			Vector3 currentVect = new Vector3();
			
			for (int p = 0; p < 3; p++){
				
				if (p == 0){
					printPosVelAcc += "Pos: ";
					currentVect = listOfBodies.get(n).posVect;
				}
				
				if (p == 1){
					printPosVelAcc += "  Vel: ";
					currentVect = listOfBodies.get(n).velVect;
				}
				if (p == 2){
					printPosVelAcc += "  Acc: ";
					currentVect = listOfBodies.get(n).accVect;
				}		
				
				printPosVelAcc += "(";
				for (int q = 0; q < 3; q++){
					
					float value = 0;
					
					if (q == 0){
						value = currentVect.x;
					}
					if (q == 1){
						value = currentVect.y;
					}
					if (q == 2){
						value = currentVect.z;
					}					
					
					value = Math.round(value * 100f) / 100f;
					
					if (q != 2){
						printPosVelAcc += value + ", ";
					}
					else {
						printPosVelAcc += value + ")";
					}
					
				}
				
			}
	
		}
		
		font.draw(batch, printPosVelAcc, frameX + 15, frameY + 60);	
		
		String printNumOfBodies = "Number of bodies: " + String.valueOf(listOfBodies.size());
		String printDeltaTime = "dt: " + String.valueOf(deltaTime);
		String printIterationStep = "step: " + String.valueOf(iterationCounter);
		String printFocusPlanet = "focus: " + listOfBodies.get(n).name;
		
		font.draw(batch, printNumOfBodies, frameX + 155, frameY + 20);
		font.draw(batch, printDeltaTime, frameX + 300, frameY + 20);
		font.draw(batch, printIterationStep, frameX + 360, frameY + 20);
		font.draw(batch, printFocusPlanet, frameX + 440, frameY + 20);
		
		
		
		batch.end();	

		OrbitalPhysics.passList(listOfBodies);
		//System.out.println(pauseState);

		if (pauseState == false){
			if (iterationCounter <= numOfIterations){
				timeCounter += deltaTime;
				OrbitalPhysics.iterateSimulation(deltaTime);	
				place();
				// DEBUG
				if ((iterationCounter % dataDivision) == 0){
							
	
				}				
			}	
			iterationCounter += 1;
		}
		
		
	}
	public void resize(int width, int height) {
		cam.viewportWidth = 1000f;
		cam.viewportHeight = 1000f * height/width;
		cam.update();
	}
	@Override
	public void dispose () {
		batch.dispose();
		
		for (int i = 0; i < runningTextures.size(); i++){
			runningTextures.get(i).dispose();
		}
		
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		System.out.println("yes");
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		System.out.println("scrolled");
		return false;
	}
}