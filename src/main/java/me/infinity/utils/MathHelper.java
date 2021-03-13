package me.infinity.utils;

public class MathHelper {

	public static int clamp(int num, int min, int max) {
		return (num < min) ? min : ((num > max) ? max : num);
	}

	public static float clamp(float num, float min, float max) {
		return (num < min) ? min : ((num > max) ? max : num);
	}

	public static double clamp(double num, double min, double max) {
		return (num < min) ? min : ((num > max) ? max : num);
	}

}
