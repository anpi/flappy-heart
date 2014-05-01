package com.badu.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartMonitorImproved implements HeartMonitor {

	private int averageIndex = 0;
	private final int averageArraySize = 4;
	private final int[] averageArray = new int[averageArraySize];

	private final int beatArraySize = 200;
	private int beatIndex = 0;
	private long[] beatArray = new long[beatArraySize];
	private long lastBeat;
	private int beatCount = 0;

	private final int refArraySize = 10;
	private float maxVariance = 100;

	private long startTime = 0;
	private boolean beat = false;
	private int bpm = -1;

	private final AtomicBoolean processing = new AtomicBoolean(false);
	private String debug = "";

	public HeartMonitorImproved() {
		startTime = System.currentTimeMillis();
		lastBeat = startTime;
	}

	public void addSample(byte[] data, int width, int height) {

		// Prevent multiprocessing
		if (!processing.compareAndSet(false, true))
			return;

		// Compute average red value
		int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
				height, width);

		// Stop if extreme value
		if (imgAvg == 0 || imgAvg == 255) {
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
				beatCount++;
				beat = true;
			}
		} else if (imgAvg > rollingAverage) {
			beat = false;
		}

		// Add entry to average history
		averageArray[averageIndex] = imgAvg;
		averageIndex = (averageIndex + 1) % averageArraySize;

		// Update BPM every 1 seconds
		long endTime = System.currentTimeMillis();
		double totalTimeInSecs = (endTime - startTime) / 1000d;
		if (totalTimeInSecs >= 1) {
			
			if (beatCount <= refArraySize){
				startTime = System.currentTimeMillis();
				processing.set(false);
				return;
			}
			
			long[] refArray = new long[refArraySize];
			for (int i = 0; i < refArraySize; i++)
				refArray[i] = beatArray[(beatIndex - i - 1 + beatArraySize)
						% beatArraySize];

			long mean = 0;
			int beatIter = refArraySize;
			while (beatCount > beatIter) {
				mean = mean(refArray);
				int i = varCheck(refArray, mean, maxVariance);
				if (i == -1)
					break;
				refArray[i] = beatArray[(beatIndex - beatIter + beatArraySize)% beatArraySize];
				beatIter ++;
			}

			bpm = (int) (1000 * 60d / mean);
			
			if (bpm < 30 || bpm > 180)
				bpm = -1;
			
			startTime = System.currentTimeMillis();
		}
		processing.set(false);
	}

	long mean(long[] a) {
		long sum = 0;
		for (long l : a)
			sum += l;
		return sum / a.length;
	}

	int varCheck(long[] a, long mean, float max) {
		double sd = 0;
		double max_diff = 0;
		int max_ind = 0;
		for (int i = 0; i < a.length; i++) {
			double diff = a[i] - mean;
			sd += diff * diff;
			if (Math.abs(diff) > max_diff) {
				max_diff = Math.abs(diff);
				max_ind = i;
			}
		}
		sd = Math.sqrt(sd / a.length);
		sd = sd;
		debug = String.valueOf(mean + " " + (float) sd);
		if (sd < max)
			return -1;
		else
			return max_ind;

	}

	public boolean isBeat() {
		return beat;
	}

	public int getBPM() {
		return bpm;
	}

	public String getDebugInfo() {
		return debug;
	}

	public void reset() {
		startTime = System.currentTimeMillis();
		lastBeat = startTime;
	}

}
