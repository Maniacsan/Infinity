package org.infinity.features.component.cape;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinity.features.component.cape.cape.CapeProvider;
import org.infinity.features.component.cape.config.Config;
import org.infinity.features.component.cape.mixinterface.PlayerSkinProviderAccess;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

public class AnyCapes {
	public static final Logger LOGGER = LogManager.getLogger();

	private static Config config;

	public static Config getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	private static void loadConfig() {
		config = new Config();
	}

	public void onInitialize(MinecraftClient client) {
		PlayerSkinProviderAccess skinProviderAccess = (PlayerSkinProviderAccess) client.getSkinProvider();
		skinProviderAccess.setCapeProvider(new CapeProvider(skinProviderAccess.getSkinCacheDir(),
				skinProviderAccess.getTextureManager(), Util.getMainWorkerExecutor(), client.getNetworkProxy()));
	}
}
