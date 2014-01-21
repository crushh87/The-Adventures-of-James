package com.thejacksullivan.james;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

public class GfxHelper {
    protected static final int WIDTH = 512; // Change to your testing device width
    protected static final int HEIGHT = 512; // Change to your testing device height
 
    public static Image resize(Image img) {
        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight();
 
        float changeX = x / WIDTH;
        float changeY = y / HEIGHT;
 
        img.setX(img.getX() * changeX);
        img.setY(img.getY() * changeY);
        img.setWidth(img.getWidth() * changeX);
        img.setHeight(img.getHeight() * changeY);
 
        return img;
    }
    

}