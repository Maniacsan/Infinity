package me.infinity.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

import net.minecraft.client.input.Input;

public class TickMovementEvent extends EventCancellable {
	
	private Input input;

	public TickMovementEvent(Input input) {
		this.setInput(input);
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

}
