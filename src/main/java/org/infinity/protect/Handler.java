package org.infinity.protect;

import com.darkmagician6.eventapi.EventManager;

public abstract class Handler {
	
	public boolean enabled;

	public void init() {
		EventManager.register(this);
		absInit();
	}

	public void deInit() {
		EventManager.unregister(this);
		absDeInit();
	}

	public void setInit(boolean init) {
		if (init) {
			init();
			enabled = true;
		} else if (!init) {
			deInit();
			enabled = false;
		}
	}

	public void enable() {
		setInit(true);
	}

	public void absInit() {

	}

	public void absDeInit() {

	}
}
