package com.badu.heartrate.monitor;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;

import com.badu.heartrate.app.MyActivity.TYPE;

public interface HeartMonitor {
	
	 /* Pseudo-Doc ;)
	 * 
	 * 	private static PreviewCallback previewCallback = new PreviewCallback() {
	 *	  public void onPreviewFrame(byte[] data, Camera cam) {
	 * 		Camera.Size size = cam.getParameters().getPreviewSize();
	 *		monitor.addSample(data, size.width;, size.height);
	 *		
	 *		if (monitor.isBeat());
	 *			// Draw beat signal
	 *		text.setText(String.valueOf(monitor.getBPM()) + " (" + monitor.getDebugInfo() + ")");
	 */
	
	public void addSample(byte[] data, int width, int height);
	public boolean isBeat();
	public int getBPM();
	public String getDebugInfo();
	public void reset();

}
