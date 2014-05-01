package com.badub.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartMonitorStandard implements HeartMonitor {

    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];
    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private double beats = 0;
    private long startTime = 0;
    private boolean beat = false;
    private int beatsAvg = -1;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private String debug = "";

    public HeartMonitorStandard() {

    }

    public void addSample(byte[] data, int width, int height) {
        // Prevent multiprocessing
        if (!processing.compareAndSet(false, true))
            return;

        // Compute average red value
        int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
                height, width);

        debug = String.valueOf(imgAvg);

        // Stop if extreme value
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false);
            return;
        }

        // Compute rolling average
        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int i = 0; i < averageArray.length; i++) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i];
                averageArrayCnt++;
            }
        }
        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt)
                : 0;

        // Determine if beat
        if (imgAvg < rollingAverage) {
            if (!beat) {
                beats++;
                beat = true;
            }
        } else if (imgAvg > rollingAverage) {
            beat = false;
        }

        // Add entry to average color history
        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = imgAvg;
        averageIndex++;

        // Temporal computations
        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;

        // Update BPM every 10 seconds
        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            // Reset if unrealistic value
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis();
                beats = 0;
                processing.set(false);
                return;
            }

            // Add to beats history
            if (beatsIndex == beatsArraySize)
                beatsIndex = 0;
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;

            // Compute beat average
            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for (int i = 0; i < beatsArray.length; i++) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i];
                    beatsArrayCnt++;
                }
            }
            beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }

    public boolean isBeat() {
        return beat;
    }

    public int getBpm() {
        return beatsAvg;
    }

    public String getDebugInfo() {
        return debug;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

}
