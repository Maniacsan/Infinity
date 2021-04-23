package org.infinity.event.protect;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;

public class StartProcessEvent extends EventCancellable {

	private EventType type;

	public StartProcessEvent(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
}
