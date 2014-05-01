package hk.ust.flappyheart.android;

import java.util.concurrent.atomic.AtomicInteger;

import hk.ust.flappyheart.FlappyHeart;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
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
import com.jwetherell.heart_rate_monitor.SurfaceHolderCallback;

/**
 * 
 * @author aaltoan
 */
public class FlappyHeartApp extends AndroidApplication {
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private SurfaceHolderCallback monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        AtomicInteger bpm = new AtomicInteger();
        View gameView = initializeForView(new FlappyHeart(bpm), config);
        SurfaceView preview = new SurfaceView(getApplication());
        previewHolder = preview.getHolder();
        monitor = new SurfaceHolderCallback(camera, previewHolder, bpm);
        previewHolder.addCallback(monitor);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // The camera must always be visible on screen to take pictures for
        // preview.
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
        monitor.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        monitor.onPause();
    }
}
