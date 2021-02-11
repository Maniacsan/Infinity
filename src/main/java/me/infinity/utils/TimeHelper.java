package me.infinity.utils;

public class TimeHelper {
	
	public static TimeHelper INSTANCE = new TimeHelper();

	private long lastMS = -1L;

	public boolean hasReached(double d2) {
		return (double) (getCurrentTime() - lastMS) >= d2;
	}
	
	public void reset() {
		lastMS = getCurrentTime();
	}

	private long getCurrentTime() {
		return System.nanoTime() / 1000000L;
	}

}
