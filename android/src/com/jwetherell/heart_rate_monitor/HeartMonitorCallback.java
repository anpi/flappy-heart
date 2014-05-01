package com.jwetherell.heart_rate_monitor;

import com.badub.heartrate.monitor.HeartMonitor;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;

public class HeartMonitorCallback implements PreviewCallback {
    private HeartMonitor monitor;
    
    public HeartMonitorCallback(HeartMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null) {
            throw new NullPointerException();
        }
        
        Camera.Size size = camera.getParameters().getPreviewSize();
        if (size == null) {
            throw new NullPointerException();
        }

        monitor.addSample(data, size.width, size.height);
    }

}
