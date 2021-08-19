package org.infinity.protect.impl;

import org.infinity.event.protect.SuccessEvent;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.file.config.ConfigManager;
import org.infinity.main.InfMain;
import org.infinity.protect.Handler;
import org.infinity.ui.menu.ClickMenu;
import org.infinity.utils.ConnectUtil;
import org.infinity.via.ViaFabric;

import com.darkmagician6.eventapi.EventTarget;

public class InitProcess extends Handler {

	@EventTarget(3)
	public void onSuccess(SuccessEvent event) {
		InfMain.getHandler().getHandler(TickLogger.class).enable();

		InfMain.INSTANCE.init.configManager = new ConfigManager();
		InfMain.INSTANCE.init.macroManager = new MacroManager();
		InfMain.INSTANCE.init.menu = new ClickMenu();

		// loads
		InfMain.INSTANCE.init.configManager.loadConfig(false);
		InfMain.INSTANCE.init.macroManager.load();
		InfMain.INSTANCE.init.friend.load();
		InfMain.INSTANCE.init.accountManager.load();
		InfMain.INSTANCE.SETTINGS.load();
		
		new ViaFabric().onInitialize();

		ConnectUtil.downloadPhoto();

		setInit(false);
	}
}
