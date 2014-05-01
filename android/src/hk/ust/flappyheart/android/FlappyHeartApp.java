package hk.ust.flappyheart.android;

import java.util.List;

import hk.ust.flappyheart.FlappyHeart;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jwetherell.heart_rate_monitor.HeartRateMonitor;

public class FlappyHeartApp extends AndroidApplication {
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    
    private static final String TAG = "HeartRateMonitor";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        View gameView = initializeForView(new FlappyHeart(HeartRateMonitor.bpm), config);
        preview = new SurfaceView(getApplication());
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // The camera must always be visible on screen to take pictures for preview.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(5, size.y);
        preview.setLayoutParams(params);
        
        ViewGroup viewGroup = new LinearLayout(getApplication());
        viewGroup.addView(preview);
        viewGroup.addView(gameView);
        setContentView(viewGroup);
    }
    

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setPictureSize(640, 480);
        camera.setParameters(params);
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
    

    public static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(HeartRateMonitor.previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
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
    };

    private static Camera.Size getSmallestSize(List<Size> sizes) {
        Camera.Size result = sizes.get(0);

        for (Camera.Size size : sizes) {
            if (size.width <= result.width && size.height <= result.height) {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea < resultArea) result = size;
            }
        }
        return result;
    }
}
