package hk.ust.flappyheart.android;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import hk.ust.flappyheart.FlappyHeart;

public class AndroidLauncher extends AndroidApplication {
    private AtomicInteger bpm = new AtomicInteger();
    private HeartBeatReceiver heartBeatReceiver = new HeartBeatReceiver(bpm);
    private IntentFilter heartBeatFilter = new IntentFilter(
            HeartBeatService.BROADCAST_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        Intent heartBeatIntent = new Intent(getApplication(),
                HeartBeatService.class);
        getApplication().startService(heartBeatIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                heartBeatReceiver, heartBeatFilter);

        initialize(new FlappyHeart(bpm), config);
    }
}
