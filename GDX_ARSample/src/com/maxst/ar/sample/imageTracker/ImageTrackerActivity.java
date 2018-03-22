/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package com.maxst.ar.sample.imageTracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.MaxstARUtil;
import com.maxst.ar.ResultCode;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.sample.ARActivity;
import com.maxst.ar.sample.util.SampleUtil;

public class ImageTrackerActivity extends ARActivity {

	private ImageTrackerRenderer imageTargetRenderer;
	private int preferCameraResolution = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TrackerManager.getInstance().addTrackerData("ImageTarget/Blocks.2dmap", true);
		TrackerManager.getInstance().addTrackerData("ImageTarget/Glacier.2dmap", true);
		TrackerManager.getInstance().addTrackerData("ImageTarget/Lego.2dmap", true);
		TrackerManager.getInstance().loadTrackerData();

		preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);

		Bitmap bitmap = MaxstARUtil.getBitmapFromAsset("MaxstAR_Cube.png", getAssets());

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new ImageTrackerRenderer(bitmap), config);
	}

	@Override
	protected void onResume() {
		super.onResume();

		TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_IMAGE);

		ResultCode resultCode = ResultCode.Success;
		switch (preferCameraResolution) {
			case 0:
				resultCode = CameraDevice.getInstance().start(0, 640, 480);
				break;

			case 1:
				resultCode = CameraDevice.getInstance().start(0, 1280, 720);
				break;

			case 2:
				resultCode = CameraDevice.getInstance().start(0, 1920, 1080);
				break;
		}

		MaxstAR.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		TrackerManager.getInstance().stopTracker();
		CameraDevice.getInstance().stop();
		MaxstAR.onPause();
	}
}
