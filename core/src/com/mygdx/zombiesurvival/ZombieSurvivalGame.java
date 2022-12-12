package com.mygdx.zombiesurvival;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class ZombieSurvivalGame extends Game {

	GameScreen screen;

	public static Random random = new Random();

	@Override
	public void create(){
		screen = new GameScreen();
		setScreen(screen);
	}

	@Override
	public void render(){
		super.render();

	}

	@Override
	public void dispose(){
		screen.dispose();
	}

}
