package com.badub.heartrate.monitor;

public interface HeartMonitor {
    /**
     * @param data One frame of camera preview. Non-null.
     * @param width
     * @param height
     */
    public void addSample(byte[] data, int width, int height);

    /**
     * TODO: document
     * @return
     */
    public boolean isBeat();

    /**
     * The latest heart beats per minute for user.
     * @return
     */
    public int getBpm();

    public String getDebugInfo();

    /**
     * TODO document
     */
    public void reset();
}
