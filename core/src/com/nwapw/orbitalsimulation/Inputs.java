package com.nwapw.orbitalsimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Inputs implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ESCAPE) {
        	if (RunSimulation.sidePanelState == true){
        		RunSimulation.sidePanelState = false;
        	} else {
        		RunSimulation.sidePanelState = true;
        	}
	    }   
	
		if (keycode == Input.Keys.N) {
			RunSimulation.n++;
			//RunSimulation.zF = LibGDXTools.calculateDefaultZoom(RunSimulation.listOfBodies.get(RunSimulation.n).spriteWidth);
		}
		
		if (keycode == Input.Keys.B) {
			RunSimulation.listOfBodies.get(RunSimulation.n).posVect.set(100, 100, 100);
			RunSimulation.listOfBodies.get(RunSimulation.n).velVect.set(0, 0, 0);
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
		if (amount == 1){
			 RunSimulation.zF -= .01f;
		} else {
			 RunSimulation.zF += .01f;
		}
		
		if (RunSimulation.zF <= 0.01){
			RunSimulation.zF = 0.01f;
		} else if (RunSimulation.zF >= 1000){
			RunSimulation.zF = 1000f;
		}
		//System.out.println(RunSimulation.zF);
		return false;
	}

}
