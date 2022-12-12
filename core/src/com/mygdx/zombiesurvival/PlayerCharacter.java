package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerCharacter extends Character{


    public PlayerCharacter(float movementSpeed, int health, float xCentre, float yCentre, float width, float height, Array<TextureRegion> playerTextures,
                           TextureRegion projectileTexture, float projectileWidth,
                           float projectileHeight, float projectileSpeed, float shotInterval, int frameCount, float cycleTime) {

        super(movementSpeed, health, xCentre, yCentre, width, height, playerTextures,
                projectileTexture, projectileWidth, projectileHeight, projectileSpeed, shotInterval, frameCount, cycleTime);
    }

    @Override
    public Projectiles[] shootProjectiles() {
        Projectiles[] projectiles = new Projectiles[1];
        projectiles[0] = new Projectiles(projectileSpeed, xPos+width*0.8f, yPos+height/2.3f,projectileWidth,projectileHeight,projectileTexture);

        shotTimer = 0;

        return projectiles;
    }
}
