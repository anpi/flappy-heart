package com.badub.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartMonitorImproved implements HeartMonitor {

    private final int averageArraySize = 4; // Buffer size for rolling average
                                            // beat detection
    private final int refArraySize = 10; // Buffer size for the sample perio BPM
                                         // average computation
    private final float wishVariance = 100; // Desired variance for the sample
                                            // period standard deviation
    private final float maxVariance = 120; // Maxmimum variance for the sample
                                           // period standard deviation
    private final int minRed = 150; // Minimal red value for the sample to be
                                    // accepted

    private final int[] averageArray = new int[averageArraySize];
    private int averageIndex = 0;
    private final int beatArraySize = 100;
    private long[] beatArray = new long[beatArraySize];
    private int beatIndex = 0;
    private long lastBeat;
    private long calcTime;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private boolean beat = false;
    private int bpm = 0;
    private long mean = 0;
    private float sd = 0;
    private int red_val = 0;

    public HeartMonitorImproved() {
        calcTime = System.currentTimeMillis();
        lastBeat = calcTime;
    }

    public void addSample(byte[] data, int width, int height) {

        // Prevent parallel processing
        if (!processing.compareAndSet(false, true))
            return;

        // Compute average red value
        int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
                height, width);
        red_val = imgAvg;

        // Stop if extreme value
        if (imgAvg < minRed || imgAvg == 255) {
            processing.set(false);
            return;
        }

        // Compute rolling average
        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int i = 0; i < averageArraySize; i++) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i];
                averageArrayCnt++;
            }
        }
        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt)
                : 0;

        // Beat computation
        if (imgAvg < rollingAverage) {
            if (!beat) {
                // Add entry to beat history
                beatArray[beatIndex] = System.currentTimeMillis() - lastBeat;
                beatIndex = (beatIndex + 1) % beatArraySize;
                lastBeat = System.currentTimeMillis();
                beat = true;
            }
        } else if (imgAvg > rollingAverage) {
            beat = false;
        }

        // Add entry to average history
        averageArray[averageIndex] = imgAvg;
        averageIndex = (averageIndex + 1) % averageArraySize;

        // Update BPM every 1 seconds
        if ((System.currentTimeMillis() - calcTime) / 1000d >= 1) {

            long[] refArray = new long[refArraySize];
            for (int i = 0; i < refArraySize; i++)
                refArray[i] = beatArray[(beatIndex - i - 1 + beatArraySize)
                        % beatArraySize];

            int beatIter = refArraySize;
            while (beatArray[(beatIndex - beatIter + beatArraySize)
                    % beatArraySize] > 0) {
                calcMean(refArray);
                int i = varianceCheck(refArray, mean, wishVariance);
                if (i == -1 && true)
                    break;
                refArray[i] = beatArray[(beatIndex - beatIter + beatArraySize)
                        % beatArraySize];
                beatIter++;
            }

            int newBpm = (int) (1000 * 60d / mean);
            if (!(sd > maxVariance || newBpm < 30 || newBpm > 180)) {
                bpm = newBpm;
            }

            calcTime = System.currentTimeMillis();
        }
        processing.set(false);
    }

    private void calcMean(long[] a) {
        long sum = 0;
        for (long l : a)
            sum += l;
        mean = sum / a.length;
    }

    private int varianceCheck(long[] a, long mean, float max) {
        double temp = 0;
        double max_diff = 0;
        int max_ind = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - mean;
            temp += diff * diff;
            if (Math.abs(diff) > max_diff) {
                max_diff = Math.abs(diff);
                max_ind = i;
            }
        }
        sd = (float) Math.sqrt(temp / a.length);
        if (sd < max)
            return -1;
        else
            return max_ind;

    }

    public boolean isBeat() {
        return beat;
    }

    public int getBpm() {
        return bpm;
    }

    public String getDebugInfo() {
        return String.format("%d %.0f %d", mean, sd, red_val);
    }

    public void reset() {
    }

}
