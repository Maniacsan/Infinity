package org.infinity.features.component.cape.config;

import java.util.Arrays;
import java.util.List;

public class Config {

	public Config() {
		options = new Options();
	}

	private Options options;

	public Options getOptions() {
		return options;
	}

	public static class Options {
		public static final Options DEFAULT = new Options();

		public List<String> capeUrls = Arrays.asList("{mojang}", "http://s.optifine.net/capes/{username}.png",
				"https://minecraftcapes.net/profile/{uuid}/cape", "https://dl.labymod.net/capes/{uuid-dash}");
		public boolean useCaching = false;
	}
}
