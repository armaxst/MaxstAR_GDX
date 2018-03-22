package com.maxst.ar.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame {
	SpriteBatch batch;
	Texture img;

	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	public void render () {
		batch.begin();
		img.bind();
		batch.draw(img, 600, 600);
		batch.end();
	}
	
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
