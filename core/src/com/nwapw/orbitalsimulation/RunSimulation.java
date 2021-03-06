package com.nwapw.orbitalsimulation;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

import javax.management.openmbean.SimpleType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RunSimulation extends ApplicationAdapter implements ApplicationListener {

	// Constant for the force of gravity, affects how much bodies accelerate
	final static int gravConst = 100;
	
	// TODO Switches methods of calculating perturbations
	final static int perturbationCalculationMethod = 0;	// 0 = Cowell's Method
	
	//
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	
	// Specifies time used to calculate numerical integration

	static float deltaTime = (float) 0.05;	//Normal
	static float deltaPredictionTime = (float) 0.05;	//Normal

	//static float deltaTime = (float) 0.0000005;	//Slow
	//static float deltaPredictionTime = (float) 0.0000005;	//Slow

	//static float deltaTime = (float) 0.00000000001386385; // Real Time
	//static float deltaPredictionTime = (float) 0.00000000001386385; // Real Time
	static float originalDeltaTime = deltaTime;
	static float originalDeltaPredictionTime = deltaPredictionTime;
	
	
	// The max number of iterations that the simulation runs
	final static int numOfIterations = 1000000000;
	final static int numOfPredictions = 1000;
	final static float drawLimit = 1000;
	final static float predictedDrawLimit = 1000;
	
	// 0 = Focus on a particular body, 1 = free movement
	static int cameraMode = 0;
	
	// To debug
	double timeCounter = 0;
	int iterationCounter = 0;
	int predictionCounter = 0;
	int dataDivision = 10;
	
	// Cycle through focus
	public static int n = 0;
	
	int placedPlanetCounter = 0;
	int placedSunCounter = 0;
	
	FPSLogger fpsLogger = new FPSLogger();
	static String loadFileName = "";
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();

	public static ArrayList<Float> PTOldX = new ArrayList<Float>();
	public static ArrayList<Float> PTOldY = new ArrayList<Float>();
	public static ArrayList<Float> PTNewX = new ArrayList<Float>();
	public static ArrayList<Float> PTNewY = new ArrayList<Float>();
	public static ArrayList<Integer> PTBody = new ArrayList<Integer>();
	
	public static ArrayList<Float> FTOldX = new ArrayList<Float>();
	public static ArrayList<Float> FTOldY = new ArrayList<Float>();
	public static ArrayList<Float> FTNewX = new ArrayList<Float>();
	public static ArrayList<Float> FTNewY = new ArrayList<Float>();
	
	//List of vectors for comet tails
    float tailOldX;
    float tailOldY;
    float tailNewX;
    float tailNewY;
	
	// Booleans for mouse input edge detection
	boolean newPlanet = false;
	boolean newSun = false;
	boolean newSystem = false;
	
	// Mouse position variables for new bodies
	int clickLeftPositionX;
	int clickLeftPositionY;
	int currentMousePositionX;
	int currentMousePositionY;
	int unclickLeftPositionX;
	int unclickLeftPositionY;
	int clickRightPositionX;
	int clickRightPositionY;
	int unclickRightPositionX;
	int unclickRightPositionY;

	//The x and y positions of the focused body from the previous iteration
	float focusedBodyOldX;
	float focusedBodyOldY;

	//Graphics objects
	SpriteBatch batch;
	static Texture backgroundTexture;
	ShapeRenderer shapeRenderer;

	//Camera coordinate variables
	static OrthographicCamera cam;
	float camX = 0;
	float camY = 0;
	float frameX;
	float frameY;
	
	static float panX = 0;
	static float panY = 0;
	
	// zoom factor
	static float zF = 1;
	
	static float placedBodySpeed = 0.5f;

	int placedBodies = 1; //for star/planet system
	
	static boolean predictions = true;
	static int slingShot = 1;

	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
    static ArrayList<Texture> availableStarTextures = new ArrayList<Texture>();

    //List of colors of stars for texture choosing
    static String[] starColors = new String[5];

    //List of all the textures currently being used
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();

	//Font objects
	BitmapFont font;
	BitmapFont fontTitle;
	BitmapFont fontHeader;
	BitmapFont fontText;
	BitmapFont fontSubtitle;
	BitmapFont fontFocus;

	//Text to display the position, velocity, and acceleration
	String printPos;
	String printVel;
	String printAcc;

	//Booleans for interface
	static boolean sidePanelState = false;
	static boolean zoomLines = false;
	static boolean purgeState = false;
	static boolean collisionsOn = true;
	static boolean cameraPan = false;
	static Skin skin;
	
	static boolean coolBackground = false;
	static boolean party = false;
	
	static boolean pauseState = false;

	//Input processor object
	InputMultiplexer multiplexer;
	
	@Override

	public void create () {

		/* GRAPHICS & INPUTS*/

		//Initialize graphics objects and input processor
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		InputProcessor inputProcessor = new Inputs();
		
		// Allows chaining of multiple inputProcessors
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(multiplexer);

		/* CAMERA */

		//Height and width of window
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		//Set initial camera position
		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);

		//Initialize text to display position, velocity, and acceleration
		printPos = "Pos: (0.0, 0.0, 0.0)";
		printVel = "Vel: (0.0, 0.0, 0.0)";
		printAcc = "Acc: (0.0, 0.0, 0.0)";
		
		/* FONTS */

		//Initialize font objects for text
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
		FreeTypeFontParameter fTitle = new FreeTypeFontParameter();
		FreeTypeFontParameter fHeader = new FreeTypeFontParameter();
		FreeTypeFontParameter fSubtitle = new FreeTypeFontParameter();
		FreeTypeFontParameter fText = new FreeTypeFontParameter();
		FreeTypeFontParameter fFocus = new FreeTypeFontParameter();

		//Set size, position ,and color
		fTitle.size = 35;
		fTitle.shadowColor = Color.BLACK;
		fTitle.shadowOffsetX = 2;
		fTitle.shadowOffsetY = 2;
		
		fHeader.size = 25;
		fHeader.color = Color.GOLDENROD;
		
		//fHeader.color = new Color(28, 25, 54, 1f);
		
		fSubtitle.size = 18;
		fSubtitle.color = Color.CORAL;
		
		fText.size = 18;
		fText.color = Color.LIGHT_GRAY;
		
		fFocus.size = 26;
		fText.color = Color.WHITE;

		//Set up generator and format text
		fontTitle = generator.generateFont(fTitle); 
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
		
		fontHeader = generator.generateFont(fHeader);
		fontSubtitle = generator.generateFont(fSubtitle);
		fontText = generator.generateFont(fText);
		fontFocus = generator.generateFont(fFocus);
		
		fontTitle.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fontHeader.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fontSubtitle.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fontText.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fontFocus.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		generator.dispose();

		/* TEXTURES */

		//Adds names of colors to star color list
        starColors[0] = "blue";
        starColors[1] = "orange";
        starColors[2] = "red";
        starColors[3] = "white";
        starColors[4] = "yellow";

        //Loops through file names of available planet textures and adds them to available textures list
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

		//Loops through file names of available star textures and adds them to available textures list
        for (int i = 0; i < 5; i++){
            for (int j = 1; j < 5; j++) {
            	String starFileName = "stars/mainsequence/star_" + starColors[i] + "0" + j + ".png";
                textures = new Texture(starFileName);
                availableStarTextures.add(textures);
            }
        }
        
		//Selects a random background texture and sets it to wrap
		int j = 1 + (int)(Math.random() * 8); 
        String backgroundFileName = "backgrounds/" + j + ".jpg";
        backgroundTexture = new Texture(backgroundFileName);
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
        /* INITIAL BODIES */

		// Name, Mass, radius, posx, posy, velx, vely, spritewidth

        //LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 1, 250 , 250, 35, -35);
        //LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 10000, 0, 0, 0, 0);
        
        /* FILES */

        //If there are no bodies in the system, attempt to load file
        if (listOfBodies.size() == 0) {
        	if (loadFileName != "") {
				loadFile(loadFileName);
			} else {
        		loadFile();
			}
        }

        //Sets the zoom factor to the default zoom
        zF = LibGDXTools.calculateDefaultZoom(listOfBodies.get(n).spriteWidth);
	}

    public void loadFile() {
		//Gets file path of current file
        String filePath = this.getClass().getClassLoader().getResource("").getPath();   //The path of the running file
        filePath = filePath.substring(0, filePath.indexOf("/desktop")) + "/core/assets/systems/count.txt";    //Navigate to system file
        filePath = filePath.replaceAll("%20", " ");    //Remove space placeholder in file path

		//Define file readers
        File systemFile;
        FileReader in;
        BufferedReader readFile;
        String textLine;

        //Variables for loading files
        boolean fileLoaded = false;
        int i = 0;
        int fileCount = 0;

        //Read the current file number from count.txt
        try {
        	//Initialize file readers
            systemFile = new File(filePath);
            in = new FileReader(systemFile);
            readFile = new BufferedReader(in);

            //Read count.txt and put it into fileCount
            fileCount = Integer.parseInt(readFile.readLine().trim());
        } catch (FileNotFoundException e) {
        	//Catch file not found exception
            System.out.println("File Not Found: " + e.getMessage());
        } catch (IOException e) {
        	//Catch IO exception
            System.out.println("Problem Reading File: " + e.getMessage());
        }
        //Define variables of data
        String nameStr;
        float massFlt, radiusFlt, posXFlt, posYFlt, velXFlt, velYFlt, spriteWidthFlt;

        //Change working file path to the system file
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath + "/system" + fileCount + ".txt";

		try {
			//Initialize file readers
			systemFile = new File(filePath);
			in = new FileReader(systemFile);
			readFile = new BufferedReader(in);

			//Loop through the file line by line
			while ((textLine = readFile.readLine()) != null) {
				nameStr = textLine.substring(0, textLine.indexOf(","));		//Read name
				textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

				massFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read mass
				textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

				posXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x position
				textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

				posYFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read y position
				textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

				velXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x velocity
				textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

				velYFlt = Float.parseFloat(textLine.substring(0, textLine.length()));		//Read y velocity

				LibGDXTools.bodyCreate(nameStr, massFlt, posXFlt, posYFlt, velXFlt, velYFlt);
			}
			readFile.close();
			in.close();

			fileLoaded = true;
		} catch (FileNotFoundException e) {
			//Catch file not found exception
			System.out.println("File Not Found: " + e.getMessage());
			System.out.println("Creating Default File...");

			try {
				systemFile = new File(filePath);
				systemFile.createNewFile();
				loadFile("default.txt");
				filePath = filePath.substring(filePath.lastIndexOf("/"));
				saveFile(filePath);
			} catch (IOException err) {
				System.out.println("Problem Creating File: " + e.getMessage());
			}
		} catch (IOException e) {
			//Catch IO exception
			System.out.println("Problem Reading File: " + e.getMessage());
		}
    }

	public void loadFile(String fileName) {
		//Gets file path of current file
		String filePath = this.getClass().getClassLoader().getResource("").getPath();   //The path of the running file
		filePath = filePath.substring(0, filePath.indexOf("/desktop")) + "/core/assets/systems/" + fileName;    //Navigate to system file
		filePath = filePath.replaceAll("%20", " ");    //Remove space placeholder in file path

		//Define file readers
		File systemFile;
		FileReader in;
		BufferedReader readFile;
		String textLine;

		//Variables for loading files
		boolean fileLoaded = false;
		int i = 0;

		//Define variables of data
		String nameStr;
		float massFlt, radiusFlt, posXFlt, posYFlt, velXFlt, velYFlt, spriteWidthFlt;

		try {
			//Initialize file readers
			systemFile = new File(filePath);
			in = new FileReader(systemFile);
			readFile = new BufferedReader(in);

			//Loop through the file line by line
			while ((textLine = readFile.readLine()) != null) {
				if(fileName == "solar system.txt") {
					nameStr = textLine.substring(0, textLine.indexOf(","));		//Read name
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					massFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read mass
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					radiusFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));	//Read radius
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					posXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x position
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					posYFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read y position
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					velXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x velocity
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					velYFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x velocity
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					spriteWidthFlt = Float.parseFloat(textLine.substring(0, textLine.length()));	//Read sprite width

					LibGDXTools.bodyCreate(nameStr, massFlt, radiusFlt, posXFlt, posYFlt, velXFlt, velYFlt, spriteWidthFlt);	//Create body with data from file
				} else {
					nameStr = textLine.substring(0, textLine.indexOf(","));		//Read name
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					massFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read mass
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					posXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x position
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					posYFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read y position
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					velXFlt = Float.parseFloat(textLine.substring(0, textLine.indexOf(",")));		//Read x velocity
					textLine = textLine.substring(textLine.indexOf(",") + 1);	//Cut string to remove name

					velYFlt = Float.parseFloat(textLine.substring(0, textLine.length()));		//Read y velocity

					LibGDXTools.bodyCreate(nameStr, massFlt, posXFlt, posYFlt, velXFlt, velYFlt);	//Create body with data from file
				}
			}
			readFile.close();
			in.close();

			fileLoaded = true;
		} catch (FileNotFoundException e) {
			//Catch file not found exception
			System.out.println("File Not Found: " + e.getMessage());
			System.out.println("Creating Default File...");

			try {
				systemFile = new File(filePath);
				systemFile.createNewFile();
				loadFile("default.txt");
				filePath = filePath.substring(filePath.lastIndexOf("/"));
				saveFile(filePath);
			} catch (IOException err) {
				System.out.println("Problem Creating File: " + e.getMessage());
			}
		} catch (IOException e) {
			//Catch IO exception
			System.out.println("Problem Reading File: " + e.getMessage());
		}
	}

   	public void place() {
   		if (Gdx.input.isButtonPressed(0) && !newPlanet && !newSun && !newSystem) {
   			//System.out.println("planetPlace");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
   			cam.unproject(mousePos);
   			
   			clickLeftPositionX = (int) (mousePos.x / zF);
   			clickLeftPositionY = (int) (mousePos.y / zF);		
   			
   			focusedBodyOldX = listOfBodies.get(n).posVect.x;
   			focusedBodyOldY = listOfBodies.get(n).posVect.y;
   			
   			LibGDXTools.bodyCreatePlanet(clickLeftPositionX , clickLeftPositionY, 0, 0);
   			newPlanet = true;
   		} else if(Gdx.input.isButtonPressed(0) && newPlanet && !newSun && !newSystem) {
   			//System.out.println("planetHold");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
   			cam.unproject(mousePos);
   			
   			currentMousePositionX = (int) (mousePos.x / zF);
   			currentMousePositionY = (int) (mousePos.y / zF);
   			
   			listOfBodies.get(listOfBodies.size() - 1).setPosition(clickLeftPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickLeftPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedPosition(clickLeftPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickLeftPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity(slingShot * ((clickLeftPositionX - focusedBodyOldX) - (currentMousePositionX - listOfBodies.get(n).posVect.x)), slingShot * ((clickLeftPositionY - focusedBodyOldY) - (currentMousePositionY - listOfBodies.get(n).posVect.y)), 0);			
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedGravity(true);
   			if (predictions) {
				predict(listOfBodies.get(listOfBodies.size() - 1), listOfBodies.get(n));
			}
   		} else if (!Gdx.input.isButtonPressed(0) && newPlanet && !newSun && !newSystem) {
   			//System.out.println("planetMove");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
   			cam.unproject(mousePos);
   			
   			unclickLeftPositionX = (int) (mousePos.x / zF);
   			unclickLeftPositionY = (int) (mousePos.y / zF);
   			
   			listOfBodies.get(listOfBodies.size() - 1).setVelocity(slingShot * ((clickLeftPositionX - focusedBodyOldX) - (unclickLeftPositionX - listOfBodies.get(n).posVect.x)), slingShot * ((clickLeftPositionY - focusedBodyOldY) - (unclickLeftPositionY - listOfBodies.get(n).posVect.y)), 0);
   			listOfBodies.get(listOfBodies.size() - 1).setGravity(true);
   			newPlanet = false;
   		} 
   		
   		if (Gdx.input.isButtonPressed(1) && !newSun && !newPlanet && !newSystem) {
   			//System.out.println("sunPlace");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
   			cam.unproject(mousePos);
   			
   			clickRightPositionX = (int) (mousePos.x / zF);
   			clickRightPositionY = (int) (mousePos.y / zF);
   			
   			focusedBodyOldX = listOfBodies.get(n).posVect.x;
   			focusedBodyOldY = listOfBodies.get(n).posVect.y;
   			
   			LibGDXTools.bodyCreateSun(clickRightPositionX , clickRightPositionY, 0, 0);
   			newSun = true;
   		} else if(Gdx.input.isButtonPressed(1) && newSun && !newPlanet && !newSystem) {
   			//System.out.println("sunHold");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
   			cam.unproject(mousePos);
   			
   			currentMousePositionX = (int) (mousePos.x / zF);
   			currentMousePositionY = (int) (mousePos.y / zF);
   			
   			listOfBodies.get(listOfBodies.size() - 1).setPosition(clickRightPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickRightPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedPosition(clickRightPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickRightPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity(slingShot * ((clickRightPositionX - focusedBodyOldX) - (currentMousePositionX - listOfBodies.get(n).posVect.x)), slingShot * ((clickRightPositionY - focusedBodyOldY) - (currentMousePositionY - listOfBodies.get(n).posVect.y)), 0);			
   			listOfBodies.get(listOfBodies.size() - 1).setPredictedGravity(true);
   			if (predictions) {
				predict(listOfBodies.get(listOfBodies.size() - 1), listOfBodies.get(n));
			}
		} else if (!Gdx.input.isButtonPressed(1) && newSun && !newPlanet && !newSystem) {
   			//System.out.println("sunMove");
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
   			
   			cam.unproject(mousePos);
   			unclickRightPositionX = (int) (mousePos.x / zF);
   			unclickRightPositionY = (int) (mousePos.y / zF);	
   			
   			listOfBodies.get(listOfBodies.size() - 1).setPosition(clickRightPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickRightPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
   			listOfBodies.get(listOfBodies.size() - 1).setVelocity(slingShot * ((clickRightPositionX - focusedBodyOldX) - (unclickRightPositionX - listOfBodies.get(n).posVect.x)), slingShot * ((clickRightPositionY - focusedBodyOldY) - (unclickRightPositionY - listOfBodies.get(n).posVect.y)), 0);
   			listOfBodies.get(listOfBodies.size() - 1).setGravity(true);
   			
   			newSun = false;
   		}
   		
   		if (Gdx.input.isKeyPressed(43) && !newPlanet && !newSun && !newSystem) {
   			//System.out.println("O Click");
   			
   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
   			cam.unproject(mousePos);
   			
   			clickLeftPositionX = (int) (mousePos.x / zF);
   			clickLeftPositionY = (int) (mousePos.y / zF);		
   			
   			newSystem = true;
   		} else if (!Gdx.input.isKeyPressed(43) && newSystem && !newPlanet && !newSun) {
   			//System.out.println("O Unclick");

   			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
   			cam.unproject(mousePos);
   			
   			float unclickLeftPositionX = (int) (mousePos.x / zF);
   			float unclickLeftPositionY = (int) (mousePos.y / zF);					
   			
   			float velocityX = (unclickLeftPositionX - clickLeftPositionX)*placedBodySpeed/4;
   			float velocityY = (unclickLeftPositionY - clickLeftPositionY)*placedBodySpeed/4;
   			
   			//System.out.println(velocityX);
   			//System.out.println(velocityY);
   			//System.out.println("");
   			Random random = new Random();
   			float randScalar = 1 + (random.nextFloat() - 0.5f)/4;
   			
   			if (random.nextFloat() < 0.5){
   				randScalar *= -1;
   			}
   			
   			LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 43779 + randScalar*1000, clickLeftPositionX, clickLeftPositionY ,0,0);
   			listOfBodies.get(listOfBodies.size() - 1).addVelocity(-velocityX, -velocityY);
   			
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 3, clickLeftPositionX + 1709*randScalar, clickLeftPositionY - 123*randScalar, 2.2944372f*randScalar, -34.84709f*randScalar);	
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, clickLeftPositionX + 842*randScalar, clickLeftPositionY - 172*randScalar, 36f*randScalar, 60f*randScalar);	
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}				
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, clickLeftPositionX - 1127*randScalar, clickLeftPositionY - 101*randScalar, 6.7067494f*randScalar, 55.26426f*randScalar);
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}			
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 1, clickLeftPositionX +480*randScalar, clickLeftPositionY - 51*randScalar, 18.085638f*randScalar, -92.809425f*randScalar);
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}			
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 3, clickLeftPositionX +589*randScalar, clickLeftPositionY - 71*randScalar, -10.085638f*randScalar, 83.809425f*randScalar);
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, clickLeftPositionX + 4*randScalar, clickLeftPositionY -127*randScalar, 98.83999f*randScalar, 25.148289f*randScalar);
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}
   			if (random.nextFloat() < 0.5){
   				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 2, clickLeftPositionX - 1298*randScalar, clickLeftPositionY -417*randScalar, -25f*randScalar, 42.148289f*randScalar);
   				listOfBodies.get(listOfBodies.size() - 1).addVelocity(slingShot * -velocityX, slingShot * -velocityY);
   			}	
   			newSystem = false;
   		}
	}
	
   	public void predict (OrbitalBody newBody, OrbitalBody focusedBody) {
   		OrbitalPhysics.passList(listOfBodies);
   		if (pauseState == false) {
			while (predictionCounter <= numOfPredictions) {
				timeCounter += deltaPredictionTime;
				predictionCounter += 1;
				//System.out.println(predictionCounter);
				if (!listOfBodies.get(listOfBodies.size() - 1).removed) {
					FTOldX.add(newBody.predictedOldPosVect.x);
					FTOldY.add(newBody.predictedOldPosVect.y);
					FTNewX.add(newBody.predictedPosVect.x);
					FTNewY.add(newBody.predictedPosVect.y);
				} else {
					//System.out.println("Removed: " + predictionCounter + ", "  + iterationCounter + ", " + listOfBodies.get(listOfBodies.size() - 1).name);
				}
				
				Gdx.gl.glEnable(GL30.GL_BLEND);
		   		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		   		OrbitalPhysics.predictedIterateSimulation(deltaPredictionTime);
				shapeRenderer.begin(ShapeType.Line);
				for (int x = 0; x < FTOldX.size(); x++) {
					
					if (n >= listOfBodies.size()) {
        				n -= n;
        			}
					
					float velX = -0.075f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
            		float velY = -0.075f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);
            		
            		if(party) {
            			shapeRenderer.setColor(1, (float) Math.random(), (float) Math.random(), x / (float) FTOldX.size());	
            		} else {
            			shapeRenderer.setColor(1, 0, 0, x / (float) FTOldX.size());
            		}
					shapeRenderer.line(FTOldX.get(x) * zF + velX, FTOldY.get(x) * zF + velY, FTNewX.get(x) * zF + velX, FTNewY.get(x) * zF + velY);
				}
				shapeRenderer.end();
				Gdx.gl.glDisable(GL30.GL_BLEND);
			}
			while (FTOldX.size() >= predictedDrawLimit) {
				FTOldX.remove(0);
				FTOldY.remove(0);
				FTNewX.remove(0);
				FTNewY.remove(0);
			}
			while (!FTOldX.isEmpty()) {
				FTOldX.remove(0);
				FTOldY.remove(0);
				FTNewX.remove(0);
				FTNewY.remove(0);
				for (int i = 0; i < listOfBodies.size(); i++) {
					listOfBodies.get(i).setRemoved(false);
				}
				//System.out.println("Reset");
				predictionCounter = 0;
			}
		}
   	}
   	
   	public void party () {
   		if(party) {
   			for(int i = 0; i < listOfBodies.size(); i++) {
   				listOfBodies.get(i).setTexture(LibGDXTools.bodyTextureChooser(listOfBodies.get(i).mass));
   	   			//int j = 11;
   				if((i % 10) == 0) {
   					//System.out.println((i % 10) == 0);
   					//System.out.println("Random Background");
   					int j = 1 + (int)(Math.random() * 11); 
   					String backgroundFileName = "backgrounds/" + j + ".jpg";
   					RunSimulation.backgroundTexture = new Texture(backgroundFileName);
   					RunSimulation.backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
   				}
   			}
   		}
   	}
   	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (purgeState == true) {
			for (int i = 0; i < listOfBodies.size(); i++){
				if (listOfBodies.get(i).accVect.len() < 0.1 && listOfBodies.get(i).mass < 10000 && listOfBodies.get(i).gravity){
					listOfBodies.remove(i);
				}
			}			
		}

        if (listOfBodies.size() == 0){
        	loadFile();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
        RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y += 1/zF;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y -= 1/zF;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x -= 1/zF;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x += 1/zF;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.M)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.set(0,0,0);
        }
 
        if (Gdx.input.isButtonPressed(Buttons.MIDDLE)){
        	zF = 1;
        }
        
        // System.out.println(sidePanel.getWidth());
        // else if (sidePanelState == false && popupTable.getWidth() != 0f){
        // 	System.out.println("not 0f");
        // }
        
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		batch.begin();
		batch.draw(backgroundTexture, -cam.viewportWidth/2 + camX, -cam.viewportHeight/2 + camY, (int) camX, (int) -camY, (int) cam.viewportWidth, (int) cam.viewportHeight);
		//System.out.println("Background Print");
		batch.end();

		for (int i = 0; i < listOfBodies.size(); i++) {
			if (!listOfBodies.get(i).oldPosVect.isZero()) {
				PTOldX.add(listOfBodies.get(i).oldPosVect.x);
				PTOldY.add(listOfBodies.get(i).oldPosVect.y);
				PTNewX.add(listOfBodies.get(i).posVect.x);
				PTNewY.add(listOfBodies.get(i).posVect.y);
				PTBody.add(i);
			}
		}
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Line);
		for (int x = 0; x < PTOldX.size(); x++) {
			while (PTOldX.size() >= drawLimit) {
				PTOldX.remove(0);
				PTOldY.remove(0);
				PTNewX.remove(0);
				PTNewY.remove(0);
				PTBody.remove(0);
			}
			
			if (n >= listOfBodies.size()) {
				n -= n;
			}
			
			float velX = -0.025f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
			float velY = -0.025f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);
			
			if(party) {
    			shapeRenderer.setColor(1, (float) Math.random(), (float) Math.random(), x / (float) FTOldX.size());	
    		} else {
    			shapeRenderer.setColor(1, 1, 1, x / (float) FTOldX.size());
    		}
			shapeRenderer.line(PTOldX.get(x) * zF + velX, PTOldY.get(x) * zF + velY, PTNewX.get(x) * zF + velX, PTNewY.get(x) * zF + velY); 	
			//System.out.println("Line Print");
		}
		shapeRenderer.end();	
		Gdx.gl.glDisable(GL30.GL_BLEND);
		
		//Draw comet tails
		boolean starsPresent = false;
        for (int i = 0; i < listOfBodies.size(); i++) {
            if (listOfBodies.get(i).mass == 1) {
                for (int j = 0; j < listOfBodies.size(); j++) {
                    if (listOfBodies.get(j).mass >= 10000 && listOfBodies.get(i).mostPullingBodyName == listOfBodies.get(j).name) {
                    	starsPresent = true;
                    	
                        tailOldX = (listOfBodies.get(i).posVect.x + ((listOfBodies.get(i).posVect.x - listOfBodies.get(j).posVect.x) / listOfBodies.get(i).posVect.dst(listOfBodies.get(j).posVect)));
                        tailOldY = (listOfBodies.get(i).posVect.y + ((listOfBodies.get(i).posVect.y - listOfBodies.get(j).posVect.y) / listOfBodies.get(i).posVect.dst(listOfBodies.get(j).posVect)));
                        tailNewX = (listOfBodies.get(i).posVect.x);
                        tailNewY = (listOfBodies.get(i).posVect.y);

						listOfBodies.get(i).cometTailX.add(0, (float) tailOldX);
						listOfBodies.get(i).cometTailY.add(0, (float) tailOldY);
						listOfBodies.get(i).cometTailX.add(0, (float) tailNewX);
						listOfBodies.get(i).cometTailY.add(0, (float) tailNewY);
                    }
                }

                Gdx.gl.glEnable(GL30.GL_BLEND);
                Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
                shapeRenderer.begin(ShapeType.Line);

                while (listOfBodies.get(i).cometTailX.size() >= drawLimit / 4) {
					listOfBodies.get(i).cometTailX.remove(listOfBodies.get(i).cometTailX.size() - 1);
					listOfBodies.get(i).cometTailY.remove(listOfBodies.get(i).cometTailY.size() - 1);
                }

                float velX = -0.025f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
        		float velY = -0.025f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);
                
                if(party) {
        			shapeRenderer.setColor(1, (float) Math.random(), (float) Math.random(), 1);	
        		} else {
        			shapeRenderer.setColor(0, 1, 1, 1);
        		}
				shapeRenderer.line(tailNewX * zF + velX, tailNewY * zF + velY, tailOldX * zF + velX, tailOldY * zF + velY);

				for (int j = 1; j < listOfBodies.get(i).cometTailX.size(); j++) {
					if (j == 1 && j < listOfBodies.get(i).cometTailX.size() - 1) {
						listOfBodies.get(i).cometTailX.set(j + 1, listOfBodies.get(i).cometTailX.get(j + 1) + (tailOldX - tailNewX));
						listOfBodies.get(i).cometTailY.set(j + 1, listOfBodies.get(i).cometTailY.get(j + 1) + (tailOldY - tailNewY));
					} else {
						listOfBodies.get(i).cometTailX.set(j, listOfBodies.get(i).cometTailX.get(j) + (tailOldX - tailNewX));
						listOfBodies.get(i).cometTailY.set(j, listOfBodies.get(i).cometTailY.get(j) + (tailOldY - tailNewY));
					}
				}

                for (int j = 1; j < listOfBodies.get(i).cometTailX.size() - 1; j++) {
                	
                	if (n >= listOfBodies.size()) {
        				n -= n;
        			}
                	
            		
            		if(party) {
            			shapeRenderer.setColor(1, (float) Math.random(), (float) Math.random(), 1 - (j / (float) listOfBodies.get(i).cometTailX.size()));	
            		} else {
            			shapeRenderer.setColor(0, 1, 1, 1 - (j / (float) listOfBodies.get(i).cometTailX.size()));
            		}
                    shapeRenderer.line(listOfBodies.get(i).cometTailX.get(j) * zF + velX, listOfBodies.get(i).cometTailY.get(j) * zF + velY, listOfBodies.get(i).cometTailX.get(j + 1) * zF + velX, listOfBodies.get(i).cometTailY.get(j + 1) * zF + velY);
                }

                if (!starsPresent) {
                	listOfBodies.get(i).cometTailX.clear();
                	listOfBodies.get(i).cometTailY.clear();
                }
                shapeRenderer.end();
                Gdx.gl.glDisable(GL30.GL_BLEND);
            }
        }

        OrbitalPhysics.passList(listOfBodies);
		if(pauseState == false) {
			if(iterationCounter <= numOfIterations){
				timeCounter += deltaTime;
				OrbitalPhysics.iterateSimulation(deltaTime);
				passError();
				place();
				party();
				if((iterationCounter % dataDivision) == 0) {
					//fpsLogger.log();
				}
			iterationCounter += 1;
			}	
		}
		
		batch.begin();
		for (int i = 0; i < listOfBodies.size(); i++) {
			OrbitalBody renderBody = listOfBodies.get(i);


            float spriteWidth = renderBody.spriteWidth;

            float spriteX = (float) zF * (renderBody.posVect.x - (spriteWidth / 2));
			float spriteY = (float) zF * (renderBody.posVect.y - (spriteWidth / 2));
						
			float frameX = 0;
			float frameY = 0;
			
			if (!pauseState){
				if (n >= listOfBodies.size()) {
					n -= n;
				}
				if (!listOfBodies.get(n).gravity) {
					n += 1;
				}
				if (n >= listOfBodies.size()) {
					n -= n;
				}
				if (listOfBodies.size() == 0) {
					loadFile();
				}
				frameX = spriteX - 0.075f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
				frameY = spriteY - 0.075f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);
			} else {
				frameX = spriteX;
				frameY = spriteY;
			}
			

			//if (1/(Math.pow(zF, 3)) < spriteWidth) {
				if (i == n){
					if (party) {
						fontFocus.setColor(1, (float) 0.078, (float) 0.576, 1);
					} else {
						fontFocus.setColor(1, 1, 1, 1);
					}
					fontFocus.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.25f*spriteWidth*zF/10);
	
				}
				else {
					if (party) {
						fontSubtitle.setColor(1, (float) 0.078, (float) 0.576, 1);
					} else {
						fontSubtitle.setColor(1, 1, 1, 1);
					}
					fontSubtitle.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.25f*spriteWidth*zF/10);
				}
			//}
			
			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, frameX, frameY, (float) (spriteWidth * zF), (float) (spriteWidth * zF));
			//System.out.println("Body Print");
		}
        float focusX = 0;
		float focusY = 0;

		
		
		if (n >= listOfBodies.size()) {
			n -=n;
		}
		
		if (!listOfBodies.get(n).gravity) {
			n += 1;
		}
		
		if (n >= listOfBodies.size()) {
			n -= n;
		}
		
		if (listOfBodies.size() == 0) {
			loadFile();
		}

		
		if (sidePanelState == true){
			focusX += cam.viewportWidth/6;
		}
		if (cameraPan == false) {
			

		focusX = (float) listOfBodies.get(n).posVect.x * zF + 0/8*listOfBodies.get(n).spriteWidth *zF;
	    focusY = (float) listOfBodies.get(n).posVect.y * zF + 0/8*listOfBodies.get(n).spriteWidth *zF;
			
	    float moveX = (camX - focusX) * 2/3;
			float moveY = (camY - focusY) * 2/3;
			
			camX -= moveX;
			camY -= moveY;
		
			camX += 1.1f*cam.viewportHeight/40;

			if (pauseState == false){
				frameX = camX - 0.05005f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
				frameY = camY - 0.0500f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);	
				 
			}
			else {
				frameX = camX;
				frameY = camY;		
			}
			
		}
		else {
	        if (Gdx.input.isKeyPressed(Input.Keys.W)){
	        	panY += 10;
	        	
	        }

	        if (Gdx.input.isKeyPressed(Input.Keys.A)){
	        	panX -= 10;
	        }
	        
	        if (Gdx.input.isKeyPressed(Input.Keys.S)){
	        	panY -= 10;
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.D)){
	        	panX += 10;
	        }        
	        focusX = panX;
			focusY = panY;
			
			float moveX = (camX - panX) * 2/3;
			float moveY = (camY - panY) * 2/3;
			
			camX -= moveX;
			camY -= moveY;
		
			camX += 1.1f*cam.viewportHeight/40;
			

			frameX = camX;
			frameY = camY;		
		        
		}
		


		
		cam.position.set(camX, camY, 0);
		cam.update();	


		
		if (sidePanelState == true && pauseState == true){
			if (party) {
				fontHeader.setColor(1, (float) 0.078, (float) 0.576, 1);
			} else {
				fontHeader.setColor(1, 1, 1, 1);
			}
			fontHeader.draw(batch, "PAUSED & SAVED", frameX - 0.97f*cam.viewportWidth/2, frameY + 8.3f*cam.viewportHeight/20);
		} else if (sidePanelState == false && pauseState == true){
			if (party) {
				fontHeader.setColor(1, (float) 0.078, (float) 0.576, 1);
			} else {
				fontHeader.setColor(1, 1, 1, 1);
			}
			fontHeader.draw(batch, "PAUSED & SAVED", frameX - 1.4f*cam.viewportWidth/20, frameY + 8.3f*cam.viewportHeight/20);
		}
				
		batch.end();		
		if (sidePanelState == true){
			Gdx.gl.glEnable(GL30.GL_BLEND);
			Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.8f);
			shapeRenderer.rect(frameX + cam.viewportWidth/6, frameY - cam.viewportHeight/2, cam.viewportWidth/2, cam.viewportHeight);
			shapeRenderer.end();	
			Gdx.gl.glDisable(GL30.GL_BLEND);
		}
		shapeRenderer.begin(ShapeType.Line);
		

		//focusX -= zF*listOfBodies.get(n).spriteWidth/2;
		//focusY -= zF*listOfBodies.get(n).spriteWidth/2;
		if (sidePanelState == true) {
			shapeRenderer.setColor(0, 0, 0, 1);	
		} else {
			shapeRenderer.setColor(1, 1, 1, 1);
		}
		
		//shapeRenderer.line(new Vector2(focusX - 10, focusY), new Vector2(focusX + 10, focusY));
		//shapeRenderer.line(new Vector2(focusX, focusY - 10), new Vector2(focusX, focusY+10));
		
		if (n >= listOfBodies.size()) {
			n -= n;
		}
		
		float velX = -0.075f*listOfBodies.get(n).velVect.x*zF*(deltaTime*20);
		float velY = -0.075f*listOfBodies.get(n).velVect.y*zF*(deltaTime*20);

		shapeRenderer.line(new Vector2(focusX - 10 + velX, focusY + velY), new Vector2(focusX + 10 + velX, focusY + velY));
		shapeRenderer.line(new Vector2(focusX + velX, focusY - 10 + velY), new Vector2(focusX + velX, focusY + 10 + velY));
		
		shapeRenderer.end();
		
        // Workaround to make side panel items appear above shapeRenderer transparent rectangle

		batch.begin();
		
		if (party) {
			fontHeader.setColor(1, (float) 0.078, (float) 0.576, 1);
			fontSubtitle.setColor(1, (float) 0.078, (float) 0.576, 1);
			fontText.setColor(1, (float) 0.078, (float) 0.576, 1);
			fontTitle.setColor(1, (float) 0.078, (float) 0.576, 1);
		} else {
			fontHeader.setColor(1, 1, 1, 1);
			fontSubtitle.setColor(1, 1, 1, 1);
			fontText.setColor(1, 1, 1, 1);
			fontTitle.setColor(1, 1, 1, 1);
		}
		
		if(sidePanelState == true) {

			String printNumOfBodies = "# of bodies: " + String.valueOf(listOfBodies.size());
			String printDeltaTime = "dt: " + String.valueOf(deltaTime);
			String printIterationStep = "step: " + String.valueOf(iterationCounter);
			String printFocusPlanet = "FOCUS: " + listOfBodies.get(n).name;			
			String printFocusNumber = "focused body: " + n;
			String printMostAttraction = "Most Grav. Attraction: " + listOfBodies.get(n).mostPullingBodyName;
			String printCamMode = "cam: ";
			if (cameraPan == true){
				printCamMode += "pan";
			}
			else {
				printCamMode += "follow";
			}
			
			fontHeader.draw(batch, "CONTROLS", frameX + 2.5f*cam.viewportWidth/12, frameY + 11.1f*cam.viewportHeight/24);			
			fontSubtitle.draw(batch, LibGDXTools.underlineCalculation("CONTROLS") + "____", frameX + 2.5f*cam.viewportWidth/12,  frameY + 10.85f*cam.viewportHeight/24);
			fontText.draw(batch, "(SCROLL OR +/-) Zoom  (Z) Zoom lines", frameX + 2.5f*cam.viewportWidth/12, frameY + 10*cam.viewportHeight/24);
			fontText.draw(batch, "(LEFT CLICK) Create New Planet (Q) Step method", frameX + 2.5f*cam.viewportWidth/12, frameY + 9*cam.viewportHeight/24);
			fontText.draw(batch, "(RIGHT CLICK) Create New Star (U) Collisions On/Off", frameX + 2.5f*cam.viewportWidth/12, frameY + 8*cam.viewportHeight/24);			
			fontText.draw(batch, "(ARROW KEYS) Move Focused Body (I) Reset dT", frameX + 2.5f*cam.viewportWidth/12, frameY + 7*cam.viewportHeight/24);
			fontText.draw(batch, "(SPACEBAR) Pause & Save  (V) Predictions On/Off", frameX + 2.5f*cam.viewportWidth/12, frameY + 6*cam.viewportHeight/24);
			fontText.draw(batch, "(CTRL) Focus on body closest to mouse", frameX + 2.5f*cam.viewportWidth/12, frameY + 5*cam.viewportHeight/24);
			fontText.draw(batch, "(M) Reset Vel  (T) Purge extraneous planets", frameX + 2.5f*cam.viewportWidth/12, frameY + 4*cam.viewportHeight/24);
			fontText.draw(batch, "(N) Change Focus (B) Change Focus to Next Star", frameX + 2.5f*cam.viewportWidth/12, frameY + 3*cam.viewportHeight/24);
			fontText.draw(batch, "(C) Switch Camera Modes  (WASD) Panning", frameX + 2.5f*cam.viewportWidth/12, frameY + 2*cam.viewportHeight/24);
			fontText.draw(batch, "( , ) Decrease dt  ( . ) Increase dt", frameX + 2.5f*cam.viewportWidth/12, frameY + 1*cam.viewportHeight/24);
			fontText.draw(batch, "(O) Spawn a Star/Planets System", frameX + 2.5f*cam.viewportWidth/12, frameY + 0*cam.viewportHeight/24);
							
			
			
			fontHeader.draw(batch, "SIMULATION SETTINGS", frameX + 2.5f*cam.viewportWidth/12, frameY - 0.9f*cam.viewportHeight/24);
			fontSubtitle.draw(batch, "___________________________", frameX + 2.5f*cam.viewportWidth/12, frameY -1.15f*cam.viewportHeight/24);
			fontText.draw(batch, printNumOfBodies, frameX + 2.5f*cam.viewportWidth/12, frameY - 2*cam.viewportHeight/24);
			fontText.draw(batch, printIterationStep, frameX + 2.5f*cam.viewportWidth/12, frameY - 3*cam.viewportHeight/24);
			fontText.draw(batch, printDeltaTime, frameX + 2.5f*cam.viewportWidth/12, frameY - 4*cam.viewportHeight/24);
			fontText.draw(batch, printCamMode, frameX + 2.5f*cam.viewportWidth/12, frameY - 5*cam.viewportHeight/24);
			fontHeader.draw(batch, printFocusPlanet.toUpperCase(), frameX + 2.5f*cam.viewportWidth/12, frameY - 6*cam.viewportHeight/24);
			fontSubtitle.draw(batch, LibGDXTools.underlineCalculation(printFocusPlanet) + "____", frameX + 2.5f*cam.viewportWidth/12, frameY - 6.25f*cam.viewportHeight/24);
			fontText.draw(batch, printMostAttraction, frameX + 2.5f*cam.viewportWidth/12, frameY - 7*cam.viewportHeight/24);
			fontText.draw(batch, "Mass: " + listOfBodies.get(n).mass, frameX + 2.5f*cam.viewportWidth/12, frameY - 8*cam.viewportHeight/24);


			if (iterationCounter % 6 == 0 || pauseState == true ) {
				printPos = "Pos: ";
				printVel = "Vel: ";
				printAcc = "Acc: ";
				Vector3 currentVect = new Vector3();
				
				currentVect.set(listOfBodies.get(n).posVect);
				//currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printPos += currentVect;

				currentVect.set(listOfBodies.get(n).velVect);
				//currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printVel += currentVect;

				currentVect.set(listOfBodies.get(n).accVect);
				currentVect.set( (float) Math.round(currentVect.x*100f)/100f, (float) Math.round(currentVect.y*100f)/100f, (float) Math.round(currentVect.z*100f)/100f);
				printAcc += currentVect;
			} 
			fontText.draw(batch, printPos, (float) (frameX + 2.5f*cam.viewportWidth/12), (float) (frameY - 9*cam.viewportHeight/24));
			fontText.draw(batch, printVel, (float) (frameX + 2.5f*cam.viewportWidth/12), (float) (frameY - 10*cam.viewportHeight/24));
			fontText.draw(batch, printAcc, (float) (frameX + 2.5f*cam.viewportWidth/12), (float) (frameY - 11*cam.viewportHeight/24));
			
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX - 0.97f*cam.viewportWidth/2, frameY + 0.93f*cam.viewportHeight/2);	
			fontText.draw(batch, "(press ESC for FULLSCREEN)", frameX - 0.97f*cam.viewportWidth/2, frameY - 0.92f*cam.viewportHeight/2);	
			
		}
		else {
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX  - 1.5f*cam.viewportWidth/12, frameY + 0.93f*cam.viewportHeight/2);	
			fontHeader.draw(batch, "(press ESC for more INFO)", frameX  - 1.1f*cam.viewportWidth/12, frameY - 0.92f*cam.viewportHeight/2);	
		
		}
		
		batch.end();
	
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		float orderOfMag = RunSimulation.zF;
		float orderOfMagCounter = 0;
		
		while (orderOfMag < 1){
			orderOfMag *= 100;	
			orderOfMagCounter++;
		}
		//System.out.println(orderOfMagCounter);
		
		//shapeRenderer.line(new Vector2(100*zF, 200*zF), new Vector2(200*zF, 100*zF));
		
		//focusX += 50 * zF * Math.pow(10, orderOfMagCounter);
		//focusY += 50 * zF * Math.pow(10, orderOfMagCounter);
		
		if (sidePanelState == false) {			
			shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		}
		else {
			shapeRenderer.setColor(0f, 0f, 0f, 0f);
		}
		
		if (zoomLines){
		
		for (int k = 0; k < 200; k++) {
			float xSpacing = (float) ((k-100) * 300 * zF * Math.pow(18, orderOfMagCounter));
			float ySpacing = (float) ((k-100) * 300 * zF * Math.pow(18, orderOfMagCounter));
			shapeRenderer.line(new Vector2(focusX - cam.viewportWidth, focusY + ySpacing), new Vector2(focusX + cam.viewportWidth, focusY + ySpacing));
			shapeRenderer.line(new Vector2(focusX + xSpacing, focusY - cam.viewportHeight), new Vector2(focusX + xSpacing, focusY + cam.viewportHeight));
		}
		
		}
		
		shapeRenderer.end();
		
		// Set the viewport to the whole screen.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// Draw anywhere on the screen.
		//stage.act(0.01f);
		//stage.draw();
		
		// Restore the stage's viewport.
		//stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}
	
	public static void saveFile() {
        String filePath = RunSimulation.class.getProtectionDomain().getCodeSource().getLocation().getPath();    //The path of the RunSimulation
        filePath = filePath.substring(0, filePath.indexOf("/build")) + "/assets/systems/count.txt";    //Navigate to system file
        filePath = filePath.replaceAll("%20", " ");
        File systemFile;
        FileWriter out;
        FileReader in;
        BufferedWriter writeFile;
        BufferedReader readFile;

        Boolean fileWritten = false;
        int i = 0;
        String textLine = "";
        int fileCount = 0;

        try {
            //Read count.txt to get file count, increment it, and write it to the file
            systemFile = new File(filePath);
            in = new FileReader(systemFile);
            readFile = new BufferedReader(in);

            fileCount = Integer.parseInt(readFile.readLine().trim()) + 1;

            out = new FileWriter(systemFile);
            writeFile = new BufferedWriter(out);

            writeFile.write(String.valueOf(fileCount));

            writeFile.close();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Problem Reading File: " + e.getMessage());
        }

        //Switch to system.txt
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath + "/system" + fileCount + ".txt";

        while (fileWritten == false && i < 2) {
            try {
                //Write data to system file
                systemFile = new File(filePath);
                out = new FileWriter(systemFile);
                writeFile = new BufferedWriter(out);

                for (int j = 0; j < listOfBodies.size(); j++) {
                    textLine = listOfBodies.get(j).name + "," + listOfBodies.get(j).mass + "," + listOfBodies.get(j).posVect.x + "," + listOfBodies.get(j).posVect.y;
                    textLine = textLine + "," + listOfBodies.get(j).velVect.x + "," + listOfBodies.get(j).velVect.y;

                    writeFile.write(textLine);
                    writeFile.newLine();
                }
                writeFile.close();
                out.close();

                fileWritten = true;
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found: " + e.getMessage());
                System.out.println("Creating New File...");

                try {
                    systemFile = new File(filePath);
                    systemFile.createNewFile();
                } catch (IOException err) {
                    System.out.println("Problem Creating File: " + e.getMessage());
                }

                i++;
            } catch (IOException e) {
                System.out.println("Problem Writing to File: " + e.getMessage());
            }
        }
    }

	public static void saveFile(String fileName) {
		String filePath = RunSimulation.class.getProtectionDomain().getCodeSource().getLocation().getPath();    //The path of the RunSimulation
		filePath = filePath.substring(0, filePath.indexOf("/build")) + "/assets/systems/" + fileName;    //Navigate to system file
		filePath = filePath.replaceAll("%20", " ");
		File systemFile;
		FileWriter out;
		BufferedWriter writeFile;

		Boolean fileWritten = false;
		int i = 0;
		String textLine = "";

		while (fileWritten == false && i < 2) {
			try {
				//Write data to system file
				systemFile = new File(filePath);
				out = new FileWriter(systemFile);
				writeFile = new BufferedWriter(out);

				for (int j = 0; j < listOfBodies.size(); j++) {
					textLine = listOfBodies.get(j).name + "," + listOfBodies.get(j).mass + "," + listOfBodies.get(j).posVect.x + "," + listOfBodies.get(j).posVect.y;
					textLine = textLine + "," + listOfBodies.get(j).velVect.x + "," + listOfBodies.get(j).velVect.y;

					writeFile.write(textLine);
					writeFile.newLine();
				}
				writeFile.close();
				out.close();

				fileWritten = true;
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + e.getMessage());
				System.out.println("Creating New File...");

				try {
					systemFile = new File(filePath);
					systemFile.createNewFile();
				} catch (IOException err) {
					System.out.println("Problem Creating File: " + e.getMessage());
				}

				i++;
			} catch (IOException e) {
				System.out.println("Problem Writing to File: " + e.getMessage());
			}
		}
	}
	
	public void resize(int width, int height) {
		//stage.getViewport().update(width, height, true);
		cam.viewportWidth = 1500f;
		cam.viewportHeight = cam.viewportWidth * height/width;
		cam.update();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		for(int i = 0; i < runningTextures.size(); i++){
			runningTextures.get(i).dispose();
		}
	}
	
	public void passError() {
		if (OrbitalPhysics.passIndexError()) {
			n -= n;
		}
	}
	
	static int passIndex() {
		return n;
	}
}