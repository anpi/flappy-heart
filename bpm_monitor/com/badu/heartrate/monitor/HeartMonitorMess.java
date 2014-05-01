package com.badu.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartMonitorMess implements HeartMonitor {

	// Results
	private boolean beat = false;
	private int bpm = -1;
	private String debug = "";

	// Time
	private int lastVal = 200;
	private long lastTime, nowTime;
	private int hist_len = 50;

	// Constant / Config
	final static int[] freq_range = new int[] { 30, 180};
	
	// Helper
	private int[] hist;
	private int hpoint = 0;
	private final AtomicBoolean busy = new AtomicBoolean(false);

	public HeartMonitorMess() {
		lastTime = System.currentTimeMillis();
		hist = new int[hist_len];
	}

	public void addSample(byte[] data, int width, int height) {

		// Prevent multiprocessing
		if (!busy.compareAndSet(false, true))
			return;

		// Compute average red value
		int imgAvg = ImageProcessing.decodeYUV420SPtoRedSum(data.clone(), width, height);

		// Differentiate
		nowTime = System.currentTimeMillis();
		int diff = Math.abs(lastVal - imgAvg);
		lastVal = imgAvg;
		lastTime = nowTime;
		
		// Detect
		hist[hpoint] = diff;
		hpoint = (hpoint + 1) % hist_len;
		
		int sum = arraySum(hist);
		if (sum >= 3)
			debug = "+ " + String.valueOf(diff);
		else if (sum == 0)
			debug = "- " + String.valueOf(diff);
		else 
			debug = "  " + String.valueOf(diff);

		busy.set(false);
	}
	
	private int arraySum(int[] a){
		int sum = 0;
		for (int i : a)
			sum += i;
		return sum;
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
		lastTime = System.currentTimeMillis();
	}

}
