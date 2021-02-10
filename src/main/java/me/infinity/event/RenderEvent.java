package me.infinity.event;

import com.darkmagician6.eventapi.events.Event;

public class RenderEvent implements Event {

	public float tickDelta;
	public double offsetX, offsetY, offsetZ;

	public RenderEvent(float tickDelta, double offsetX, double offsetY, double offsetZ) {
		this.tickDelta = tickDelta;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}

}
