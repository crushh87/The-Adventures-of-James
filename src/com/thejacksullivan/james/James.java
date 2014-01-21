package com.thejacksullivan.james;

import com.badlogic.gdx.math.Vector2;


public class James {

	public enum State {
		STANDING, RUNNING, JUMPING, DEAD
	}
	
	static float WIDTH;
	static float HEIGHT;
	static float MAX_VELO = 10f;
	static float JUMP_VELO = 40;;
	static float DAMPING = 0.87f; 
	
	final Vector2 position = new Vector2();
	final Vector2 velocity = new Vector2();
	
	public State state = State.RUNNING;
	float stateTime = 0;
	boolean facesRight = true;
	boolean grounded = false;
	boolean isJumping = false;

}
