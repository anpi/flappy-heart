package com.badu.heartrate.monitor;


public class HeartMonitorReverse implements HeartMonitor {

	// Constants
	static final int MAX_PEAKS = 12500;
	static final int MAX_SAMPLES = 50000;
	static final int TEMP_SIZE = 100;
	int DIR_DOWN = -1;
	int DIR_UNDEF = 0;
	int DIR_UP = 1;

	int calculatedHr = -1;
	public int currentFps = 30;
	int detDir = this.DIR_UNDEF;
	int fpsCounter = 0;
	long fpsLastTimeStamp = 0L;
	long fpsPrevTimeStamp = 0L;
	public int hartRateLimit = this.maxHeartRate;
	final int hrMax = 30;
	final int hrMin = 4;
	int maxAveragingTime = 15000;
	public int maxHeartRate = 190;
	long maxTime = 1500L;
	int maxTimeVariability = 200;
	public int minHeartRate = 30;
	int peaksCnt = 0;
	long[] peaksT = new long[12500];
	int[] peaksV = new int[12500];
	long sampleDeltaMax = 0L;
	long sampleDeltaMin = 1000L;
	int samplesCnt = 0;
	long[] samplesT = new long[50000];
	int[] samplesV = new int[50000];
	int shanonFactor = 6;
	final int[] tempBuffer = new int[100];
	int thD = 4;
	int totalMeasuringTime = 0;
	int validPeaksCnt = 0;
	long[] validPeaksT = new long[12500];
	int[] validPeaksV = new int[12500];
	int valueDifferenceTriggerFactor = 5;
	
	String debug = "";

	public HeartMonitorReverse() {

	}

	public void addSample(byte[] data, int width, int height) {
		// Compute average red value
		int imgVal = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
				height, width);
		
		// Create timestamp
		long timeNow = System.nanoTime() / 1000000L;

		// Store values
	    samplesT[samplesCnt] = timeNow;
	    samplesV[samplesCnt] = imgVal;
		debug = String.valueOf(imgVal);
		

		// Compute temporal parameters
		if (timeNow - fpsLastTimeStamp > 1000L) {
			currentFps = fpsCounter;
			fpsCounter = 0;
			fpsLastTimeStamp = timeNow;
			hartRateLimit = Math.min(maxHeartRate, 60 * currentFps
					/ shanonFactor);
			sampleDeltaMax = 0L;
			sampleDeltaMin = 1000L;
		}
		fpsCounter = (1 + fpsCounter);
		if (fpsPrevTimeStamp > 0L) {
			sampleDeltaMax = Math.max(sampleDeltaMax, timeNow
					- fpsPrevTimeStamp);
			sampleDeltaMin = Math.min(sampleDeltaMin, timeNow
					- fpsPrevTimeStamp);
		}
		fpsPrevTimeStamp = timeNow;

		// Update samples count
		samplesCnt = (1 + samplesCnt);
		if (samplesCnt < 2)
			return;

		// Case Navigation / Peak detection
		if (detDir == DIR_UP) {
			if (samplesV[(-1 + samplesCnt)] < samplesV[(-2 + samplesCnt)]){
			addPeak(samplesT[(-2 + samplesCnt)], samplesV[(-2 + samplesCnt)]);
			detDir = DIR_DOWN;
			}
		}
		else if (detDir == DIR_DOWN) {
			if (samplesV[(-1 + samplesCnt)] > samplesV[(-2 + samplesCnt)]) {
				addPeak(samplesT[(-2 + samplesCnt)],
						samplesV[(-2 + samplesCnt)]);
				detDir = DIR_UP;
			}
		} else {
			if (samplesV[(-1 + samplesCnt)] < samplesV[(-2 + samplesCnt)]) {
				detDir = DIR_UP;
			} else
				detDir = DIR_DOWN;
		}
	}

	public void addPeak(long timeStamp, int peakValue) {
		// Log and count peak
		peaksT[peaksCnt] = timeStamp;
		peaksV[peaksCnt] = peakValue;
		validPeaksT[validPeaksCnt] = timeStamp;
		validPeaksV[validPeaksCnt] = peakValue;
		peaksCnt = (1 + peaksCnt);
		validPeaksCnt = (1 + validPeaksCnt);

		if (validPeaksCnt > 0)
			return;

		// Abort analysis if not enough data;
		if (validPeaksCnt < 4)
			return;

		// Remove duplicate peaks
		int valDiff3;
		do {
			valDiff3 = validPeaksV[(-4 + validPeaksCnt)]
					- validPeaksV[(-3 + validPeaksCnt)]; // i
			int valDiff2 = validPeaksV[(-3 + validPeaksCnt)]
					- validPeaksV[(-2 + validPeaksCnt)]; // j
			int valDiff1 = validPeaksV[(-2 + validPeaksCnt)]
					- validPeaksV[(-1 + validPeaksCnt)]; // k

			long timeDiff3 = validPeaksT[(-3 + validPeaksCnt)]
					- validPeaksT[(-4 + validPeaksCnt)]; // l1
			long timeDiff2 = validPeaksT[(-2 + validPeaksCnt)]
					- validPeaksT[(-3 + validPeaksCnt)]; // l2
			//long timeDiff1 = validPeaksT[(-1 + validPeaksCnt)]
			//		- validPeaksT[(-2 + validPeaksCnt)];
			long timeDiffSum = timeDiff3 + timeDiff2;

			// Peaks are too close and not different enough
			if ((Math.abs(valDiff2) < Math.abs(valDiff3 / thD))
					&& (timeDiffSum < maxTime)) {
				// Duplicate case (remove)
				if (Math.abs(valDiff1) < Math.abs(valDiff2)) {
					validPeaksCnt = (-2 + validPeaksCnt);
					return;
				}

				// Stretch case (merge)
				validPeaksV[(-3 + validPeaksCnt)] = validPeaksV[(-1 + validPeaksCnt)];
				validPeaksT[(-3 + validPeaksCnt)] = validPeaksT[(-1 + validPeaksCnt)];
				validPeaksCnt = (-2 + validPeaksCnt);
				return;
			}
		} while (valDiff3 >= 0);
	}

	public void calcHR() {
		// Abort analysis if not enough data;
		if (validPeaksCnt < 15)
			return;
		double timeDiff =  ((double)(validPeaksT[(-1 + validPeaksCnt)]
				- validPeaksT[(-15 + validPeaksCnt)]))/100;
		calculatedHr = (int) (15 / timeDiff * 60 * 2);

	}

	public boolean isBeat() {
		return false;
	}

	public int getBPM() {
		calcHR();
		return calculatedHr;
	}

	public String getDebugInfo() {
		return debug;
	}

	public void reset() {
	}

}
