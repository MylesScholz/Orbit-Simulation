package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
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
	int dataDivision = 1;
	
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
	String printPos;
	String printVel;
	String printAcc;
	
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
		// Name, Mass, radius, posx, posy, velx, vely, spritewidth
		
        //LibGDXTools.bodyInitialize("Mercury", 330f, 2f, 57900f, 0f, 0f, 474f, 4f);
        //LibGDXTools.bodyInitialize("Earth", 5972f, 6f, 149600f, 0f, 0f, 298f, 12f);
        //LibGDXTools.bodyInitialize("Moon", 73f, 2f, 149984f, 0f, 0f, 338f, 4f);
        //LibGDXTools.bodyInitialize("Sun", 198850000f, 696f, 0f, 0f, 0f, 0f, 1392f);
        
        // Multiply given velocity by 10
        // Both mercury and moon seem to deviate from this rule
        
        //LibGDXTools.bodyInitialize("Moon", 730f, 17f, 149984f, 0f, 0f, 1f, 34f);
        //LibGDXTools.bodyInitialize("Earth", 59724f, 64f, 149600f, 0f, 0f, 29.8f, 128f);
        //LibGDXTools.bodyInitialize("Sun", 19885000000f, 6957f, 0f, 0f, 0f, 0f, 13914f);
        
        //LibGDXTools.bodyInitialize("Moon", 73000000f, 1.7375f, 1499840f, 0f, 0f, 1f, 3.475f);
        //LibGDXTools.bodyInitialize("Earth", 5972400000f, 6.378f, 1496000f, 0f, 0f, 29.8f, 12.756f);
        //LibGDXTools.bodyInitialize("Sun", 1988500000000000f, 695.7f, 0f, 0f, 0f, 0f, 1391.4f);
        
        //LibGDXTools.bodyInitialize("Sun", 19885000000f, 695.7f, 0f, 0f, 0f, 0f, 1391.4f);
        //LibGDXTools.bodyInitialize("Mercury", 3301.1f, 2.4397f, 579100f, 0f, 0f, 260f, 4.8794f);
        //LibGDXTools.bodyInitialize("Venus", 48675f, 6.0518f, 1499840f, 0f, 0f, 350f, 12.1036f);
        //LibGDXTools.bodyInitialize("Earth", 59723f, 6.371008f, 1496000f, 0f, 0f, 298f, 12.742016f);
        //LibGDXTools.bodyInitialize("Moon", 734.6f, 1.7374f, 1499844f, 0f, 0f, 338f, 3.4748f);
        //LibGDXTools.bodyInitialize("Mars", 6417.1f, 3.389f, 2279200f, 0f, 0f, 241f, 6.778f);
        //LibGDXTools.bodyInitialize("Jupiter", 18981900f, 69.911f, 7785700f, 0f, 0f, 131f, 139.822f);
        //LibGDXTools.bodyInitialize("Saturn", 5683400f, 58.232f, 14335300f, 0f, 0f, 97f, 116.464f);
        //LibGDXTools.bodyInitialize("Uranus", 868130f, 25.362f, 28724600f, 0f, 0f, 68f, 50.724f);
        //LibGDXTools.bodyInitialize("Neptune", 1024130f, 24.622f, 44950600f, 0f, 0f, 54f, 49.244f);
        //LibGDXTools.bodyInitialize("Pluto", 130.3f, 1.187f, 59063800f, 0f, 0f, 47f, 2.374f);
        
        //LibGDXTools.bodyInitialize("Sun", 19885000000f, 695.7f, 0f, 0f, 0f, 0f, 1391.4f);
        //LibGDXTools.bodyInitialize("Mercury", 3301.1f, 2.4397f, 579100f, 0f, 0f, 474f, 4.8794f);
        //LibGDXTools.bodyInitialize("Venus", 48675f, 6.0518f, 1499840f, 0f, 0f, 350f, 12.1036f);
        //LibGDXTools.bodyInitialize("Earth", 59723f, 6.371008f, 1496000f, 0f, 0f, 298f, 12.742016f);
        //LibGDXTools.bodyInitialize("Moon", 734.6f, 1.7374f, 1499844f, 0f, 0f, 308f, 3.4748f);
        //LibGDXTools.bodyInitialize("Mars", 6417.1f, 3.389f, 2279200f, 0f, 0f, 241f, 6.778f);
        //LibGDXTools.bodyInitialize("Jupiter", 18981900f, 69.911f, 7785700f, 0f, 0f, 131f, 139.822f);
        //LibGDXTools.bodyInitialize("Saturn", 5683400f, 58.232f, 14335300f, 0f, 0f, 97f, 116.464f);
        //LibGDXTools.bodyInitialize("Uranus", 868130f, 25.362f, 28724600f, 0f, 0f, 68f, 50.724f);
        //LibGDXTools.bodyInitialize("Neptune", 1024130f, 24.622f, 44950600f, 0f, 0f, 54f, 49.244f);
        //LibGDXTools.bodyInitialize("Pluto", 130.3f, 1.187f, 59063800f, 0f, 0f, 47f, 2.374f);
        
        // Mass divided by 100000000000000000000 [10^20] | Everything else divided by 1000 [10^3]
        
        //LibGDXTools.bodyInitialize("Moon", 73000000000000000000000f, 1737.5f, 1499840000f, 0f, 0f, 1f, 3475f);
        //LibGDXTools.bodyInitialize("Earth", 5972400000000000000000000f, 6378f, 1496000000f, 0f, 0f, 29.8f, 12756f);
        //LibGDXTools.bodyInitialize("Sun", 1988500000000000000000000000000f, 695700f, 0f, 0f, 0f, 0f, 1391400f);
        
        //LibGDXTools.bodyInitialize("Moon", 7f, 1, 9324, 0, 0, 200f, 2);
        //LibGDXTools.bodyInitialize("Earth", 600f, 4, 9300, 0, 0, 186f, 8);
        //LibGDXTools.bodyInitialize("Sun", 200000000f, 430, 0, 0, 0, 0, 860);
        
		//LibGDXTools.bodyInitialize("Star 1", 10000, 25, 100, 100, 0, 0, 50);
		//LibGDXTools.bodyInitialize("Star 2", 10000, 25, -100, -100, 0, 0, 50);
        
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
		
		printPos = "Pos: (0.0, 0.0, 0.0)";
		printVel = "Vel: (0.0, 0.0, 0.0)";
		printAcc = "Acc: (0.0, 0.0, 0.0)";
		
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
			
			String planetName = LibGDXTools.nameGen();
			
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
			
			String sunName = LibGDXTools.nameGen();
			
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
        	LibGDXTools.bodyInitialize("Star", 10000, 25, 0, 0, 0, 0, 50);
		}
        
        if (Gdx.input.isButtonPressed(Buttons.MIDDLE)){
            zF = 1;
        }
        
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		for (int i = 0; i < listOfBodies.size(); i++) {
			Gdx.gl.glEnable(GL30.GL_BLEND);
			Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(0, 0, 0, 0);
			shapeRenderer.line(listOfBodies.get(i).posVect.x, listOfBodies.get(i).posVect.y, listOfBodies.get(i).oldPosVect.x, listOfBodies.get(i).oldPosVect.y);
			shapeRenderer.end();	
			Gdx.gl.glDisable(GL30.GL_BLEND);	
		}
		
		batch.begin();
		batch.draw(backgroundTexture, -cam.viewportWidth/2 + camX, -cam.viewportHeight/2 + camY, (int) camX, (int) -camY, (int) cam.viewportWidth, (int) cam.viewportHeight);
				
		for (int i = 0; i < listOfBodies.size(); i++) {

			OrbitalBody renderBody = listOfBodies.get(i);

            float spriteWidth = renderBody.spriteWidth;

			float spriteX = (float) renderBody.posVect.x * zF - (spriteWidth / 2);
			float spriteY = (float) renderBody.posVect.y * zF - (spriteWidth / 2);
			
			font.draw(batch, renderBody.name, spriteX + 0.7f*spriteWidth*zF/2, spriteY + 1.5f*spriteWidth*zF/10);

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

		float moveX = (camX - focusX) * 1/3;
		float moveY = (camY - focusY) * 1/3;
		
		camX -= moveX;
		camY -= moveY;
		sourceX -= moveX;
		sourceY -= moveY;
		
		camX += 1.1f*cam.viewportHeight/40;
		
		if (sidePanelState == true){
	
			
		}
		
		cam.position.set(camX, camY, 0);
		cam.update();		
		batch.end();

		if (sidePanelState == true){
			Gdx.gl.glEnable(GL30.GL_BLEND);
			Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
			 shapeRenderer.begin(ShapeType.Filled);
			 shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.8f);
			 shapeRenderer.rect(camX + cam.viewportWidth/6, camY - cam.viewportHeight/2, cam.viewportWidth/2, cam.viewportHeight);
			 shapeRenderer.end();	
			 Gdx.gl.glDisable(GL30.GL_BLEND);
		}
		

		// Workaround to make side panel items appear above shapeRenderer transparent rectangle
		batch.begin();
		if (sidePanelState == true) {
			
			String printNumOfBodies = "# of bodies: " + String.valueOf(listOfBodies.size());
			String printDeltaTime = "dt: " + String.valueOf(deltaTime);
			String printIterationStep = "step: " + String.valueOf(iterationCounter);
			String printFocusPlanet = "FOCUS: " + listOfBodies.get(n).name;			
			String printMostAttraction = "Most Grav. Attraction: " + listOfBodies.get(n).mostPullingBodyName;
			
			font.draw(batch, "CONTROLS", camX + 2.5f*cam.viewportWidth/12, camY + 9*cam.viewportHeight/20);			
			font.draw(batch, LibGDXTools.underlineCalculation("CONTROLS") + "_", camX + 2.5f*cam.viewportWidth/12,  camY + 8.9f*cam.viewportHeight/20);
			font.draw(batch, "(Scroll) Zoom", camX + 2.5f*cam.viewportWidth/12, camY + 8*cam.viewportHeight/20);
			font.draw(batch, "(Left Click) Create new planet", camX + 2.5f*cam.viewportWidth/12, camY + 7*cam.viewportHeight/20);
			font.draw(batch, "(Right Click) Create new star", camX + 2.5f*cam.viewportWidth/12, camY + 6*cam.viewportHeight/20);			
			font.draw(batch, "(Arrow Keys) Move Focused Body", camX + 2.5f*cam.viewportWidth/12, camY + 5*cam.viewportHeight/20);
			font.draw(batch, "(P) Pause   (N) Change Focus", camX + 2.5f*cam.viewportWidth/12, camY + 4*cam.viewportHeight/20);
			font.draw(batch, "(M) Reset Current Body's Veloicty", camX + 2.5f*cam.viewportWidth/12, camY + 3*cam.viewportHeight/20);
		
			font.draw(batch, "SIMULATION SETTINGS", camX + 2.5f*cam.viewportWidth/12, camY + cam.viewportHeight/20);
			font.draw(batch, LibGDXTools.underlineCalculation("SIMULATION SETTINGS"), camX + 2.5f*cam.viewportWidth/12, camY + 0.9f*cam.viewportHeight/20);
			font.draw(batch, printNumOfBodies, camX + 2.5f*cam.viewportWidth/12, camY);
			font.draw(batch, printIterationStep, camX + 2.5f*cam.viewportWidth/12, camY - cam.viewportHeight/20);
			font.draw(batch, printDeltaTime, camX + 2.5f*cam.viewportWidth/12, camY - 2*cam.viewportHeight/20);

			font.draw(batch, printFocusPlanet, camX + 2.5f*cam.viewportWidth/12, camY - 4*cam.viewportHeight/20);
			font.draw(batch, LibGDXTools.underlineCalculation(printFocusPlanet), camX + 2.5f*cam.viewportWidth/12, camY - 4.1f*cam.viewportHeight/20);
			font.draw(batch, printMostAttraction, camX + 2.5f*cam.viewportWidth/12, camY - 5*cam.viewportHeight/20);
		
			if (iterationCounter % 6 == 0 || pauseState == true ) {
				printPos = "Pos: ";
				printVel = "Vel: ";
				printAcc = "Acc: ";

				Vector3 currentVect = new Vector3();
				
				currentVect.set(listOfBodies.get(n).posVect);
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printPos += currentVect;

				currentVect.set(listOfBodies.get(n).velVect);
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printVel += currentVect;

				currentVect.set(listOfBodies.get(n).accVect);
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printAcc += currentVect;
						
			}
			font.draw(batch, printPos, camX + 2.5f*cam.viewportWidth/12, camY - 6*cam.viewportHeight/20);
			font.draw(batch, printVel, camX + 2.5f*cam.viewportWidth/12, camY - 7*cam.viewportHeight/20);
			font.draw(batch, printAcc, camX + 2.5f*cam.viewportWidth/12, camY - 8*cam.viewportHeight/20);
			
			font.draw(batch, "ORBITAL SIMULATION", camX - 0.97f*cam.viewportWidth/2, camY + 0.93f*cam.viewportHeight/2);	
			
		}
		else {
			font.draw(batch, "ORBITAL SIMULATION", camX  - 0.9f*cam.viewportWidth/12, camY + 0.93f*cam.viewportHeight/2);	
			font.draw(batch, "(press ESC for more info)", camX  - 0.91f*cam.viewportWidth/12, camY - 0.93f*cam.viewportHeight/2);	
			
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
					/*
					float sunMercuryDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(1).posVect);
					float sunVenusDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(2).posVect);
					float sunEarthDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(3).posVect);
					float earthMoonDist = listOfBodies.get(3).posVect.dst(listOfBodies.get(4).posVect);
					float sunMarsDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(5).posVect);
					float sunJupiterDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(5).posVect);
					float sunSaturnDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(6).posVect);
					float sunUranusDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(7).posVect);
					float sunNeptuneDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(8).posVect);
					float sunPlutoDist = listOfBodies.get(0).posVect.dst(listOfBodies.get(9).posVect);
					System.out.println("sunMercuryDist: " + sunMercuryDist);
					System.out.println("sunVenusDist: " + sunVenusDist);
					System.out.println("sunEarthDist: " + sunEarthDist);
					System.out.println("earthMoonDist: " + earthMoonDist);
					System.out.println("sunMarsDist: " + sunMarsDist);
					System.out.println("sunJupiterDist: " + sunJupiterDist);
					System.out.println("sunSaturnDist: " + sunSaturnDist);
					System.out.println("sunUranusDist: " + sunUranusDist);
					System.out.println("sunNeptuneDist: " + sunNeptuneDist);
					System.out.println("sunPlutoDist: " + sunPlutoDist);
					//System.out.println("");
					//System.out.println(listOfBodies.get(0).name);
					//System.out.println(listOfBodies.get(0).posVect.x);
					//System.out.println(listOfBodies.get(0).posVect.y);
					//System.out.println("");
					//System.out.println(listOfBodies.get(1).name);
					//System.out.println(listOfBodies.get(1).posVect.x);
					//System.out.println(listOfBodies.get(1).posVect.y);
					//System.out.println("");
					System.out.println("");
					*/
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