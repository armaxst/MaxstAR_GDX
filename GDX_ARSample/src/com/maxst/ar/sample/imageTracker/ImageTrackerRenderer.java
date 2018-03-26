/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */
package com.maxst.ar.sample.imageTracker;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.badlogic.gdx.ApplicationAdapter;
import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.Trackable;
import com.maxst.ar.TrackedImage;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.TrackingResult;
import com.maxst.ar.TrackingState;
import com.maxst.ar.gdx.MyGdxGame;
import com.maxst.ar.sample.arobject.BackgroundCameraQuad;
import com.maxst.ar.sample.arobject.ColoredCube;
import com.maxst.ar.sample.arobject.TexturedCube;


class ImageTrackerRenderer extends ApplicationAdapter {

	public static final String TAG = ImageTrackerRenderer.class.getSimpleName();

	private ColoredCube coloredCube;
	private TexturedCube texturedCube;

	private int surfaceWidth;
	private int surfaceHeight;
	private BackgroundCameraQuad backgroundCameraQuad;
	private MyGdxGame myARGdxGame;
	private Bitmap bitmap;

	ImageTrackerRenderer(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public void create() {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		backgroundCameraQuad = new BackgroundCameraQuad();

		coloredCube = new ColoredCube();
		texturedCube = new TexturedCube();
		texturedCube.setTextureBitmap(bitmap);

		myARGdxGame = new MyGdxGame();
		myARGdxGame.create();
	}

	@Override
	public void resize(int width, int height) {
		surfaceWidth = width;
		surfaceHeight = height;
		myARGdxGame.resize(width, height);
		MaxstAR.onSurfaceChanged(width, height);
	}

	@Override
	public void render() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);

		TrackingState state = TrackerManager.getInstance().updateTrackingState();
		TrackingResult trackingResult = state.getTrackingResult();

		TrackedImage image = state.getImage();
		float[] cameraProjectionMatrix = CameraDevice.getInstance().getBackgroundPlaneProjectionMatrix();
		backgroundCameraQuad.setProjectionMatrix(cameraProjectionMatrix);
		backgroundCameraQuad.draw(image);

		float[] projectionMatrix = CameraDevice.getInstance().getProjectionMatrix();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		if (trackingResult.getCount() == 0) {
			texturedCube.setProjectionMatrix(projectionMatrix);
			float [] identity = new float[16];
			Matrix.setIdentityM(identity, 0);
			texturedCube.setTransform(identity);
			texturedCube.setTranslate(-0.15f, 0, 1.0f);
			texturedCube.setScale(0.15f, 0.15f, 0.05f);
			texturedCube.draw();

			myARGdxGame.renderDefault(projectionMatrix, identity);
		} else {
			for (int i = 0; i < trackingResult.getCount(); i++) {
				Trackable trackable = trackingResult.getTrackable(i);

				texturedCube.setProjectionMatrix(projectionMatrix);
				texturedCube.setTransform(trackable.getPoseMatrix());
				texturedCube.setTranslate(0, 0, -0.025f);
				texturedCube.setScale(0.15f, 0.15f, 0.05f);
				texturedCube.draw();

				myARGdxGame.showTrackingResult(projectionMatrix, trackable.getPoseMatrix());
			}
		}

		myARGdxGame.render();
	}

	@Override
	public void dispose() {
		myARGdxGame.dispose();
	}
}
