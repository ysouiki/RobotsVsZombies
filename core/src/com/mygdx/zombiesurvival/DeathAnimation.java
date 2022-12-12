package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class DeathAnimation{
    private Array<TextureRegion> frames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;
    private Rectangle boundingBox;
    private boolean isFinished = false;


    DeathAnimation(Array<TextureRegion> frames, Rectangle boundingBox, float animationTime, int frameCount){

        this.frames = frames;
        this.frameCount = frameCount;
        maxFrameTime = animationTime / frameCount;
        frame = 0;

        this.boundingBox = boundingBox;

    }

    public void update(float deltaTime){
        currentFrameTime += deltaTime;
        if(currentFrameTime > maxFrameTime){
            frame ++;
            currentFrameTime = 0;
        }

        if(frame >= frameCount){
            isFinished = true;
        }

    }

    public void draw(SpriteBatch batch){
        batch.draw(frames.get(frame), boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

    }

    public boolean isAnimationFinished(){
        return isFinished;
    }

}
