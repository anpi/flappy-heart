package com.badub.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @link https://code.google.com/p/android-heart-rate-monitor/
 * @license Apache License 2.0
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartMonitorOriginal implements HeartMonitor {
    private AtomicBoolean processing = new AtomicBoolean(false);
    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];

    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private double beats = 0;
    private long startTime = 0;

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    private AtomicInteger bpm = new AtomicInteger();

    @Override
    public void addSample(byte[] data, int width, int height) {
        int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false);
            return;
        }

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
        TYPE newType = currentType;
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
                // Log.d(TAG, "BEAT!! beats="+beats);
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize) {
            averageIndex = 0;
        }
        averageArray[averageIndex] = imgAvg;
        averageIndex++;

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;
        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis();
                beats = 0;
                processing.set(false);
                return;
            }

            if (beatsIndex == beatsArraySize) {
                beatsIndex = 0;
            }
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;

            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for (int i = 0; i < beatsArray.length; i++) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i];
                    beatsArrayCnt++;
                }
            }
            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            bpm.set(beatsAvg);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }

    @Override
    public boolean isBeat() {
        // FIXME not implemented (maybe throw an exception?)
        return false;
    }

    @Override
    public int getBpm() {
        return bpm.get();
    }

    @Override
    public String getDebugInfo() {
        return null;
    }

    @Override
    public void reset() {
        
    }
}
