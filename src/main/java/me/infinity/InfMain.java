package me.infinity;

import java.io.File;

import me.infinity.config.ConfigManager;
import me.infinity.features.HookManager;
import me.infinity.features.ModuleManager;
import me.infinity.utils.Helper;

public class InfMain {

	public static InfMain INSTANCE = new InfMain();
	private static String name = "Infinity";
	private static String version = "1.5";
	private static File infDirection;
	private static ConfigManager configManager;
	private static ModuleManager moduleManager;
	private static HookManager hookManager;

	public void initialize() {
		infDirection = new File(Helper.minecraftClient.runDirectory + File.separator + "Infinity");
		configManager = new ConfigManager();
		moduleManager = new ModuleManager();
		hookManager = new HookManager();
		configManager.loadConfig(false);
		System.out.println("Injected bullshit");
	}

	public static String getName() {
		return name;
	}

	public static String getVersion() {
		return version;
	}

	public static ModuleManager getModuleManager() {
		return moduleManager;
	}

	public static HookManager getHookManager() {
		return hookManager;
	}

	public static File getInfDirection() {
		return infDirection;
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}
}