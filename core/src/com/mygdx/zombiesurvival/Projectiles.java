package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Projectiles {
    float movementSpeed;
    float xPos, yPos;
    float width, height;

    TextureRegion projectileTextureRegion;

    public Projectiles(float movementSpeed, float xPos, float yPos, float width, float height, TextureRegion projectileTextureRegion){
        this.movementSpeed = movementSpeed;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.projectileTextureRegion = projectileTextureRegion;
    }

    public void draw(Batch batch){
        batch.draw(projectileTextureRegion,xPos,yPos,width,height);
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(xPos, yPos, width, height);
    }
}
