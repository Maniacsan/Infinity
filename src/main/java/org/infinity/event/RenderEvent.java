package org.infinity.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;

public class RenderEvent extends EventCancellable {

	private EventType type;

	private float tickDelta;

	public RenderEvent(EventType type, float tickDelta) {
		this.type = type;
		this.tickDelta = tickDelta;
	}

	public EventType getType() {
		return type;
	}

	public float getTickDelta() {
		return tickDelta;
	}

}
