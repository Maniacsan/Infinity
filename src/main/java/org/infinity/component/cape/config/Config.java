package org.infinity.component.cape.config;

import java.util.Arrays;
import java.util.List;

public class Config {

	private Options options;

	public Options getOptions() {
		return options;
	}

	public void load() {
		options = new Options();
	}

	public static class Options {
		public static final Options DEFAULT = new Options();

		public List<String> capeUrls = Arrays.asList("{mojang}", "http://s.optifine.net/capes/{username}.png",
				"https://minecraftcapes.net/profile/{uuid}/cape", "https://dl.labymod.net/capes/{uuid-dash}");
		public boolean useCaching = false;
	}
}
