package com.mygdx.zombiesurvival;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;




public class Animation {
    private Array<TextureRegion> frames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;

    public Animation(Array<TextureRegion> frames, int frameCount, float cycleTime){

        this.frames = frames;
        this.frameCount = frameCount;
        maxFrameTime = cycleTime / frameCount;
        frame = 0;

    }

    public void update(float deltaTime){
        currentFrameTime += deltaTime;
        if(currentFrameTime > maxFrameTime){
            frame ++;
            currentFrameTime = 0;
        }
        if(frame >= frameCount){
            frame = 0;
        }
    }

    public TextureRegion getFrame(){
        return frames.get(frame);
    }
}
