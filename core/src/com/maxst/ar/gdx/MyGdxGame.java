package com.maxst.ar.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame {
	private static final String MODEL_NAME = "3d/jet.g3db";

	protected Environment  	 	environment;
	protected PerspectiveCamera camera;

	AssetManager assets;
	SpriteBatch  batch;
	ModelBatch   modelBatch;

	Texture img;
	Model   model;
	Array<ModelInstance> instances = new Array<ModelInstance>();

	boolean loading;

	public void create () {
		//setup light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		//setup camera
		camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		camera.lookAt(0,0,10);
		camera.near = 0.01f;
		camera.far = 5000.0f;
		camera.update();

		batch = new SpriteBatch();
		modelBatch = new ModelBatch();
		assets = new AssetManager();
		ModelBuilder modelBuilder = new ModelBuilder();
		//create some test assets
		img = new Texture("badlogic.jpg");

		model = modelBuilder.createBox(0.15f, 0.15f, 0.05f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		ModelInstance cube = new ModelInstance(model);
		cube.transform.setTranslation(0.15f,0,1);
		instances.add(cube);

		assets.load(MODEL_NAME, Model.class);
		loading = true;
	}

	public void render () {
		if (loading && assets.update()) {
			doneLoading();
		}

		modelBatch.begin(camera);
		modelBatch.render(instances,environment);
		modelBatch.end();

		batch.begin();
		img.bind();
		batch.draw(img, Gdx.graphics.getWidth() - img.getWidth(), 0);
		batch.end();
	}

	public void renderDefault(float[] projection, float [] modelMatrix) {
		camera.combined.set(new Matrix4(projection));

		ModelInstance model = instances.first();
		model.transform.set(new Matrix4(modelMatrix));
		model.transform.translate(0.15f, 0, 1.0f);
	}

	//FIXME
	public void showTrackingResult(float[] _projectionMatrix, float[] _poseMatrix){
//		Matrix4 maxSTMVP = new Matrix4(_projectionMatrix);
//		maxSTMVP.mul(new Matrix4(_poseMatrix));
//		maxSTMVP.inv();
//
//		float [] mvp = maxSTMVP.getValues();
//
//		camera.position.set(mvp[12], mvp[13], mvp[14]);
//		camera.up.set(mvp[4], mvp[5], mvp[6]);
//		camera.lookAt(mvp[8], mvp[9], mvp[10]);
//		camera.update();

		camera.combined.set(new Matrix4(_projectionMatrix));

		ModelInstance model = instances.first();
		model.transform.set(new Matrix4(_poseMatrix));
		//model.transform.translate(mvp[8], mvp[9], mvp[10]);
	}

	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update(true);
	}

	public void dispose () {
		batch.dispose();
		img.dispose();

		modelBatch.dispose();
		model.dispose();

		instances.clear();
		assets.dispose();
	}

	private void doneLoading() {
		ModelInstance shipInstance = new ModelInstance(assets.get(MODEL_NAME, Model.class));
		instances.add(shipInstance);
		shipInstance.transform.scale(0.5f,0.5f,0.5f);
		shipInstance.transform.setTranslation(5,0,-5);
		loading = false;
	}
}
