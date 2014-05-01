package com.badu.heartrate.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartMonitorTrigo implements HeartMonitor {

	// Results
	private boolean beat = false;
	private int bpm = -1;
	private String debug = "";

	// Time
	private int lastVal = 200;
	private long lastTime, nowTime, windowTime;

	// Helper
	private final AtomicBoolean busy = new AtomicBoolean(false);
	private int[][][] trig_table;
	private int[][] freq_table;

	// Constant / Config
	final static int[] freq_range = new int[] { 30, 180};
	final static int phase_gran = 20;
	final static int time_gran = 100; // slots per second
	final static float PI = 3.14159265359f;

	public HeartMonitorTrigo() {
		
		trig_table = new int[freq_range[1] - freq_range[0]][phase_gran][time_gran];
		freq_table = new int[freq_range[1] - freq_range[0]][phase_gran];
		
		// Frequency loop
		for (int f = 0; f < freq_range[1] - freq_range[0]; f++) {
			// Phase offset loop
			for (int p = 0; p < phase_gran; p++) {
				// Time loop
				for (int t = 0; t < time_gran; t++) {
					trig_table[f][p][t] = (int) (100 * Math
							.sin((double) (f + freq_range[0])/freq_range[0] * t / time_gran
									* 2 * PI + (float) p / phase_gran * 2 * PI));
				}
			}
		}
		windowTime = System.currentTimeMillis();
		lastTime = windowTime;
	}

	public void addSample(byte[] data, int width, int height) {

		// Prevent multiprocessing
		if (!busy.compareAndSet(false, true))
			return;

		// Compute average red value
		int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
				height, width);

		// Differentiate
		nowTime = System.currentTimeMillis();
		int diff = (int) ( 1000 * (lastVal - imgAvg) / (nowTime - lastTime));
		lastVal = imgAvg;
		lastTime = nowTime;
		
		debug = String.valueOf(diff);
		
		if (Math.abs(diff) > 30){
			busy.set(false);
			return;
		}
		
		// Compute right bin
		// ! Hardcoded the base period (2s) for 30Hx
		int t = (int) ((nowTime - windowTime) % 2000 * time_gran / 2000) ;
		
		// Frequency loop
		for (int f = 0; f < freq_range[1] - freq_range[0]; f++) {
			// Phase offset loop
			for (int p = 0; p < phase_gran; p++) {
				// Calculate overlap
				freq_table[f][p] += trig_table[f][p][t] * diff;
			}
		}
		
		
		// Check if next window
		if (nowTime - windowTime > 5000){
			
			// Find best frequency fit
			int fit_freq = 0;
			int fit_val = 0;
			for (int f = 0; f < freq_range[1] - freq_range[0]; f++) {
				for (int p = 0; p < phase_gran; p++) {
					if(freq_table[f][p] > fit_val){
						fit_freq = f;
						fit_val = Math.abs(freq_table[f][p]);
					}
				}
			}
			bpm = fit_freq + freq_range[0];
			
			freq_table = new int[freq_range[1] - freq_range[0]][phase_gran];
			for (int f = 0; f < freq_range[1] - freq_range[0]; f++) {
				for (int p = 0; p < phase_gran; p++) {
					freq_table[f][p] = 0;
				}
			}
			windowTime = nowTime;
		}

		busy.set(false);
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
		windowTime = System.currentTimeMillis();
		lastTime = windowTime;
	}

}
