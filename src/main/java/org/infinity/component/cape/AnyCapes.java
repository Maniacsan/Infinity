package org.infinity.component.cape;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinity.component.cape.cape.CapeProvider;
import org.infinity.component.cape.config.Config;
import org.infinity.component.cape.mixinterface.PlayerSkinProviderAccess;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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
		config.load();
	}

	public void onInitialize() {
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
			PlayerSkinProviderAccess skinProviderAccess = (PlayerSkinProviderAccess) client.getSkinProvider();
			skinProviderAccess.setCapeProvider(new CapeProvider(
				skinProviderAccess.getSkinCacheDir(),
				skinProviderAccess.getTextureManager(),
				Util.getMainWorkerExecutor(),
				client.getNetworkProxy()
			));
		});
	}
}
