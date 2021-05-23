package org.infinity.features;

public enum Category {
	COMBAT("Combat"),
	MOVEMENT("Movement"),
	WORLD("World"),
	PLAYER("Player"),
	VISUAL("Visual"),
	HIDDEN("Hidden"),
	ENABLED("Enabled");
	
	public String name;
	
	Category(String name) {
		this.name = name;
	}
	
} 
