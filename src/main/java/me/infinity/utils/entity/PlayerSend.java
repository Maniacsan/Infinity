package me.infinity.utils.entity;

public class PlayerSend {

	private static float yaw;
	private static float pitch;

	private static double x;
	private static double y;
	private static	 double z;

	private static boolean onGround;

	public PlayerSend(float yaw, float pitch, double x, double y, double z, boolean onGround) {
		PlayerSend.yaw = yaw;
		PlayerSend.pitch = pitch;
		PlayerSend.x = x;
		PlayerSend.y = y;
		PlayerSend.z = z;
		PlayerSend.onGround = onGround;
	}

	public static void setRotation(float yaw, float pitch) {
		if (Float.isNaN(yaw) || Float.isNaN(pitch) || pitch > 90 || pitch < -90)
			return;

		setYaw(yaw);
		setPitch(pitch);
	}

	public static void setPosition(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public static void setPosition(double x, double y, double z, boolean onGround) {
		setX(x);
		setY(y);
		setZ(z);
		setOnGround(onGround);
	}

	public static float getYaw() {
		return yaw;
	}

	public static void setYaw(float yaw) {
		PlayerSend.yaw = yaw;
	}

	public static float getPitch() {
		return pitch;
	}

	public static void setPitch(float pitch) {
		PlayerSend.pitch = pitch;
	}

	public static double getX() {
		return x;
	}

	public static void setX(double x) {
		PlayerSend.x = x;
	}

	public static double getY() {
		return y;
	}

	public static void setY(double y) {
		PlayerSend.y = y;
	}

	public static double getZ() {
		return z;
	}

	public static void setZ(double z) {
		PlayerSend.z = z;
	}

	public static boolean isOnGround() {
		return onGround;
	}

	public static void setOnGround(boolean onGround) {
		PlayerSend.onGround = onGround;
	}

}
