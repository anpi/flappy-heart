package com.badu.heartrate.monitor;

public interface HeartMonitor {
    public void addSample(byte[] data, int width, int height);

    public boolean isBeat();

    public int getBPM();

    public String getDebugInfo();

    public void reset();
}
