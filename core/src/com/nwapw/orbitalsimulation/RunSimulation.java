package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
	final static float deltaTime = (float) 0.1;
	
	// The max number of iterations that the simulation runs
	final static int numOfIterations = 100000000;
	
	// 0 = Focus on a particular body, 1 = free movement
	static int cameraMode = 0;
	
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
	boolean panelShift;
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
	ShapeRenderer shapeRenderer;
	
	static OrthographicCamera cam;
	float camX = 0;
	float camY = 0;
	float sourceX = 0;
	float sourceY = 0;
	
	// zoom factor
	static float zF = 1;
	
	// Makes camera transition either smooth (for moving focus) or fast (for zooming)
	static float camTransition = 1/6;
	
	
	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
    static ArrayList<Texture> availableStarTextures = new ArrayList<Texture>();

    static String[] starColors = new String[5];
	
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();
	
	BitmapFont font;
	String printPosVelAcc = "";
	
	boolean sidePanelState = false;
	int sidePanelWidth = 200;

	
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
		

        LibGDXTools.bodyCreate("Sun", 10000, 0,0, 0, 0);
        LibGDXTools.bodyCreate("Planet", 1, 250,250, 40, -40);

		//LibGDXTools.bodyInitialize("Star 1", 10000, 25, 100, 100, 0, 0, 50);
		//LibGDXTools.bodyInitialize("Star 2", 10000, 25, -100, -100, 0, 0, 50);

		shapeRenderer = new ShapeRenderer();
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
		
		
		InputProcessor inputProcessor = new Inputs();
		Gdx.input.setInputProcessor(inputProcessor);
		
	}
	
	public void place() {		
		
		if (Gdx.input.isButtonPressed(0) && !newPlanet) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    clickLeftPositionX = (int) (mousePos.x / zF);
			clickLeftPositionY = (int) (mousePos.y / zF);		


			newPlanet = true;
		}
		
		else if (!Gdx.input.isButtonPressed(0) && newPlanet) {
			
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    unclickLeftPositionX = (int) (mousePos.x / zF);
			unclickLeftPositionY = (int) (mousePos.y / zF);		

		
			int randomMass = 1 + (int)(Math.random() * 4);
			
			String planetName = "New Planet " + placedPlanetCounter;	
			placedPlanetCounter++;
			
			LibGDXTools.bodyCreate(planetName, randomMass, clickLeftPositionX , clickLeftPositionY, unclickLeftPositionX - clickLeftPositionX, unclickLeftPositionY - clickLeftPositionY);
			newPlanet = false;
		}
		
		if (Gdx.input.isButtonPressed(1) && !newSun) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    clickRightPositionX = (int) (mousePos.x / zF);
			clickRightPositionY = (int) (mousePos.y / zF);		
		
		  
			newSun = true;
		}
		
		else if (!Gdx.input.isButtonPressed(1) && newSun) {
			
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    unclickRightPositionX = (int) (mousePos.x / zF);
			unclickRightPositionY = (int) (mousePos.y / zF);	
				
			int randomMass = 10000 + (int)(Math.random() * 40000);
			
			String sunName = "New Sun " + placedSunCounter;	
			placedSunCounter++;
			
			LibGDXTools.bodyCreate(sunName, randomMass, clickRightPositionX, clickRightPositionY, unclickRightPositionX - clickRightPositionX, -(unclickRightPositionY - clickRightPositionY));
			newSun = false;
		}
	}
	

	
	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isKeyPressed(Input.Keys.N) && !focusShift){
			n++;
			if (n >= listOfBodies.size()) {
				n -= n;
			}
			focusShift = true;
			camTransition = 2/3;
	    }
		
		else if(!Gdx.input.isKeyPressed(Input.Keys.N) && focusShift){
			focusShift = false;
	    }
		
		if(Gdx.input.isKeyPressed(Input.Keys.B)){
			if (n >= listOfBodies.size()) {
				n -= n;
			}
			listOfBodies.get(n).posVect.set(100, 100, 100);
			listOfBodies.get(n).velVect.set(0, 0, 0);
	    }

		if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && !deleteBody){
			if (n >= listOfBodies.size()) {
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
		
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && !panelShift){
        	if (sidePanelState == true){
        		sidePanelState = false;
        	}
        	else {
        		sidePanelState = true;
        	}
        	panelShift = true;
        	// camTransition = 1/6;
	    }
		else if(!Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && panelShift){
			panelShift = false;
		}
		
		
		else if(!Gdx.input.isKeyPressed(Input.Keys.P) && pauseIteration){
			pauseIteration = false;
	    }
		
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)){
        	if (n >= listOfBodies.size()) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.y += 3;
        }
		
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
        	if (n >= listOfBodies.size()) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.y -= 3;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
        	if (n >= listOfBodies.size()) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.x -= 3;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
        	if (n >= listOfBodies.size()) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.x += 3;
        }
       

        if(Gdx.input.isKeyPressed(Input.Keys.M)){
        	if (n >= listOfBodies.size()) {
				n -= n;
			}
        	listOfBodies.get(n).velVect.set(0,0,0);
        }
        

		
		
        if (listOfBodies.size() == 0){
        	LibGDXTools.bodyInitialize("Star", 10000, 25, 0, 0, 0, 0, 40);
		}

        if (Gdx.input.isButtonPressed(Buttons.MIDDLE)){
            zF = 1;
        }
        
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		
		


		
		batch.begin();
		batch.draw(backgroundTexture, -cam.viewportWidth/2 + camX, -cam.viewportHeight/2 + camY, (int) camX, (int) -camY, (int) cam.viewportWidth, (int) cam.viewportHeight);
		
		
		for (int i = 0; i < listOfBodies.size(); i++) {

			OrbitalBody renderBody = listOfBodies.get(i);

            float spriteWidth = renderBody.spriteWidth;

			float spriteX = (float) renderBody.posVect.x * zF - (spriteWidth / 2);
			float spriteY = (float) renderBody.posVect.y * zF - (spriteWidth / 2);
			
			font.draw(batch, renderBody.name, spriteX + 10, spriteY);

			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, spriteX, spriteY, (float) (spriteWidth * zF), (float) (spriteWidth * zF));

			
		}

        float focusX = 0;
		float focusY = 0;

        if (n >= listOfBodies.size()) {
            n -= n;
        }
        focusX = (float) listOfBodies.get(n).posVect.x * zF - (listOfBodies.get(n).spriteWidth / 2);
        focusY = (float) listOfBodies.get(n).posVect.y * zF - (listOfBodies.get(n).spriteWidth / 2);
		
		if (sidePanelState == true){
			focusX += sidePanelWidth;
		}
		
		float moveX = (camX - focusX) * 2/3;
		float moveY = (camY - focusY) * 2/3;
		
		camX -= moveX;
		camY -= moveY;
		sourceX -= moveX;
		sourceY -= moveY;
		
		
		float frameX = camX - 425;
		float frameY = camY - 250;
		
		System.out.println(frameX);
		System.out.println(frameY);
		
		if (sidePanelState == true){
	
			
		}
		
		cam.position.set(camX, camY, 0);
		cam.update();	




		
		font.draw(batch, "Orbital Simulation", frameX + 15, frameY + 20);
				
		font.draw(batch, "(p) pause (n) focus  (d) delete (s) set pos/vel (arrow keys) vel  (0) vel = 0", frameX + 155, frameY + 40);

		
		if (iterationCounter % 6 == 0 || pauseState == true ) {
            if (n >= listOfBodies.size()) {
                n -= n;
            }
			printPosVelAcc = "";
	
			printPosVelAcc += "Most Pull: " + listOfBodies.get(n).mostPullingBodyName + " ";
			Vector3 currentVect = new Vector3();
			
			for (int p = 0; p < 3; p++){
				
				if (p == 0){
                    if (n >= listOfBodies.size()) {
                        n -= n;
                    }
					printPosVelAcc += "Pos: ";
					currentVect = listOfBodies.get(n).posVect;
				}
				
				if (p == 1){
                    if (n >= listOfBodies.size()) {
                        n -= n;
                    }
					printPosVelAcc += "  Vel: ";
					currentVect = listOfBodies.get(n).velVect;
				}
				if (p == 2){
                    if (n >= listOfBodies.size()) {
                        n -= n;
                    }
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

        if (n >= listOfBodies.size()) {
            n -= n;
        }
		String printNumOfBodies = "Number of bodies: " + String.valueOf(listOfBodies.size());
		String printDeltaTime = "dt: " + String.valueOf(deltaTime);
		String printIterationStep = "step: " + String.valueOf(iterationCounter);
		String printFocusPlanet = "focus: " + listOfBodies.get(n).name;
		
		font.draw(batch, printNumOfBodies, frameX + 155, frameY + 20);
		font.draw(batch, printDeltaTime, frameX + 300, frameY + 20);
		font.draw(batch, printIterationStep, frameX + 360, frameY + 20);
		font.draw(batch, printFocusPlanet, frameX + 440, frameY + 20);
		
		if (sidePanelState == true){
			 
			 shapeRenderer.begin(ShapeType.Filled);
			 shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.8f);
			 shapeRenderer.rect(camX + cam.viewportWidth/6, camY - cam.viewportHeight/2, cam.viewportWidth/3, cam.viewportHeight);
			 shapeRenderer.end();		 
		}
		
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
		cam.viewportHeight = cam.viewportWidth * height/width;
		cam.update();

	}
	@Override
	public void dispose () {
		batch.dispose();
		
		for (int i = 0; i < runningTextures.size(); i++){
			runningTextures.get(i).dispose();
		}
		
	}


}