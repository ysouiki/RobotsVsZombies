package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class ZombieCharacter extends Character{
    public ZombieCharacter(float movementSpeed, int health, float xCentre, float yCentre, float width, float height, Array<TextureRegion> playerTextures, TextureRegion projectileTexture, float projectileWidth, float projectileHeight, float projectileSpeed, float shotInterval, int frameCount, float cycleTime) {
        super(movementSpeed, health, xCentre, yCentre, width, height, playerTextures, projectileTexture, projectileWidth, projectileHeight, projectileSpeed, shotInterval, frameCount, cycleTime);
    }

    @Override
    public Projectiles[] shootProjectiles() {
        return new Projectiles[0];
    }
}
