package me.infinity.features.component;

public class AntiFabricSpoof {

	private static boolean enabled;

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		AntiFabricSpoof.enabled = enabled;
	}

}
