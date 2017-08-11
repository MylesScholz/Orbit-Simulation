package com.nwapw.orbitalsimulation;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class Inputs implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.V) {
			if (RunSimulation.predictions == true){
        		RunSimulation.predictions = false;
        	} else {
        		RunSimulation.predictions = true;
        	}
		}
		
		if (keycode == Input.Keys.U) {
			if (RunSimulation.collisionsOn == true){
        		RunSimulation.collisionsOn = false;
        	} else {
        		RunSimulation.collisionsOn = true;
        	}
		}
		
		if (keycode == Input.Keys.Q) {
			if (OrbitalPhysics.integrationMethod == 0){
        		OrbitalPhysics.integrationMethod = 1;
        	} else {
        		OrbitalPhysics.integrationMethod = 0;
        	}
		}
		
		if (keycode == Input.Keys.X) {
			if (RunSimulation.slingShot == 1){
        		RunSimulation.slingShot = -1;
        	} else {
        		RunSimulation.slingShot = 1;
        	}
		}
		
		if (keycode == Input.Keys.I) {
			RunSimulation.deltaTime = RunSimulation.originalDeltaTime;
			RunSimulation.deltaPredictionTime = RunSimulation.originalDeltaPredictionTime;
		}
		
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
		    //closestDist +=  RunSimulation.listOfBodies.get(0).velVect.len() * (-100f * RunSimulation.zF * RunSimulation.deltaTime * 20);
		   
		    
		    int closestN = 0;
		   
		    for (int i = 1; i < RunSimulation.listOfBodies.size(); i++){
		    	
		    	float checkClosestDist = RunSimulation.listOfBodies.get(i).posVect.dst(mousePos);
		    	//checkClosestDist +=  RunSimulation.listOfBodies.get(i).velVect.len() * (-100f * RunSimulation.zF * RunSimulation.deltaTime * 20);
		    	
		    	if (checkClosestDist < closestDist ) {
		    		closestN = i;
		    		closestDist = checkClosestDist;
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
			//RunSimulation.zF = LibGDXTools.calculateDefaultZoom(RunSimulation.listOfBodies.get(RunSimulation.n).spriteWidth);
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
			if (RunSimulation.zF <= 0.000000000000009){
				RunSimulation.zF = 0.0000000000000009f;
			} else if (RunSimulation.zF >= 15){
				RunSimulation.zF = 15f;
			}
			//System.out.println(RunSimulation.zF);
			return false;
		}

		if (keycode == Input.Keys.MINUS) {
			RunSimulation.zF -= 0.1f;
			if (RunSimulation.zF <= 0.000000000000009){
				RunSimulation.zF = 0.0000000000000009f;
			} else if (RunSimulation.zF >= 15){
				RunSimulation.zF = 15f;
			}
			//System.out.println(RunSimulation.zF);
			return false;
		}
		
		if (keycode == Input.Keys.PERIOD) {
			RunSimulation.deltaTime += RunSimulation.deltaTime / 10;
			RunSimulation.deltaPredictionTime += RunSimulation.deltaPredictionTime / 10;
		}
		
		if (keycode == Input.Keys.COMMA) {
			RunSimulation.deltaTime -= RunSimulation.deltaTime / 10;
			RunSimulation.deltaPredictionTime += RunSimulation.deltaPredictionTime / 10;
		}

		if(keycode == Input.Keys.Z){
			if (RunSimulation.zoomLines == false){
				RunSimulation.zoomLines = true;
			}
			else {
				RunSimulation.zoomLines = false;
			}
	    }
		if(keycode == Input.Keys.Y){
			if (RunSimulation.purgeState == false){
				RunSimulation.purgeState = true;
			}
			else {
				RunSimulation.purgeState = false;
			}
	    }
		if(keycode == Input.Keys.J){
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
			
			if (RunSimulation.coolBackground == true){
				RunSimulation.coolBackground = false;
				int j=1;
				if (Math.random() < 0.5){
					j = 10;
				}
				else {
					j = 11;
				}
		        String backgroundFileName = "backgrounds/" + j + ".jpg";
		        RunSimulation.backgroundTexture = new Texture(backgroundFileName);
		        RunSimulation.backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			}
			else {
				RunSimulation.coolBackground = true;
				int j = 1 + (int)(Math.random() * 8); 
		        String backgroundFileName = "backgrounds/" + j + ".jpg";
		        RunSimulation.backgroundTexture = new Texture(backgroundFileName);
		        RunSimulation.backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			}
			}
	    }
		
		if(keycode == Input.Keys.Y) {
			if(Gdx.input.isKeyPressed(Input.Keys.T)) {
				if(Gdx.input.isKeyPressed(Input.Keys.R)) {
					if(Gdx.input.isKeyPressed(Input.Keys.A)) {
						if(Gdx.input.isKeyPressed(Input.Keys.P)) {
							if (RunSimulation.party == false){
								RunSimulation.party = true;
							}
							else {
								RunSimulation.party = false;
							}
						}
					}
				}
			}
		}
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
		
		if (RunSimulation.zF <= 0.000000000000009f){
			RunSimulation.zF = 0.000000000000009f;
		}
		else if (RunSimulation.zF >= 15f){
			RunSimulation.zF = 15f;
		}
		
		//System.out.println(RunSimulation.zF);
		return false;
	}

}
