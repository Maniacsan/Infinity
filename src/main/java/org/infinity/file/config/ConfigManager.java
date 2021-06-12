package org.infinity.file.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

public class ConfigManager {

	public static File dir = new File(InfMain.getDirection() + File.separator + "configs");

	public ArrayList<Config> configList;

	public ConfigManager() {
		configList = new ArrayList<>();
	}

	public void add(Config config) {
		Config possibleConfiguration = fromName(config.getName());
		if (possibleConfiguration == null) {
			this.configList.add(config);
		} else {
			this.configList.set(this.configList.indexOf(possibleConfiguration), config);
		}
	}

	public void delete(Config config) {
		if (this.configList.contains(config))
			config.delete();
		this.configList.remove(config);
	}

	public Config fromName(String name) {
		for (Config config : this.configList) {
			if (config.getName().equals(name))
				return config;
		}
		return null;
	}

	public void loadConfig(boolean refresh) {
		if (!dir.exists())
			dir.mkdirs();
		this.configList.clear();
		for (File file : (File[]) Objects.<File[]>requireNonNull(dir.listFiles())) {
			if (Helper.isUser() && configList.size() >= 7)
				continue;
			
			if (file.getName().endsWith(".json")) {
				for (org.infinity.features.Module m : InfMain.getModuleManager().getList())
					add(new Config(file, m, refresh));
			}
		}
	}

	public ArrayList<Config> getConfigList() {
		return this.configList;
	}

}
