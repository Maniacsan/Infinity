package me.infinity.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;

/**
 * MotionUpdate event for canceling move , rotate packets for new packet (govno voobshem lucshe ne pridumal)
 * 
 * @author spray
 *
 */
public class MotionEvent extends EventCancellable {
	
	EventType type;
	
	public MotionEvent(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}


}
