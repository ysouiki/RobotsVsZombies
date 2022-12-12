package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Character {
    float movementSpeed;
    int health;

    float xPos, yPos;
    float width, height;

    Array<TextureRegion> playerTextures;
    TextureRegion projectileTexture;

    float projectileWidth, projectileHeight;
    float projectileSpeed;
    float shotInterval;
    float shotTimer = 0;

    Rectangle hitBox;
    Animation newAnimation;
    int frameCount;
    float cycleTime;

    public Character(float movementSpeed, int health, float xCentre, float yCentre, float width, float height, Array<TextureRegion> playerTextures,
                     TextureRegion projectileTexture, float projectileWidth, float projectileHeight, float projectileSpeed, float shotInterval,
                     int frameCount, float cycleTime) {

        //icon params
        this.yPos = yCentre - height/2;
        this.xPos = xCentre - width/2;
        this.movementSpeed = movementSpeed;
        this.health = health;
        this.width = width;
        this.height = height;
        this.playerTextures = playerTextures;

        hitBox = new Rectangle(xPos, yPos, width, height);

        //projectile params
        this.projectileWidth = projectileWidth;
        this.projectileHeight = projectileHeight;
        this.projectileSpeed = projectileSpeed;
        this.projectileTexture = projectileTexture;

        this.shotInterval = shotInterval;
        this.frameCount = frameCount;
        this.cycleTime = cycleTime;

        newAnimation = new Animation(playerTextures, frameCount, cycleTime);
    }

    public void draw(Batch batch){
        batch.draw(newAnimation.getFrame(), xPos, yPos, width, height);
    }

    public void update(float deltaTime){
        newAnimation.update(deltaTime);
        shotTimer += deltaTime;
        hitBox.set(xPos+100, yPos, width, height-50);
    }

    public boolean canShoot(){
        return (shotTimer - shotInterval >= 0);
    }

    public boolean hitAndCheckDead(){
      health--;
      if(health <= 0){
          return true;
      }
      return false;
    }

    public boolean checkCollision(Rectangle otherRectangle){
        return hitBox.overlaps(otherRectangle);
    }

    public void moveIcon(float xChange, float yChange){
        xPos = xPos + xChange;
        yPos = yPos + yChange;
    }

    public float getX(){
        return xPos;
    }

    public float getY(){
        return yPos;
    }

    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }


    public abstract Projectiles[] shootProjectiles();

    public void setShotInterval(float newInterval){
        this.shotInterval = newInterval;
    }

    public void setProjectileTexture(TextureRegion projTexture){
        this.projectileTexture = projTexture;
    }
}
