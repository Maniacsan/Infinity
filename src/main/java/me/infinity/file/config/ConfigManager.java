package me.infinity.file.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import me.infinity.InfMain;

public class ConfigManager {

	public static File dir = new File(InfMain.getInfDirection() + File.separator + "configs");

	public ArrayList<Config> configList = new ArrayList<>();

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
			this.configList.get(this.configList.indexOf(config)).delete();
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
			if (file.getName().endsWith(".json")) {
				for (me.infinity.features.Module m : InfMain.getModuleManager().getList())
					add(new Config(file, m, refresh));
			}
		}
	}

	public ArrayList<Config> getConfigList() {
		return this.configList;
	}

}
