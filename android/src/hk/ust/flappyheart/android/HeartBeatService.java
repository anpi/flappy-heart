package hk.ust.flappyheart.android;

import java.util.Random;

import android.app.IntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Intent;

public class HeartBeatService extends IntentService {
    public static final String BROADCAST_ACTION = "hk.ust.flappyheart.android.HeartBeatService";
    public static final String EXTENDED_DATA_BPM = "hk.ust.flappyheart.android.BPM";
    
    private boolean running = true;

    public HeartBeatService() {
        super("HeartBeatService");
    }
    public HeartBeatService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int bpm = 80;
        Random random = new Random();
        
        while (running) {
            Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_BPM, bpm);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            bpm += random.nextInt(1)-5;
            if (bpm < 50) {
                bpm = 100;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
