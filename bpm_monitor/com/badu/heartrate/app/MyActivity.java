package com.badu.heartrate.app;

import com.badu.heartrate.R;
import com.badu.heartrate.monitor.HeartMonitor;
import com.badu.heartrate.monitor.HeartMonitorStandard;
import com.badu.heartrate.monitor.HeartMonitorTrigo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {

	private static final String TAG = "HeartRateMonitor";


	private static SurfaceView preview = null;
	private static SurfaceHolder previewHolder = null;
	private static Camera camera = null;
	private static View image = null;
	private static TextView text = null;

	private static HeartMonitor monitor = null;
	private static WakeLock wakeLock = null;

	public static enum TYPE {
		GREEN, RED
	};
	private static TYPE currentType = TYPE.GREEN;
	public static TYPE getCurrent() {
		return currentType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Create Content
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up surface handlers
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		image = findViewById(R.id.image);
		text = (TextView) findViewById(R.id.text);
		
		// Setup monitor
		monitor = new HeartMonitorTrigo();

		// Power Management
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm
				.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
	}

	@Override
	// Power Management
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		camera = Camera.open();
		monitor.reset();
	}

	@Override
	// Power Management
	public void onPause() {
		super.onPause();
		wakeLock.release();
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	private static PreviewCallback previewCallback = new PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera cam) {
			if (data == null)
				throw new NullPointerException();
			Camera.Size size = cam.getParameters().getPreviewSize();
			if (size == null)
				throw new NullPointerException();

			int width = size.width;
			int height = size.height;
			
			// Call library
			monitor.addSample(data, width, height);
			
			TYPE newType = currentType;
			if (monitor.isBeat()) {
				newType = TYPE.RED;
			} else {
				newType = TYPE.GREEN;
			}
			
			// Redraw beat icon
			if (newType != currentType) {
				currentType = newType;
				image.postInvalidate();
			}
			
			// Set text
			text.setText(String.valueOf(monitor.getBPM()) + " (" + monitor.getDebugInfo() + ")");
			
		}
	};

	// Rest: Camera config & management
	private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallback(previewCallback);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}
		}


		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			Camera.Size size = getSmallestPreviewSize(width, height, parameters);
			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				Log.d(TAG, "Using width=" + size.width + " height="
						+ size.height);
			}
			camera.setParameters(parameters);
			camera.startPreview();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// Ignore
		}
	};

	private static Camera.Size getSmallestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea < resultArea)
						result = size;
				}
			}
		}

		return result;
	}
}
