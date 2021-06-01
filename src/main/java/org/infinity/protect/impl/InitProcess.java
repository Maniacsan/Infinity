package org.infinity.protect.impl;

import org.infinity.chat.IRC.IRCClient;
import org.infinity.clickmenu.ClickMenu;
import org.infinity.event.protect.SuccessEvent;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.file.config.ConfigManager;
import org.infinity.main.InfMain;
import org.infinity.protect.Handler;
import org.infinity.utils.ConnectUtil;

import com.darkmagician6.eventapi.EventTarget;

public class InitProcess extends Handler {

	@EventTarget(3)
	public void onSuccess(SuccessEvent event) {
		InfMain.getHandler().getHandler(TickLogger.class).enable();

		InfMain.INSTANCE.init.configManager = new ConfigManager();
		InfMain.INSTANCE.init.macroManager = new MacroManager();

		// loads
		InfMain.INSTANCE.init.configManager.loadConfig(false);
		InfMain.INSTANCE.init.macroManager.load();

		InfMain.INSTANCE.init.menu = new ClickMenu();
	
		ConnectUtil.downloadPhoto();

		try {
			InfMain.INSTANCE.init.irc = new IRCClient("irc.w3.org", 6667, InfMain.getUser().getName(),
					InfMain.getUser().getRole().getName(), "#InfinityClientModChat");

			InfMain.getIrc().startInit();

		} catch (Exception e) {
		}

		setInit(false);
	}
}
