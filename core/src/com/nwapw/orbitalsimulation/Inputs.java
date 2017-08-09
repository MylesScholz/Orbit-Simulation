package com.nwapw.orbitalsimulation;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class Inputs implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.C) {
			boolean cameraPan = RunSimulation.cameraPan;
			
			if (cameraPan == false){
				cameraPan = true;
				RunSimulation.panX = (float) RunSimulation.listOfBodies.get(RunSimulation.n).posVect.x * RunSimulation.zF - (RunSimulation.listOfBodies.get(RunSimulation.n).spriteWidth / 8)*RunSimulation.zF;
				RunSimulation.panY = (float) RunSimulation.listOfBodies.get(RunSimulation.n).posVect.y * RunSimulation.zF - (RunSimulation.listOfBodies.get(RunSimulation.n).spriteWidth / 8)*RunSimulation.zF;
				
			}
			else {
				cameraPan = false;
			}
			RunSimulation.cameraPan = cameraPan;
			
			
		}
		
		
		
		if (keycode == Input.Keys.CONTROL_LEFT) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
			RunSimulation.cam.unproject(mousePos); 
		    mousePos.scl(1/RunSimulation.zF);
		    
		    float closestDist = RunSimulation.listOfBodies.get(0).posVect.dst(mousePos);
		    int closestN = 0;
		   
		    for (int i = 1; i < RunSimulation.listOfBodies.size()-1; i++){
		    	if (RunSimulation.listOfBodies.get(i).posVect.dst(mousePos) < closestDist ) {
		    		//System.out.println(RunSimulation.listOfBodies.get(i).name);
		    		//System.out.println((RunSimulation.listOfBodies.get(i).posVect.dst(mousePos)));
		    		closestN = i;
		    		
		    	}		    	
	
		    }
		    RunSimulation.n = closestN;
		    
		    
		}
		
		
		if(keycode == Input.Keys.ESCAPE){
        	if (RunSimulation.sidePanelState == true){
        		RunSimulation.sidePanelState = false;
        	} else {
        		RunSimulation.sidePanelState = true;
        	}
	    }   
		if(keycode == Input.Keys.Q){
			
        	if (RunSimulation.collisionsOn == true){
        		RunSimulation.collisionsOn = false;
        	}
        	else {
        		RunSimulation.collisionsOn = true;
        	}
        	
	    }   
		if (keycode == Input.Keys.N){
			RunSimulation.n++;
			if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
				RunSimulation.n -= RunSimulation.n;
			}
			RunSimulation.zF = LibGDXTools.calculateDefaultZoom(RunSimulation.listOfBodies.get(RunSimulation.n).spriteWidth);
		}
		if (keycode == Input.Keys.B){
			
			// focus on next star
			boolean next = true;
			int cycleCounter = 0;
			ArrayList<OrbitalBody> listOfBodies = RunSimulation.listOfBodies;
			
			while (next) {
				RunSimulation.n++;
				cycleCounter++;
				if (RunSimulation.n >= RunSimulation.listOfBodies.size()) {
					RunSimulation.n -= RunSimulation.n;
				}
				if (listOfBodies.get(RunSimulation.n).mass > 10000) {
					next = false;
				}

				if (cycleCounter > listOfBodies.size()){
					break;
				}
			}
			
	    }
		
		if (keycode == Input.Keys.BACKSPACE) {
			RunSimulation.listOfBodies.remove(RunSimulation.n);
	    }
	
		if(keycode == Input.Keys.SPACE) {
		    RunSimulation.saveFile();
			if (RunSimulation.pauseState == false){
				RunSimulation.pauseState = true;
			}
			else {
				RunSimulation.pauseState = false;
			}
	    }


		if (keycode == Input.Keys.PLUS || keycode == Input.Keys.EQUALS) {
			RunSimulation.zF += 0.1f;
			if (RunSimulation.zF <= 0.001){
				RunSimulation.zF = 0.001f;
			} else if (RunSimulation.zF >= 100){
				RunSimulation.zF = 100f;
			}
			//System.out.println(RunSimulation.zF);
			return false;
		}

		if (keycode == Input.Keys.MINUS) {
			RunSimulation.zF -= 0.1f;
			if (RunSimulation.zF <= 0.001){
				RunSimulation.zF = 0.001f;
			} else if (RunSimulation.zF >= 100){
				RunSimulation.zF = 100f;
			}
			//System.out.println(RunSimulation.zF);
			return false;
		}
		
		if (keycode == Input.Keys.PERIOD) {
			RunSimulation.deltaTime += RunSimulation.deltaTime / 10;
		}
		
		if (keycode == Input.Keys.COMMA) {
			RunSimulation.deltaTime -= RunSimulation.deltaTime / 10;
		}

		if(keycode == Input.Keys.L){
			if (RunSimulation.zoomLines == false){
				RunSimulation.zoomLines = true;
			}
			else {
				RunSimulation.zoomLines = false;
			}
	    }
		if(keycode == Input.Keys.P){
			if (RunSimulation.purgeState == false){
				RunSimulation.purgeState = true;
			}
			else {
				RunSimulation.purgeState = false;
			}
	    }
		/*
		if(keycode == Input.Keys.X){
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		    RunSimulation.cam.unproject(mousePos); 
		    
		    int spawnPosX = (int) (mousePos.x / RunSimulation.zF);
			int spawnPosY = (int) (mousePos.y / RunSimulation.zF);		
			
			Random random = new Random();
			float randScalar = 1 + (random.nextFloat() - 0.5f)/4;
			
			if (random.nextFloat() < 0.5){
				randScalar *= -1;
			}
			
			LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 43779 + randScalar*1000, spawnPosX, spawnPosY ,0,0);
			
			
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 3, spawnPosX + 1709*randScalar, spawnPosY - 123*randScalar, 2.2944372f*randScalar, -34.84709f*randScalar);	
			}
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, spawnPosX + 842*randScalar, spawnPosY - 172*randScalar, 36f*randScalar, 60f*randScalar);	
							}				
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, spawnPosX - 1127*randScalar, spawnPosY - 101*randScalar, 6.7067494f*randScalar, 55.26426f*randScalar);
			}			
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 1, spawnPosX +480*randScalar, spawnPosY - 51*randScalar, 18.085638f*randScalar, -92.809425f*randScalar);
			}			
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 3, spawnPosX +589*randScalar, spawnPosY - 71*randScalar, -10.085638f*randScalar, 83.809425f*randScalar);
			}
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 4, spawnPosX + 4*randScalar, spawnPosY -127*randScalar, 98.83999f*randScalar, 25.148289f*randScalar);
			}
			if (random.nextFloat() < 0.5){
				LibGDXTools.bodyCreate(LibGDXTools.nameGen(), 2, spawnPosX - 1298*randScalar, spawnPosY -417*randScalar, -25f*randScalar, 42.148289f*randScalar);
			}						
	    }		
		 */
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
	
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
		
		float zf = 0;
		//System.out.println(RunSimulation.zF);		
		
		float orderOfMag = RunSimulation.zF;
		float orderOfMagCounter = 0;
		
		while (orderOfMag < 1){
			orderOfMag *= 10;
			orderOfMagCounter += 1;			
		}
		orderOfMagCounter -= orderOfMag/10;

		zf += 0.2f / (Math.pow(10, orderOfMagCounter));
				
		
		if (amount == 1){
			zf *= -1;
		}
		
		RunSimulation.zF += zf;

		
		if (RunSimulation.zF <= 0){
			RunSimulation.zF = 0.000001f;
		}
		else if (RunSimulation.zF >= 15){
			RunSimulation.zF = 15f;
		}
		
		
		//System.out.println(RunSimulation.zF);
		return false;
	}

}
