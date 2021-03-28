package me.infinity.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;

/**
 * MotionUpdate event for canceling move , rotate packets for new packet (govno
 * voobshem lucshe ne pridumal)
 * 
 * @author spray
 *
 */
public class MotionEvent extends EventCancellable {

	private float yaw;
	private float pitch;

	private double x;
	private double y;
	private double z;

	private boolean onGround;

	private EventType type;

	public MotionEvent(EventType type, float yaw, float pitch, double x, double y, double z, boolean onGround) {
		this.type = type;
		this.yaw = yaw;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
		this.onGround = onGround;
	}

	public EventType getType() {
		return type;
	}

	public void setRotation(float yaw, float pitch) {
		if (Float.isNaN(yaw) || Float.isNaN(pitch) || pitch > 90 || pitch < -90)
			return;

		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void setPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setPosition(double x, double y, double z, boolean onGround) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.onGround = onGround;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

}
