package com.thejacksullivan.james;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Scaling;

public class JamesGDXGame implements Screen{

	final GameScreens game;
	
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Texture gameTexture;
    
    private Animation stand;
    private Animation run;
    private Animation jump;
    
    SpriteBatch ButtonBatch = new SpriteBatch(512, 512);
    SpriteBatch bt = new SpriteBatch(512, 512);
    private Texture leftB = new Texture(Gdx.files.internal("data/leftB.png"));
    private Texture rightB = new Texture(Gdx.files.internal("data/rightB.png"));
    private Texture jumpB = new Texture(Gdx.files.internal("data/Jump.png"));
    private Texture backB = new Texture(Gdx.files.internal("data/back.png"));
    
    private Image left = new Image(leftB);
    private Image right = new Image(rightB);
    private Image up = new Image(jumpB);
    private Image back = new Image(backB);
    
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;

    
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {@Override protected Rectangle newObject(){return new Rectangle();}};
    private Pool<Rectangle> deathPool = new Pool<Rectangle>() {@Override protected Rectangle newObject(){ return new Rectangle();}};
    private Pool<Rectangle> doorPool = new Pool<Rectangle>() {@Override protected Rectangle newObject(){return new Rectangle();}};
    
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private Array<Rectangle> deathTiles = new Array<Rectangle>();
    private Array<Rectangle> doorTiles = new Array<Rectangle>();
    
    private static final float GRAVITY = -2.5f;
    private James james;
	private int LEVEL;
	
	
	
	Stage stage = new Stage();
	 
    public JamesGDXGame(final GameScreens gam, int lev){
    	this.game = gam;
    	LEVEL = lev;
		gameTexture = new Texture("data/james.png");
		TextureRegion[] regions = TextureRegion.split(gameTexture, 16, 16)[0];
		stand = new Animation(0, regions[0]);
		run = new Animation(1, regions[1], regions[2], regions[3]);
		jump = new Animation(0, regions[1]);
		run.setPlayMode(Animation.LOOP_PINGPONG);
		
		james.WIDTH = 1/16f * regions[0].getRegionWidth();
		james.HEIGHT = 1/16f * regions[0].getRegionHeight();
		
		map = new TmxMapLoader().load("data/level" + LEVEL + ".tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 28);
		camera.update();
		bt.begin();
		james = new James();
		james.position.set(1, 10);
		GfxHelper.resize(left);
		GfxHelper.resize(right);
		GfxHelper.resize(up);
		GfxHelper.resize(back);
    }
    

	@Override
	public void render(float delta) {		
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();                     
        updateJames(deltaTime);
        
        camera.position.x = james.position.x;
        camera.update();
        renderer.setView(camera);
        renderer.render();

        renderJames(deltaTime);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	private void updateJames(float deltaTime){
		if(deltaTime == 0) return;
		james.stateTime += deltaTime;

	
		
		james.velocity.add(0, GRAVITY);
		
		if(Math.abs(james.velocity.x) > james.MAX_VELO)
			james.velocity.x = Math.signum(james.velocity.x) * james.MAX_VELO;	
		
		if(Math.abs(james.velocity.x) < 1 ) {
			james.velocity.x = 0;		
			if(james.grounded)
				james.state = james.state.STANDING;
		}
		
		james.velocity.scl(deltaTime);
		
		Rectangle jamesRect = rectPool.obtain();
        jamesRect.set(james.position.x, james.position.y, james.WIDTH, james.HEIGHT);
        int startX, startY, endX, endY;
        
        if(james.velocity.x > 0) 
             startX = endX = (int)(james.position.x + james.WIDTH + james.velocity.x);
         else 
             startX = endX = (int)(james.position.x + james.velocity.x);
        
        startY = (int)(james.position.y);
        endY = (int)(james.position.y + james.HEIGHT);
        getTiles(startX, startY, endX, endY, tiles);
        getDeathTiles(startX, startY, endX, endY, deathTiles);
        getDoorTiles(startX, startY, endX, endY, doorTiles);
        jamesRect.x += james.velocity.x;
        
        for(Rectangle tile: tiles) {
                if(jamesRect.overlaps(tile)) {
                        james.velocity.x = 0;
                        break;
                }
        }
        
        for(Rectangle tile1: deathTiles){
        	if(jamesRect.overlaps(tile1)){
        		james.position.x = 1;
        		james.position.y = 8;
        		break;
        	}
        }

        for(Rectangle tile1: doorTiles){
        	if(jamesRect.overlaps(tile1)){
        		game.setScreen(new MainMenuScreen(game));
        		break;
        	}
        }
        jamesRect.x = james.position.x;
        
        
        if(james.velocity.y > 0) 
            startY = endY = (int)(james.position.y + james.HEIGHT + james.velocity.y);
        else 
           startY = endY = (int)(james.position.y + james.velocity.y);
    
        startX = (int)(james.position.x);
        endX = (int)(james.position.x + james.WIDTH);
        getTiles(startX, startY, endX, endY, tiles);
        jamesRect.y += james.velocity.y;
        
        for(Rectangle tile: tiles) {
            if(jamesRect.overlaps(tile)) {        	
                    if(james.velocity.y > 0) 
                            james.position.y = tile.y - james.HEIGHT;
                     else {
                            james.position.y = tile.y + tile.height;
                            james.grounded = true;
                    }
                    
                    james.velocity.y = 0;
                    break;
            }
        }
        
        rectPool.free(jamesRect);
        
        james.position.add(james.velocity);
        james.velocity.scl(1/deltaTime);
        
        james.velocity.x *= james.DAMPING;
        
		left.setX(1);
		left.setY(2);
		right.setX(left.getWidth() + right.getWidth());
		right.setY(2);
		up.setX(Gdx.graphics.getWidth() - up.getWidth());
		up.setY(2);
		back.setX(Gdx.graphics.getWidth() - back.getWidth());
		back.setY(Gdx.graphics.getHeight() - back.getHeight());
		

		
		stage.addActor(left);
		stage.addActor(right);
		stage.addActor(up);
		stage.addActor(back);
		
		left.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		    {
		    	leftPressed = true;
		        return false;        
		    }
		});
		
		right.addListener(new ClickListener() {
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		    	rightPressed = true;	    	
		        return false;
		    }
		});
		
