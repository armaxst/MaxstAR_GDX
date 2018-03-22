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
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();

		batch = new SpriteBatch();
		modelBatch = new ModelBatch();
		assets = new AssetManager();
		ModelBuilder modelBuilder = new ModelBuilder();
		//create some test assets
		img = new Texture("badlogic.jpg");

		model = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		ModelInstance cube = new ModelInstance(model);
		cube.transform.setTranslation(-5,0,5);
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

	//FIXME
	public void showTrackingResult(float[] _projectionMatrix, float[] _poseMatrix){
		ModelInstance model = instances.first();

		camera.position.setZero();
		camera.up.setZero();
		camera.direction.setZero();
		camera.transform(new Matrix4(_projectionMatrix));

		float[] maxSTMVP = new float[16];

		model.transform.set(new Matrix4(_poseMatrix));
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
