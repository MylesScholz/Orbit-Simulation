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

	/* CONTROLS
	 * 
	 * P - Purge all planets not near stars
	 * X - new galaxy
	 * CTRL - Focus on closest body to mouse
	 * 
	 */
	

	// Constant for the force of gravity, affects how much bodies accelerate
	final static int gravConst = 100;
	
	// TODO Switches methods of calculating perturbations
	final static int perturbationCalculationMethod = 0;	// 0 = Cowell's Method
	
	//
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	
	// Specifies time used to calculate numerical integration

	static float deltaTime = (float) 0.05;
	static float deltaPredictionTime = (float) 1;

	
	// The max number of iterations that the simulation runs
	final static int numOfIterations = 100000000;
	final static int numOfPredictions = 100;
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
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();

	public static ArrayList<Float> PTOldX = new ArrayList<Float>();
	public static ArrayList<Float> PTOldY = new ArrayList<Float>();
	public static ArrayList<Float> PTNewX = new ArrayList<Float>();
	public static ArrayList<Float> PTNewY = new ArrayList<Float>();
	public static ArrayList<Integer> PTBody = new ArrayList<Integer>();
	public static ArrayList<Integer> createdBody = new ArrayList<Integer>();
	
	public static ArrayList<Float> FTOldX = new ArrayList<Float>();
	public static ArrayList<Float> FTOldY = new ArrayList<Float>();
	public static ArrayList<Float> FTNewX = new ArrayList<Float>();
	public static ArrayList<Float> FTNewY = new ArrayList<Float>();
	public static ArrayList<Integer> FTBody = new ArrayList<Integer>();
	
	//List of vectors for comet tails
    float tailOldX;
    float tailOldY;
    float tailNewX;
    float tailNewY;
	
	// Booleans for mouse input edge detection
	boolean newPlanet = false;
	boolean newSun = false;
	
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
	
	float focusedBodyOldX;
	float focusedBodyOldY;
	
	SpriteBatch batch;
	Texture backgroundTexture;
	ShapeRenderer shapeRenderer;
	
	static OrthographicCamera cam;
	float camX = 0;
	float camY = 0;
	float sourceX = 0;
	float sourceY = 0;
	float frameX;
	float frameY;
	float adjustX = 0;
	float adjustY = 0;
	
	static float panX = 0;
	static float panY = 0;
	
	// zoom factor
	static float zF = 1;
	static float zFTransition = 0;
	
	static float placedBodySpeed = 0.5f;
	
	Texture textures;
	static ArrayList<Texture> availablePlanetTextures = new ArrayList<Texture>();
    static ArrayList<Texture> availableStarTextures = new ArrayList<Texture>();

    static String[] starColors = new String[5];
	
	static ArrayList<Texture> runningTextures = new ArrayList<Texture>();
	
	BitmapFont font;
	BitmapFont fontTitle;
	BitmapFont fontHeader;
	BitmapFont fontText;
	BitmapFont fontSubtitle;
	BitmapFont fontFocus;
	
	String printPos;
	String printVel;
	String printAcc;
	
	static boolean sidePanelState = false;
	static boolean zoomLines = false;
	static boolean purgeState = false;
	static boolean collisionsOn = true;
	static boolean cameraPan = false;
	static Skin skin;

	Stage stage;
	Table rootTable;
	Table upperTable;
	Table popupTable;
	Table sidePanel;

	Table dockTable;
	
	Stack interfaceStack;
	static boolean pauseState = false;
	static int savedIndicator = 0;
	
	InputMultiplexer multiplexer;
	
	@Override

	public void create () {				
        
		/* SCENE2D*/
		/*
		stage = new Stage(new ExtendViewport(1366, 768)); 
		
		rootTable = new Table();
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
		
		rootTable.setDebug(true);

		 skin = new Skin(Gdx.files.internal("uiskin.json"));
		 
		 final TextButton pauseButton = new TextButton("Pause", skin, "default");
 	     
		 final TextButton saveButton = new TextButton("Save", skin, "default");
  
	     
		 interfaceStack = new Stack();
		 rootTable.add(interfaceStack).expand();
		 
		 //Label title = new Label("Orbital Simulation", skin ,"Roboto-Bold");
		 //interfaceStack.add(title);
		 
		 
	     
	     upperTable = new Table();
	     interfaceStack.add(upperTable);
	     
	     popupTable = new Table();
	     upperTable.add(popupTable).width(600f).height(600f);
	     
	     Container imageContainer = new Container();
	     Container textContainer = new Container();
	     
	     popupTable.add(imageContainer).height(400f).width(1000f);
	     popupTable.row();
	     
	     //Label popupText = new Label();
	     
	     popupTable.add(textContainer).height(200f);
	    
	     
	     sidePanel = new Table();
	     rootTable.add(sidePanel).width(500f);
	     
	     rootTable.row();
	     
	     dockTable = new Table();
	     rootTable.add(dockTable);
	     
	     dockTable.add(new TextButton("Zoom In", skin, "default")).width(75).height(Gdx.graphics.getHeight()/20);
	     dockTable.add(new TextButton("Zoom Out", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);
	     dockTable.add(new TextButton("Cam Mode", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);	     
	     dockTable.add(new TextButton("Focus", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);	  

	     
	     dockTable.add(pauseButton).pad(Gdx.graphics.getHeight()/40).width(75);
	     dockTable.add(saveButton).pad(Gdx.graphics.getHeight()/40).width(75);
	     dockTable.add(new TextButton("Load", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);	
	     dockTable.add(new TextButton("Debug", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);	     
	     dockTable.add(new TextButton("Objectives", skin, "default")).width(75).pad(Gdx.graphics.getHeight()/40);	  
	    
	     pauseButton.addListener(new ClickListener(){
	    	 @Override 
	    	 public void clicked(InputEvent event, float x, float y){
	            
	              if (pauseState == true){
	            	  pauseState = false;
	            	  pauseButton.setText("Pause");
	              }
	              else {
	            	  pauseState = true;
	            	  pauseButton.setText("Run");
	              }	
	            }
	        });	  
	     saveButton.addListener(new ClickListener(){
	    	 @Override 
	    	 public void clicked(InputEvent event, float x, float y){
	            
	    		savedIndicator = 50;

	
	            }
	        });
	     
	   // table.addActor(saveButton);	
	   // table.row();
	    //table.addActor(pauseButton);
		*/
	    

		/* GRAPHICS & INPUTS*/
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		InputProcessor inputProcessor = new Inputs();
		
		// Allows chaining of multiple inputProcessors
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		//multiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(multiplexer);

					
		/* CAMERA */
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		
		printPos = "Pos: (0.0, 0.0, 0.0)";
		printVel = "Vel: (0.0, 0.0, 0.0)";
		printAcc = "Acc: (0.0, 0.0, 0.0)";
		
		/* FONTS */ 		 
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
		FreeTypeFontParameter fTitle = new FreeTypeFontParameter();
		FreeTypeFontParameter fHeader = new FreeTypeFontParameter();
		FreeTypeFontParameter fSubtitle = new FreeTypeFontParameter();
		FreeTypeFontParameter fText = new FreeTypeFontParameter();
		FreeTypeFontParameter fFocus = new FreeTypeFontParameter();
		
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
        
		int j = 1 + (int)(Math.random() * 8); 
        String backgroundFileName = "backgrounds/" + j + ".jpg";
        backgroundTexture = new Texture(backgroundFileName);		
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
        /* INITIAL BODIES */
		// Name, Mass, radius, posx, posy, velx, vely, spritewidth

        //LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 1, 250 , 250, 35, -35);
        //LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 10000, 0, 0, 0, 0);
        
        /* FILES */
        
        if (listOfBodies.size() == 0) {
            loadFile();
        }
        
        zF = LibGDXTools.calculateDefaultZoom(listOfBodies.get(n).spriteWidth);
	}


    public void loadFile() {
        String filePath = this.getClass().getClassLoader().getResource("").getPath();   // The path of the running file
        filePath = filePath.substring(0, filePath.indexOf("/desktop")) + "/core/assets/systems/count.txt";    //Navigate to system file
        filePath = filePath.replaceAll("%20", " ");

        File systemFile;
        FileReader in;
        BufferedReader readFile;
        String textLine;

        boolean fileLoaded = false;
        int i = 0;
        int fileCount = 0;

        try {
            systemFile = new File(filePath);
            in = new FileReader(systemFile);
            readFile = new BufferedReader(in);

            fileCount = Integer.parseInt(readFile.readLine().trim());
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Problem Reading File: " + e.getMessage());
        }
        String nameStr;
        float massFlt, posXFlt, posYFlt, velXFlt, velYFlt;

        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath + "/system" + fileCount + ".txt";

        while (fileLoaded == false && i < 2) {
            try {
                systemFile = new File(filePath);
                in = new FileReader(systemFile);
                readFile = new BufferedReader(in);

                while ((textLine = readFile.readLine()) != null) {
                    nameStr = textLine.substring(0, textLine.indexOf(","));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    massFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    posXFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    posYFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    velXFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    velYFlt = (float) Double.parseDouble(textLine.substring(0, textLine.length()));

                    LibGDXTools.bodyCreate(nameStr, massFlt, posXFlt, posYFlt, velXFlt, velYFlt);
                }
                readFile.close();
                in.close();

                fileLoaded = true;
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found: " + e.getMessage());
                System.out.println("Loading Default File...");

                filePath = filePath.substring(0, filePath.lastIndexOf("/"));
                filePath = filePath + "/default.txt";
                i++;
            } catch (IOException e) {
                System.out.println("Problem Reading File: " + e.getMessage());
            }
        }
    }

    public void loadFile(String fileName) {
        String filePath = this.getClass().getClassLoader().getResource("").getPath();   // The path of the running file
        filePath = filePath.substring(0, filePath.indexOf("/desktop")) + "/core/assets/systems/" + fileName;    //Navigate to system file
        filePath = filePath.replaceAll("%20", " ");

        File systemFile;
        FileReader in;
        BufferedReader readFile;
        String textLine;

        boolean fileLoaded = false;
        int i = 0;

        String nameStr;
        float massFlt, posXFlt, posYFlt, velXFlt, velYFlt;

        while (fileLoaded == false && i < 2) {
            try {
                systemFile = new File(filePath);
                in = new FileReader(systemFile);
                readFile = new BufferedReader(in);

                while ((textLine = readFile.readLine()) != null) {
                    nameStr = textLine.substring(0, textLine.indexOf(","));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    massFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    posXFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    posYFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    velXFlt = (float) Double.parseDouble(textLine.substring(0, textLine.indexOf(",")));
                    textLine = textLine.substring(textLine.indexOf(",") + 1);
                    velYFlt = (float) Double.parseDouble(textLine.substring(0, textLine.length()));

                    LibGDXTools.bodyCreate(nameStr, massFlt, posXFlt, posYFlt, velXFlt, velYFlt);
                }
                readFile.close();
                in.close();

                fileLoaded = true;
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found: " + e.getMessage());
                System.out.println("Loading Default File...");

                filePath = filePath.substring(0, filePath.lastIndexOf("/"));
                filePath = filePath + "/default.txt";
                i++;
            } catch (IOException e) {
                System.out.println("Problem Reading File: " + e.getMessage());
            }
        }
    }

   	public void place() {		
		if (Gdx.input.isButtonPressed(0) && !newPlanet && !newSun) {
			//System.out.println("planetPlace");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		    cam.unproject(mousePos);

		    clickLeftPositionX = (int) (mousePos.x / zF);
			clickLeftPositionY = (int) (mousePos.y / zF);		
			
			focusedBodyOldX = listOfBodies.get(n).posVect.x;
			focusedBodyOldY = listOfBodies.get(n).posVect.y;
			
			LibGDXTools.bodyCreatePlanet(clickLeftPositionX , clickLeftPositionY, 0, 0);
			newPlanet = true;
		} else if(Gdx.input.isButtonPressed(0) && newPlanet && !newSun) {
			//System.out.println("planetHold");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		    cam.unproject(mousePos);
		    
		    currentMousePositionX = (int) (mousePos.x / zF);
			currentMousePositionY = (int) (mousePos.y / zF);
			
			listOfBodies.get(listOfBodies.size() - 1).setPredictedGravity(true);
			listOfBodies.get(listOfBodies.size() - 1).setPosition(clickLeftPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickLeftPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
			listOfBodies.get(listOfBodies.size() - 1).setPredictedPosition(clickLeftPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickLeftPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
			//listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity(0, 0, 0);			
			listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity((clickLeftPositionX - focusedBodyOldX) - (currentMousePositionX - listOfBodies.get(n).posVect.x), (clickLeftPositionY - focusedBodyOldY) - (currentMousePositionY - listOfBodies.get(n).posVect.y), 0);			
			predict(listOfBodies.get(listOfBodies.size() - 1), listOfBodies.get(n));
		} else if (!Gdx.input.isButtonPressed(0) && newPlanet && !newSun) {
			//System.out.println("planetMove");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		    cam.unproject(mousePos);
		    
		    unclickLeftPositionX = (int) (mousePos.x / zF);
			unclickLeftPositionY = (int) (mousePos.y / zF);
			
			listOfBodies.get(listOfBodies.size() - 1).setVelocity((clickLeftPositionX - focusedBodyOldX) - (unclickLeftPositionX - listOfBodies.get(n).posVect.x), (clickLeftPositionY - focusedBodyOldY) - (unclickLeftPositionY - listOfBodies.get(n).posVect.y), 0);
			listOfBodies.get(listOfBodies.size() - 1).setGravity(true);
			newPlanet = false;
		} 
		
		if (Gdx.input.isButtonPressed(1) && !newSun && !newPlanet) {
			//System.out.println("sunPlace");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		    cam.unproject(mousePos);
		    
		    clickRightPositionX = (int) (mousePos.x / zF);
			clickRightPositionY = (int) (mousePos.y / zF);
			focusedBodyOldX = listOfBodies.get(n).posVect.x;
			focusedBodyOldY = listOfBodies.get(n).posVect.y;
			
			LibGDXTools.bodyCreateSun(clickRightPositionX , clickRightPositionY, 0, 0);
			newSun = true;
		} else if(Gdx.input.isButtonPressed(1) && newSun && !newPlanet) {
			//System.out.println("sunHold");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		    cam.unproject(mousePos);
		    currentMousePositionX = (int) (mousePos.x / zF);
			currentMousePositionY = (int) (mousePos.y / zF);
			
			listOfBodies.get(listOfBodies.size() - 1).setPredictedGravity(true);
			listOfBodies.get(listOfBodies.size() - 1).setPosition(clickRightPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickRightPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
			listOfBodies.get(listOfBodies.size() - 1).setPredictedPosition(clickRightPositionX + listOfBodies.get(n).posVect.x - focusedBodyOldX, clickRightPositionY + listOfBodies.get(n).posVect.y - focusedBodyOldY, 0);
			//listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity(0, 0, 0);			
			listOfBodies.get(listOfBodies.size() - 1).setPredictedVelocity((clickRightPositionX - focusedBodyOldX) - (currentMousePositionX - listOfBodies.get(n).posVect.x), (clickRightPositionY - focusedBodyOldY) - (currentMousePositionY - listOfBodies.get(n).posVect.y), 0);			
			predict(listOfBodies.get(listOfBodies.size() - 1), listOfBodies.get(n));
		} else if (!Gdx.input.isButtonPressed(1) && newSun && !newPlanet) {
			//System.out.println("sunMove");
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    
			cam.unproject(mousePos);
		    unclickRightPositionX = (int) (mousePos.x / zF);
			unclickRightPositionY = (int) (mousePos.y / zF);	
			
			listOfBodies.get(listOfBodies.size() - 1).setVelocity((clickRightPositionX - focusedBodyOldX) - (unclickRightPositionX - listOfBodies.get(n).posVect.x), (clickRightPositionY - focusedBodyOldY) - (unclickRightPositionY - listOfBodies.get(n).posVect.y), 0);
			listOfBodies.get(listOfBodies.size() - 1).setGravity(true);
			
			newSun = false;
		}
	}
	
   	public void predict (OrbitalBody newBody, OrbitalBody focusedBody) {
   		OrbitalPhysics.passList(listOfBodies);
		if (pauseState == false) {
			while (predictionCounter <= numOfPredictions) {
				timeCounter += deltaPredictionTime;
				predictionCounter += 1;
				if (!listOfBodies.get(listOfBodies.size() - 1).removed) {
					FTOldX.add(newBody.predictedOldPosVect.x);
					FTOldY.add(newBody.predictedOldPosVect.y);
					FTNewX.add(newBody.predictedPosVect.x);
					FTNewY.add(newBody.predictedPosVect.y);
				} else {
					//System.out.println("Removed: " + predictionCounter + ", "  + iterationCounter);
				}
				OrbitalPhysics.predictedIterateSimulation(deltaPredictionTime);
				Gdx.gl.glEnable(GL30.GL_BLEND);
		   		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
				shapeRenderer.begin(ShapeType.Line);
				for (int x = 0; x < FTOldX.size(); x++) {
					while (FTOldX.size() >= predictedDrawLimit) {
						FTOldX.remove(0);
						FTOldY.remove(0);
						FTNewX.remove(0);
						FTNewY.remove(0);
					}
					shapeRenderer.setColor(1, 0, 0, x / (float) FTOldX.size());
					shapeRenderer.line(FTOldX.get(x)*zF, FTOldY.get(x)*zF, FTNewX.get(x)*zF, FTNewY.get(x)*zF);
				}
				shapeRenderer.end();
				Gdx.gl.glDisable(GL30.GL_BLEND);
			}
			while (!FTOldX.isEmpty()) {
				FTOldX.remove(0);
				FTOldY.remove(0);
				FTNewX.remove(0);
				FTNewY.remove(0);
				listOfBodies.get(listOfBodies.size() - 1).setRemoved(false);
				//System.out.println("Reset");
				predictionCounter = 0;
			}
		}
   	}
   	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (purgeState == true) {
			for (int i = 0; i < listOfBodies.size(); i++){
				if (listOfBodies.get(i).accVect.len() < 0.1 && listOfBodies.get(i).mass < 10000){
					listOfBodies.remove(i);
				}
			}			
		}


		
        if (listOfBodies.size() == 0){
        	loadFile();
        }
		


        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
        RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y += 3;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y -= 3;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x -= 3;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x += 3;
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
			shapeRenderer.setColor(1, 1, 1, x / (float) PTBody.size());
			//System.out.println("Line Print");
			shapeRenderer.line(PTOldX.get(x)*zF, PTOldY.get(x)*zF, PTNewX.get(x)*zF, PTNewY.get(x)*zF); 	
			// - 0.15f*listOfBodies.get(PTBody.get(x)).velVect.x*zF
		}
		shapeRenderer.end();	
		Gdx.gl.glDisable(GL30.GL_BLEND);
		
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
					fontFocus.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.25f*spriteWidth*zF/10);
	
				}
				else {
					fontSubtitle.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.25f*spriteWidth*zF/10);
	
				}
			//}
			
			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, frameX, frameY, (float) (spriteWidth * zF), (float) (spriteWidth * zF));
			//System.out.println("Body Print");
		}
        float focusX = 0;
		float focusY = 0;

		/*
        focusX = (float) listOfBodies.get(n).posVect.x * zF - zF*(listOfBodies.get(n).spriteWidth / 2);
        focusY = (float) listOfBodies.get(n).posVect.y * zF - zF*(listOfBodies.get(n).spriteWidth / 2);
		*/
		
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
			
			focusX = (float) listOfBodies.get(n).posVect.x * zF - (listOfBodies.get(n).spriteWidth / 8)*zF;
	        focusY = (float) listOfBodies.get(n).posVect.y * zF - (listOfBodies.get(n).spriteWidth / 8)*zF;

			
	
			
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
			
			float moveX = (camX - focusX) * 2/3;
			float moveY = (camY - focusY) * 2/3;
			
			camX -= moveX;
			camY -= moveY;
		
			camX += 1.1f*cam.viewportHeight/40;
			
			frameX = camX;
			frameY = camY;					        
		}
		


		
		cam.position.set(camX, camY, 0);
		cam.update();	


		
		if (sidePanelState == true && pauseState == true){
			fontHeader.draw(batch, "PAUSED & SAVED", frameX - 0.97f*cam.viewportWidth/2, frameY + 8.3f*cam.viewportHeight/20);
		} else if (sidePanelState == false && pauseState == true){
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
		
	//	focusX -= zF*listOfBodies.get(n).spriteWidth/2;
	//	focusY -= zF*listOfBodies.get(n).spriteWidth/2;
		shapeRenderer.line(new Vector2(focusX - 10, focusY), new Vector2(focusX + 10, focusY));
		shapeRenderer.line(new Vector2(focusX, focusY - 10), new Vector2(focusX, focusY+10));
		
		shapeRenderer.end();
		
		//Draw comet tails

        for (int i = 0; i < listOfBodies.size(); i++) {
            if (listOfBodies.get(i).mass == 1) {
                for (int j = 0; j < listOfBodies.size(); j++) {
                    if (listOfBodies.get(j).mass >= 10000 && listOfBodies.get(i).mostPullingBodyName == listOfBodies.get(j).name) {
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

                while (listOfBodies.get(i).cometTailX.size() >= drawLimit) {
					listOfBodies.get(i).cometTailX.remove(listOfBodies.get(i).cometTailX.size() - 1);
					listOfBodies.get(i).cometTailY.remove(listOfBodies.get(i).cometTailY.size() - 1);
                }

				shapeRenderer.setColor(0, 1, 1, 1);
				shapeRenderer.line(tailNewX * zF, tailNewY * zF, tailOldX * zF, tailOldY * zF);

				for (int j = 1; j < listOfBodies.get(i).cometTailX.size() - 1; j++) {
					if (j == 1) {
						listOfBodies.get(i).cometTailX.set(j + 1, listOfBodies.get(i).cometTailX.get(j + 1) + (tailOldX - tailNewX));
						listOfBodies.get(i).cometTailY.set(j + 1, listOfBodies.get(i).cometTailY.get(j + 1) + (tailOldY - tailNewY));
					} else {
						listOfBodies.get(i).cometTailX.set(j, listOfBodies.get(i).cometTailX.get(j) + (tailOldX - tailNewX));
						listOfBodies.get(i).cometTailY.set(j, listOfBodies.get(i).cometTailY.get(j) + (tailOldY - tailNewY));
					}
				}

                for (int j = 1; j < listOfBodies.get(i).cometTailX.size() - 1; j++) {
                    shapeRenderer.setColor(0, 1, 1, 1);
                    shapeRenderer.line(listOfBodies.get(i).cometTailX.get(j) * zF, listOfBodies.get(i).cometTailY.get(j) * zF, listOfBodies.get(i).cometTailX.get(j + 1) * zF, listOfBodies.get(i).cometTailY.get(j + 1) * zF);
                }
                shapeRenderer.end();
                Gdx.gl.glDisable(GL30.GL_BLEND);
            }
        }

        // Workaround to make side panel items appear above shapeRenderer transparent rectangle

		batch.begin();
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
			fontText.draw(batch, "(SCROLL OR +/-) Zoom", frameX + 2.5f*cam.viewportWidth/12, frameY + 10*cam.viewportHeight/24);
			fontText.draw(batch, "(LEFT CLICK) Create New Planet", frameX + 2.5f*cam.viewportWidth/12, frameY + 9*cam.viewportHeight/24);
			fontText.draw(batch, "(RIGHT CLICK) Create New Star", frameX + 2.5f*cam.viewportWidth/12, frameY + 8*cam.viewportHeight/24);			
			fontText.draw(batch, "(ARROW KEYS) Move Focused Body", frameX + 2.5f*cam.viewportWidth/12, frameY + 7*cam.viewportHeight/24);
			fontText.draw(batch, "(SPACEBAR) Pause & Save", frameX + 2.5f*cam.viewportWidth/12, frameY + 6*cam.viewportHeight/24);
			fontText.draw(batch, "(CTRL) Focus on body closest to mouse", frameX + 2.5f*cam.viewportWidth/12, frameY + 5*cam.viewportHeight/24);
			fontText.draw(batch, "(M) Reset Vel  (P) Purge extraneous planets", frameX + 2.5f*cam.viewportWidth/12, frameY + 4*cam.viewportHeight/24);
			fontText.draw(batch, "(N) Change Focus (B) Change Focus to Next Star", frameX + 2.5f*cam.viewportWidth/12, frameY + 3*cam.viewportHeight/24);
			fontText.draw(batch, "(C) Switch Camera Modes  (WASD) Panning", frameX + 2.5f*cam.viewportWidth/12, frameY + 2*cam.viewportHeight/24);
			fontText.draw(batch, "( , ) Decrease dt  ( . ) Increase dt", frameX + 2.5f*cam.viewportWidth/12, frameY + 1*cam.viewportHeight/24);
			fontText.draw(batch, "(X) Spawn a Star/Planets System", frameX + 2.5f*cam.viewportWidth/12, frameY + 0*cam.viewportHeight/24);
							
			
			
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
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printPos += currentVect;

				currentVect.set(listOfBodies.get(n).velVect);
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printVel += currentVect;

				currentVect.set(listOfBodies.get(n).accVect);
				currentVect.set(Math.round(currentVect.x*100f)/100f, Math.round(currentVect.y*100f)/100f, Math.round(currentVect.z*100f)/100f);
				printAcc += currentVect;
			} 
			fontText.draw(batch, printPos, frameX + 2.5f*cam.viewportWidth/12, frameY - 9*cam.viewportHeight/24);
			fontText.draw(batch, printVel, frameX + 2.5f*cam.viewportWidth/12, frameY - 10*cam.viewportHeight/24);
			fontText.draw(batch, printAcc, frameX + 2.5f*cam.viewportWidth/12, frameY - 11*cam.viewportHeight/24);
			
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX - 0.97f*cam.viewportWidth/2, frameY + 0.93f*cam.viewportHeight/2);	
			fontText.draw(batch, "(press ESC for FULLSCREEN)", frameX - 0.97f*cam.viewportWidth/2, frameY - 0.92f*cam.viewportHeight/2);	
			
		}
		else {
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX  - 1.5f*cam.viewportWidth/12, frameY + 0.93f*cam.viewportHeight/2);	
			fontHeader.draw(batch, "(press ESC for more INFO)", frameX  - 1.1f*cam.viewportWidth/12, frameY - 0.92f*cam.viewportHeight/2);	
		
		}
		
		batch.end();
		
		// Set the viewport to the whole screen.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Draw anywhere on the screen.
		//stage.act(0.01f);
		//stage.draw();
		
		// Restore the stage's viewport.
		//stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		OrbitalPhysics.passList(listOfBodies);
		if(pauseState == false) {
			if(iterationCounter <= numOfIterations){
				timeCounter += deltaTime;
				OrbitalPhysics.iterateSimulation(deltaTime);
				passError();
				place();
				if((iterationCounter % dataDivision) == 0) {
					//fpsLogger.log();
				}
			iterationCounter += 1;
			}	
		}

	}
		
	public static void saveFile() {
        String filePath = RunSimulation.class.getProtectionDomain().getCodeSource().getLocation().getPath();    //The path of the RunSimulation
        
        filePath = filePath.substring(0, filePath.indexOf("/bin")) + "/assets/systems/count.txt";    //Navigate to system file
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
        String filePath = RunSimulation.class.getProtectionDomain().getCodeSource().getLocation().getPath();   //The path of the RunSimulation
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