package me.infinity.utils;

public class TimeHelper {

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

	public static void runInThread(Runnable r) {
		(new Thread(() -> {
			try {
				r.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).start();
	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	

}
