package hk.ust.flappyheart.android;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeartBeatReceiver extends BroadcastReceiver {
    private AtomicInteger bpm;
    
    public HeartBeatReceiver(AtomicInteger bpm) {
        this.bpm = bpm;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        bpm.set(intent.getIntExtra(HeartBeatService.EXTENDED_DATA_BPM, -1));
    }

}