		up.addListener(new ClickListener(){
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		    	upPressed = true;	    	
		        return false;
		    }
		});
		
		back.addListener(new ClickListener(){
		    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		    	game.setScreen(new MainMenuScreen(game));
		    	return false;
		    }
		});
			
		
		
		Gdx.input.setInputProcessor(stage);
	
		if(rightPressed){
			if(Gdx.input.isTouched()){
				james.velocity.x = james.MAX_VELO;
				if(james.grounded) 
					james.state = james.state.RUNNING;
				
				james.facesRight = true;
			}else{
				rightPressed = false;
			}	
		}
		
		if(leftPressed){
			if(Gdx.input.isTouched()){
				james.velocity.x = -james.MAX_VELO;
				if(james.grounded) 
					james.state = james.state.RUNNING;
				
				james.facesRight = false;
			}else
				leftPressed = false;	
		}
		
		if(upPressed){
			if(Gdx.input.isTouched() && james.grounded){
				james.velocity.y += James.JUMP_VELO;
				james.state = James.State.JUMPING;
				james.grounded = false;
			}else
				upPressed = false;
		}
	
		if(james.velocity.y == 0)
			james.grounded = true;
		
	}
	
	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Tile Layer 1");
        rectPool.freeAll(tiles);
        tiles.clear();
       
        for(int y = startY; y <= endY; y++) {
            for(int x = startX; x <= endX; x++) {
                    Cell cell = layer.getCell(x, y);
                    if(cell != null) {
                            Rectangle rect = rectPool.obtain();
                            rect.set(x, y, 1, 1);
                            tiles.add(rect);
                     
                    }
            }
        }  
	}
	
	private void getDeathTiles(int startX, int startY, int endX, int endY, Array<Rectangle> deathTiles){
	        TiledMapTileLayer layer2 = (TiledMapTileLayer) map.getLayers().get("DeathLayer");
	        deathPool.freeAll(deathTiles);
	        deathTiles.clear();
	        
	        for(int y = startY; y <= endY; y++) {
	            for(int x = startX; x <= endX; x++) {
	                    Cell cell = layer2.getCell(x, y);
	                    if(cell != null) {
	                        Rectangle rect2 = deathPool.obtain();
	                        rect2.set(x, y, 1, 1);
	                        deathTiles.add(rect2);
	                     
	                    }
	            }
	        } 
	}
	
	private void getDoorTiles(int startX, int startY, int endX, int endY, Array<Rectangle> doorTiles){
        TiledMapTileLayer layer3 = (TiledMapTileLayer) map.getLayers().get("Door");
        doorPool.freeAll(doorTiles);
        doorTiles.clear();
        
        for(int y = startY; y <= endY; y++) {
            for(int x = startX; x <= endX; x++) {
                    Cell cell = layer3.getCell(x, y);
                    if(cell != null) {
                        Rectangle rect3 = doorPool.obtain();
                        rect3.set(x, y, 1, 1);
                        doorTiles.add(rect3);
                     
                    }
            }
        } 
	}
	
	
	private void renderJames(float deltaTime){
		TextureRegion frame = null;		
		switch(james.state){
			case STANDING:
				frame = stand.getKeyFrame(james.stateTime); break;
			case JUMPING:
				frame = jump.getKeyFrame(james.stateTime); break;
			case RUNNING:
				frame = run.getKeyFrame(james.stateTime); break;
			default:
				break;
		}
	      
		SpriteBatch batch = renderer.getSpriteBatch();
		batch.begin();
				
		if(james.facesRight) 
			batch.draw(frame, james.position.x, james.position.y, james.WIDTH, james.HEIGHT);
		else 
			batch.draw(frame, james.position.x + james.WIDTH, james.position.y, -james.WIDTH, james.HEIGHT);
		batch.end();


	}
	
	@Override
	public void resize(int width, int height) {

	}

	
	@Override
	public void dispose() {
		bt.dispose();
	}

	@Override
	public void pause(){}
	@Override
	public void resume(){}
	@Override
	public void hide(){}
	@Override
	public void show(){}
}
