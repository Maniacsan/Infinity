package me.infinity.config;

import me.infinity.features.Module;

public class ConfigData {

	private String name;
	private boolean enabled;
	private boolean visible;
	private int key;

	public ConfigData(String name, boolean enabled, boolean visible, int key) {
		super();
		this.name = name;
		this.enabled = enabled;
		this.visible = visible;
		this.key = key;
	}

	public ConfigData(Module module) {
		this.name = module.getName();
		this.enabled = module.isEnabled();
		this.visible = module.isVisible();
		this.key = module.getKey();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public boolean isVisible() {
		return visible;
	}

}
