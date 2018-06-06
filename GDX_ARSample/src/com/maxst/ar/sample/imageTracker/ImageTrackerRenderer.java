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
import com.maxst.ar.sample.arobject.BackgroundRenderHelper;
import com.maxst.ar.sample.arobject.TexturedCubeRenderer;


class ImageTrackerRenderer extends ApplicationAdapter {

	public static final String TAG = ImageTrackerRenderer.class.getSimpleName();

	private TexturedCubeRenderer texturedCubeRenderer;

	private int surfaceWidth;
	private int surfaceHeight;
	private BackgroundRenderHelper backgroundRenderHelper;

	private MyGdxGame myARGdxGame;
	private Bitmap bitmap;

	ImageTrackerRenderer(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public void create() {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		texturedCubeRenderer = new TexturedCubeRenderer();
		texturedCubeRenderer.setTextureBitmap(bitmap);

		backgroundRenderHelper = new BackgroundRenderHelper();

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
		float[] backgroundPlaneProjectionMatrix = CameraDevice.getInstance().getBackgroundPlaneProjectionMatrix();
		backgroundRenderHelper.drawBackground(image, backgroundPlaneProjectionMatrix);

		float[] projectionMatrix = CameraDevice.getInstance().getProjectionMatrix();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		if (trackingResult.getCount() == 0) {
			texturedCubeRenderer.setProjectionMatrix(projectionMatrix);
			float [] identity = new float[16];
			Matrix.setIdentityM(identity, 0);
			texturedCubeRenderer.setTransform(identity);
			texturedCubeRenderer.setTranslate(-0.15f, 0, 1.0f);
			texturedCubeRenderer.setScale(0.15f, 0.15f, 0.05f);
			texturedCubeRenderer.draw();

			myARGdxGame.renderDefault(projectionMatrix, identity);
		} else {
			for (int i = 0; i < trackingResult.getCount(); i++) {
				Trackable trackable = trackingResult.getTrackable(i);

				texturedCubeRenderer.setProjectionMatrix(projectionMatrix);
				texturedCubeRenderer.setTransform(trackable.getPoseMatrix());
				texturedCubeRenderer.setTranslate(0, 0, -0.025f);
				texturedCubeRenderer.setScale(0.15f, 0.15f, 0.05f);
				texturedCubeRenderer.draw();

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
