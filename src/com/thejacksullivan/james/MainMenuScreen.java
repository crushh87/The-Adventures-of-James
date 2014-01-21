package com.thejacksullivan.james;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen implements Screen{


	final GameScreens game;
	OrthographicCamera camera;
	private Texture tex = new Texture(Gdx.files.internal("data/title.png"));
	boolean firstRun = true;
	boolean lev = false;
    Skin uiSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
    Stage stage = new Stage();
    private TextButton lv1;
    private SelectBox list;
    String[] levels = {"Level 1", "Level 2", "Level 3"};
	
    
	public MainMenuScreen(final GameScreens gam){
		game = gam;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 512, 512);
	
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);		
		game.batch.begin();
		game.font.setScale(2f);
		
		if(firstRun){	
			game.batch.draw(tex, 0, 0);
			game.font.draw(game.batch, "Tap anywhere to begin!", 110, 260);
			
		}else{
			game.font.draw(game.batch, "Pick a level to play!", 140, 440);
			if(lev == false){				
				list = new SelectBox(levels, uiSkin);
				list.setPosition(Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() / 2);
				stage.addActor(list);
				list.setScale(10.5f);
				lev = true;
			}
			update();
		}
		
		game.batch.end();
		
		if (Gdx.input.isTouched()) {
			if(firstRun)
				firstRun = false;
		}
			
			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
			stage.draw();
	
	}

	public void update(){	
						
			lv1 = new TextButton("Play!", uiSkin);
			lv1.setPosition(Gdx.graphics.getWidth() / 2 , Gdx.graphics.getHeight() / 2);
			stage.addActor(lv1);
			lv1.setScale(2.5f);
			lv1.addListener(new ClickListener(){
		       @Override
	            public void clicked(InputEvent event, float x, float y) {
		    	   game.setScreen(new JamesGDXGame(game, list.getSelectionIndex()));
		       }
			});
			
			
			Gdx.input.setInputProcessor(stage);
	}
	
	
	
	@Override
	public void resize(int width, int height){
			
	}
	@Override
	public void dispose(){
		firstRun = false;
		game.batch.dispose();
		game.font.dispose();
	}
	@Override
	public void show(){

	}
	@Override
	public void hide() {}
	@Override
	public void pause() {}
	@Override
	public void resume(){}

}




