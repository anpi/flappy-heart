package com.jwetherell.heart_rate_monitor;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.badub.heartrate.monitor.HeartMonitor;

public class SurfaceHolderCallback implements Callback {
    private Camera camera;
    private SurfaceHolder previewHolder;
    private static final String TAG = "SurfaceHolderCallback";
    private HeartMonitor monitor;

    public SurfaceHolderCallback(SurfaceHolder ph, HeartMonitor monitor) {
        this.previewHolder = ph;
        this.monitor = monitor;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            onResume();
        }
        try {
            camera.setPreviewDisplay(previewHolder);
            camera.setPreviewCallback(new HeartMonitorCallback(monitor));
        } catch (Throwable t) {
            Log.e(TAG, "Exception in setPreviewDisplay()", t);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        Camera.Size size = getSmallestSize(parameters.getSupportedPreviewSizes());
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
            Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
        }
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private Camera.Size getSmallestSize(List<Size> sizes) {
        Camera.Size result = sizes.get(0);

        for (Camera.Size size : sizes) {
            if (size.width <= result.width && size.height <= result.height) {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea < resultArea)
                    result = size;
            }
        }
        return result;
    }

    public void onResume() {
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setPictureSize(640, 480);
        camera.setParameters(params);
    }

    public void onPause() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
