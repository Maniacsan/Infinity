package org.infinity.utils.user;

import net.minecraft.util.Formatting;

public enum UserRole {

	ADMIN("Admin"), MODERATOR("Moderator"), YOUTUBE(Formatting.RED + "You" + Formatting.BLACK + "Tube"),
	PREMIUM("Premium"), USER("User");

	public String name;

	UserRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
