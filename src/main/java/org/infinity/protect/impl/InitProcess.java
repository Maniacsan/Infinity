package org.infinity.protect.impl;

import org.infinity.InfMain;
import org.infinity.chat.IRC.IRCClient;
import org.infinity.clickmenu.ClickMenu;
import org.infinity.event.protect.SuccessEvent;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.file.config.ConfigManager;
import org.infinity.protect.Handler;

import com.darkmagician6.eventapi.EventTarget;

public class InitProcess extends Handler {

	@EventTarget(3)
	public void onSuccess(SuccessEvent event) {
		InfMain.getHandler().getHandler(TickLogger.class).enable();

		InfMain.configManager = new ConfigManager();
		InfMain.macroManager = new MacroManager();

		// loads
		InfMain.configManager.loadConfig(false);
		InfMain.macroManager.load();

		InfMain.menu = new ClickMenu();

		try {
			InfMain.irc = new IRCClient("irc.w3.org", 6667, InfMain.getUser().getName(),
					InfMain.getUser().getRole().getName(), "#InfinityClientModChat");

			InfMain.getIrc().startInit();
			
		} catch (Exception e) {
		}

		setInit(false);
	}
}
