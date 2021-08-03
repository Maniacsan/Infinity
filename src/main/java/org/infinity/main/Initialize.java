package org.infinity.main;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinity.features.HookManager;
import org.infinity.features.ModuleManager;
import org.infinity.features.command.CommandManager;
import org.infinity.features.component.friends.Friend;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.features.module.visual.HUD;
import org.infinity.file.config.ConfigManager;
import org.infinity.protect.HandlerManager;
import org.infinity.protect.IHandler;
import org.infinity.protect.ui.AuthUI;
import org.infinity.ui.account.main.AccountManager;
import org.infinity.ui.menu.ClickMenu;
import org.infinity.utils.user.User;

public class Initialize {

	public IHandler handler;

	private static final Logger LOGGER = LogManager.getLogger();

	public static File dir = new File(InfMain.getDirection() + File.separator);
	public CommandManager commandManager;
	public ConfigManager configManager;
	public ModuleManager moduleManager;
	public HookManager hookManager;
	public AccountManager accountManager;
	public MacroManager macroManager;
	public Friend friend;
	public User user;

	public boolean infChat;

	public ClickMenu menu;
	public AuthUI authUI;

	public Initialize() {
		handler = new HandlerManager();
		authUI = new AuthUI();

		moduleManager = new ModuleManager();
		hookManager = new HookManager();
		accountManager = new AccountManager();
		commandManager = new CommandManager();
		friend = new Friend();
		LOGGER.info("Injected");

	}

	public void shutDown() {

	}

}
