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
	final static int perturbationCalculationMethod = 0; // 0 = Cowell's Method
	
	//
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	
	// Specifies time used to calculate numerical integration
	// TODO Adaptive step-size control
	final static float deltaTime = (float) 0.1;
	
	// The max number of iterations that the simulation runs
	final static int numOfIterations = 100000000;
	
	final static float drawLimit = 25;
	
	// 0 = Focus on a particular body, 1 = free movement
	static int cameraMode = 0;
	
	// To debug
	double timeCounter = 0;
	int iterationCounter = 0;
	int dataDivision = 1000;
	
	// Cycle through focus
	static int n = 0;
	
	int placedPlanetCounter = 0;
	int placedSunCounter = 0;
	
	// List of currently running bodies in the simulation
	public static ArrayList<OrbitalBody> listOfBodies = new ArrayList<OrbitalBody>();
	public static ArrayList<Float> potOldX = new ArrayList<Float>();
	public static ArrayList<Float> potOldY = new ArrayList<Float>();
	public static ArrayList<Float> potNewX = new ArrayList<Float>();
	public static ArrayList<Float> potNewY = new ArrayList<Float>();

	
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
	
	float adjustX = 0;
	float adjustY = 0;
	
	// zoom factor
	static float zF = 1;
	
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
	int sidePanelWidth = 200;

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

	    
	    
		/* GRAPHICS & INPUTS*/
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		InputProcessor inputProcessor = new Inputs();
		
		// Allows chaining of multiple inputProcessors
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		multiplexer.addProcessor(stage);
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
		
		fTitle.size = 20;
		fTitle.shadowColor = Color.BLACK;
		fTitle.shadowOffsetX = 2;
		fTitle.shadowOffsetY = 2;
		
		fHeader.size = 18;
		fHeader.color = Color.GOLDENROD;
		
		//fHeader.color = new Color(28, 25, 54, 1f);
		
		fSubtitle.size = 16;
		fSubtitle.color = Color.CORAL;
		
		fText.size = 14;
		fText.color = Color.LIGHT_GRAY;
		
		fFocus.size = 16;
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
		if (Gdx.input.isButtonPressed(0) && !newPlanet) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    clickLeftPositionX = (int) (mousePos.x / zF);
			clickLeftPositionY = (int) (mousePos.y / zF);		
			
			newPlanet = true;
		} else if (!Gdx.input.isButtonPressed(0) && newPlanet) {
			
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    unclickLeftPositionX = (int) (mousePos.x / zF);
			unclickLeftPositionY = (int) (mousePos.y / zF);		

			int randomMass = 1 + (int)(Math.random() * 4);
			
			String planetName = LibGDXTools.nameGen();
			
			LibGDXTools.bodyCreate(planetName, randomMass, clickLeftPositionX , clickLeftPositionY, (unclickLeftPositionX - clickLeftPositionX)*placedBodySpeed, (unclickLeftPositionY - clickLeftPositionY)*placedBodySpeed);
			newPlanet = false;
		}
		if (Gdx.input.isButtonPressed(1) && !newSun) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    clickRightPositionX = (int) (mousePos.x / zF);
			clickRightPositionY = (int) (mousePos.y / zF);		
		
			newSun = true;
		} else if (!Gdx.input.isButtonPressed(1) && newSun) {		
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    cam.unproject(mousePos); 
	    
		    unclickRightPositionX = (int) (mousePos.x / zF);
			unclickRightPositionY = (int) (mousePos.y / zF);	
				
			int randomMass = 10000 + (int)(Math.random() * 40000);
			
			String sunName = LibGDXTools.nameGen();
			
			LibGDXTools.bodyCreate(sunName, randomMass, clickRightPositionX , clickRightPositionY, (unclickRightPositionX - clickRightPositionX)*placedBodySpeed, (unclickRightPositionY - clickRightPositionY)*placedBodySpeed);
			newSun = false;

		}
	}
	
	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
        if (listOfBodies.size() == 0){
        	LibGDXTools.bodyInitialize("Star", 10000, 25, 0, 0, 0, 0, 40);
		}
		

		
		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)){
			 if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
				 RunSimulation.n -= RunSimulation.n;
			 }
			 RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y += 3;
	     }
			
	    if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
	        	if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
	        		RunSimulation.n -= RunSimulation.n;
				
	        	}
	        	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.y -= 3;
	    }
	        
	   if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
	      if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
	    	  RunSimulation.n -= RunSimulation.n;
		 }
	        RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x -= 3;
	   }
	        
	   if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
	      
		   if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
	        	RunSimulation.n -= RunSimulation.n;
	      }
	        RunSimulation.listOfBodies.get(RunSimulation.n).velVect.x += 3;
	   }
		
	   if (Gdx.input.isKeyPressed(Input.Keys.M)){
       	if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
       		RunSimulation.n -= RunSimulation.n;
			}
       	RunSimulation.listOfBodies.get(RunSimulation.n).velVect.set(0,0,0);
	   }
	   
        if (Gdx.input.isButtonPressed(Buttons.MIDDLE)){
            zF = 1;
        }
        
        
        if (sidePanelState == true && popupTable.getWidth() != 500f) {
        	sidePanel.setWidth(500f);
        	System.out.println("fjwlefj");
        }
        else if (sidePanelState == false && popupTable.getWidth() != 0f){
        	sidePanel.setWidth(0f);
        	System.out.println("oeiwje");
        }
        
        System.out.println(sidePanel.getWidth());
        
        
        
    
       // else if (sidePanelState == false && popupTable.getWidth() != 0f){
       // 	System.out.println("not 0f");
       // }
        

        
        
        
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		batch.begin();
		
		batch.draw(backgroundTexture, -cam.viewportWidth/2 + camX, -cam.viewportHeight/2 + camY, (int) camX, (int) -camY, (int) cam.viewportWidth, (int) cam.viewportHeight);


		for (int i = 0; i < listOfBodies.size(); i++) {
			OrbitalBody renderBody = listOfBodies.get(i);

            float spriteWidth = renderBody.spriteWidth;

			float spriteX = (float) renderBody.posVect.x * zF - zF*(spriteWidth / 2);
			float spriteY = (float) renderBody.posVect.y * zF - zF*(spriteWidth / 2);
						
			float frameX = 0;
			float frameY = 0;
			
			
			if (pauseState == false){
		        if (n >= listOfBodies.size()) {
		            n -= n;
		        }
			frameX = spriteX - 0.15f*listOfBodies.get(n).velVect.x*zF;
			frameY = spriteY - 0.15f*listOfBodies.get(n).velVect.y*zF;
			}
			else {
				frameX = spriteX;
				frameY = spriteY;
			}
			
			if (i == n){
				fontFocus.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.5f*spriteWidth*zF/10);

			}
			else {
				fontSubtitle.draw(batch, renderBody.name, frameX + 0.7f*spriteWidth*zF/2, frameY + 1.5f*spriteWidth*zF/10);

			}
			
			Texture spriteTexture = renderBody.texture;
			batch.draw(spriteTexture, frameX, frameY, (float) (spriteWidth * zF), (float) (spriteWidth * zF));
		}

		
        float focusX = 0;
		float focusY = 0;

        if (n >= listOfBodies.size()) {
            n -= n;
        }
        /*
        focusX = (float) listOfBodies.get(n).posVect.x * zF - zF*(listOfBodies.get(n).spriteWidth / 2);
        focusY = (float) listOfBodies.get(n).posVect.y * zF - zF*(listOfBodies.get(n).spriteWidth / 2);
		*/
		focusX = (float) listOfBodies.get(n).posVect.x * zF - (listOfBodies.get(n).spriteWidth / 8);
        focusY = (float) listOfBodies.get(n).posVect.y * zF - (listOfBodies.get(n).spriteWidth / 8);
		
		if (sidePanelState == true){
			focusX += sidePanelWidth;
		}		

		float moveX = (camX - focusX) * 2/3;
		float moveY = (camY - focusY) * 2/3;
		
		camX -= moveX;
		camY -= moveY;
	
		camX += 1.1f*cam.viewportHeight/40;

		float frameX = 0;
		float frameY = 0;
				
		if (pauseState == false){
			frameX = camX - 0.1f*listOfBodies.get(n).velVect.x*zF;
			frameY = camY - 0.1f*listOfBodies.get(n).velVect.y*zF;	
		}
		else {
			frameX = camX;
			frameY = camY;
		}
				
		cam.position.set(camX, camY, 0);
		cam.update();	


		
		if (sidePanelState == true && pauseState == true){
			fontHeader.draw(batch, "PAUSED", frameX - 0.97f*cam.viewportWidth/2, frameY + 8.3f*cam.viewportHeight/20);
		} 
		else if (sidePanelState == false && pauseState == true){
			fontHeader.draw(batch, "PAUSED", frameX - 0.9f*cam.viewportWidth/20, frameY + 8.3f*cam.viewportHeight/20);
		}
		
		
		if (savedIndicator > 0){
			savedIndicator--;
			
			if (sidePanelState == true){
				if (pauseState == true){
					fontHeader.draw(batch, "SAVED", frameX - 0.97f*cam.viewportWidth/2, frameY + 7.6f*cam.viewportHeight/20);
				}
				else {
					fontHeader.draw(batch, "SAVED", frameX - 0.97f*cam.viewportWidth/2, frameY + 8.3f*cam.viewportHeight/20);	
				}
			}
			else {			
				if (pauseState == true){
					fontHeader.draw(batch, "SAVED", frameX - 0.9f*cam.viewportWidth/20, frameY + 7.5f*cam.viewportHeight/20);
				}
				else {
					fontHeader.draw(batch, "SAVED", frameX - 1.4f*cam.viewportWidth/20, frameY + 8.3f*cam.viewportHeight/20);
				}
			}
			
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
		for (int i = 0; i < listOfBodies.size(); i++) {
			if (listOfBodies.get(i).oldPosVect.isZero()) {
			} else {
				potOldX.add(listOfBodies.get(i).oldPosVect.x);
				potOldY.add(listOfBodies.get(i).oldPosVect.y);
				potNewX.add(listOfBodies.get(i).posVect.x);
				potNewY.add(listOfBodies.get(i).posVect.y);
				Gdx.gl.glEnable(GL30.GL_BLEND);
				Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
				shapeRenderer.begin(ShapeType.Line);
				if (potOldX.size() < drawLimit) {
					//System.out.println("1");
					for (int x = 0; x < potOldX.size(); x++) {
						shapeRenderer.setColor(1, 0, 0, x / drawLimit);
						//System.outystem.out.println(shapeRenderer.getColor().a);
						shapeRenderer.line(potOldX.get(x)*zF, potOldY.get(x)*zF, potNewX.get(x)*zF, potNewY.get(x)*zF);
						
					}	
				} else {
					potOldX.remove(0);
					potOldY.remove(0);
					potNewX.remove(0);
					potNewY.remove(0);
					if (potOldX.size() < drawLimit) {
						//System.out.println("2");
						for (int x = 0; x < potOldX.size(); x++) {
							shapeRenderer.setColor(1, 0, 0, x / drawLimit);
							shapeRenderer.line(potOldX.get(x)*zF, potOldY.get(x)*zF, potNewX.get(x)*zF, potNewY.get(x)*zF);
						}
					} else {
						//System.out.println("4");
					}
				}
				shapeRenderer.end();	
				Gdx.gl.glDisable(GL30.GL_BLEND);
			}
		}
		// Workaround to make side panel items appear above shapeRenderer transparent rectangle
		batch.begin();
		if (sidePanelState == true) {
			String printNumOfBodies = "# of bodies: " + String.valueOf(listOfBodies.size());
			String printDeltaTime = "dt: " + String.valueOf(deltaTime);
			String printIterationStep = "step: " + String.valueOf(iterationCounter);
			String printFocusPlanet = "FOCUS: " + listOfBodies.get(n).name;			
			String printFocusNumber = "focused body: " + n;
			String printMostAttraction = "Most Grav. Attraction: " + listOfBodies.get(n).mostPullingBodyName;

			fontHeader.draw(batch, "CONTROLS", frameX + 2.5f*cam.viewportWidth/12, frameY + 9*cam.viewportHeight/20);			
			fontSubtitle.draw(batch, LibGDXTools.underlineCalculation("CONTROLS") + "__", frameX + 2.5f*cam.viewportWidth/12,  frameY + 8.9f*cam.viewportHeight/20);
			fontText.draw(batch, "(SCROLL) Zoom", frameX + 2.5f*cam.viewportWidth/12, frameY + 8*cam.viewportHeight/20);
			fontText.draw(batch, "(LEFT CLICK) Create new planet", frameX + 2.5f*cam.viewportWidth/12, frameY + 7*cam.viewportHeight/20);
			fontText.draw(batch, "(RIGHT CLICK) Create new star", frameX + 2.5f*cam.viewportWidth/12, frameY + 6*cam.viewportHeight/20);			
			fontText.draw(batch, "(ARROW KEYS) Move Focused Body", frameX + 2.5f*cam.viewportWidth/12, frameY + 5*cam.viewportHeight/20);
			fontText.draw(batch, "(SPACEBAR) Pause & Save (N) Change Focus", frameX + 2.5f*cam.viewportWidth/12, frameY + 4*cam.viewportHeight/20);
			fontText.draw(batch, "(M) Reset Current Body's Velocity", frameX + 2.5f*cam.viewportWidth/12, frameY + 3*cam.viewportHeight/20);
			fontText.draw(batch, "(N) Change Focus to the Next Body", frameX + 2.5f*cam.viewportWidth/12, frameY + 2*cam.viewportHeight/20);
			
	
			fontHeader.draw(batch, "SIMULATION SETTINGS", frameX + 2.5f*cam.viewportWidth/12, frameY + cam.viewportHeight/20);
			fontSubtitle.draw(batch, "______________________", frameX + 2.5f*cam.viewportWidth/12, frameY + 0.9f*cam.viewportHeight/20);
			fontText.draw(batch, printNumOfBodies, frameX + 2.5f*cam.viewportWidth/12, frameY);
			fontText.draw(batch, printIterationStep, frameX + 2.5f*cam.viewportWidth/12, frameY - cam.viewportHeight/20);
			fontText.draw(batch, printDeltaTime, frameX + 2.5f*cam.viewportWidth/12, frameY - 2*cam.viewportHeight/20);
			fontText.draw(batch, "cam: follow", frameX + 2.5f*cam.viewportWidth/12, frameY - 3*cam.viewportHeight/20);
			
			fontHeader.draw(batch, printFocusPlanet, frameX + 2.5f*cam.viewportWidth/12, frameY - 4*cam.viewportHeight/20);
			fontSubtitle.draw(batch, LibGDXTools.underlineCalculation(printFocusPlanet), frameX + 2.5f*cam.viewportWidth/12, frameY - 4.1f*cam.viewportHeight/20);
			fontText.draw(batch, printMostAttraction, frameX + 2.5f*cam.viewportWidth/12, frameY - 5*cam.viewportHeight/20);
			fontText.draw(batch, "Mass: " + listOfBodies.get(n).mass, frameX + 2.5f*cam.viewportWidth/12, frameY - 6*cam.viewportHeight/20);

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
			fontText.draw(batch, printPos, frameX + 2.5f*cam.viewportWidth/12, frameY - 7*cam.viewportHeight/20);
			fontText.draw(batch, printVel, frameX + 2.5f*cam.viewportWidth/12, frameY - 8*cam.viewportHeight/20);
			fontText.draw(batch, printAcc, frameX + 2.5f*cam.viewportWidth/12, frameY - 9*cam.viewportHeight/20);
			
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX - 0.97f*cam.viewportWidth/2, frameY + 0.93f*cam.viewportHeight/2);	
			fontText.draw(batch, "(press ESC for FULLSCREEN)", frameX - 0.97f*cam.viewportWidth/2, frameY - 0.93f*cam.viewportHeight/2);	
			
		}
		else {
			fontTitle.draw(batch, "ORBITAL SIMULATION", frameX  - 1.3f*cam.viewportWidth/12, frameY + 0.93f*cam.viewportHeight/2);	
			fontText.draw(batch, "(press ESC for more INFO)", frameX  - 1.1f*cam.viewportWidth/12, frameY - 0.93f*cam.viewportHeight/2);	
		
		}
		batch.end();
		
		// Set the viewport to the whole screen.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Draw anywhere on the screen.
		stage.act(0.01f);
		stage.draw();
		
		// Restore the stage's viewport.
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		OrbitalPhysics.passList(listOfBodies);
		if (pauseState == false){
			if (iterationCounter <= numOfIterations){
				timeCounter += deltaTime;
				OrbitalPhysics.iterateSimulation(deltaTime);	
				place();
				if ((iterationCounter % dataDivision) == 0){							
	
				}
			}	
			iterationCounter += 1;
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