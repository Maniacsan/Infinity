package org.infinity.main;

import java.io.File;

import org.infinity.chat.InfChatHud;
import org.infinity.chat.IRC.IRCClient;
import org.infinity.features.HookManager;
import org.infinity.features.ModuleManager;
import org.infinity.features.command.CommandManager;
import org.infinity.features.component.friends.Friend;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.file.config.ConfigManager;
import org.infinity.protect.IHandler;
import org.infinity.ui.account.main.AccountManager;
import org.infinity.utils.Helper;
import org.infinity.utils.user.User;

public class InfMain {

	public static InfMain INSTANCE = new InfMain();
	private static String NAME = "Infinity";
	private static String VERSION = "1.0.4";

	private static File direction;
	public Initialize init;

	public static boolean firstStart;
	public static boolean reLogin;
	public boolean self;

	/* Minecraft timer */
	public static float TIMER = 1.0f;

	public void initialize() {
		direction = new File(Helper.minecraftClient.runDirectory + File.separator + "Infinity");
		init = new Initialize();

		if (!direction.exists()) {
			firstStart = true;
		}
		self = false;
		reLogin = true;

	}

	public void shutDown() {
		init.shutDown();
	}

	public static String getName() {
		return NAME;
	}

	public static String getVersion() {
		return VERSION;
	}

	public static File getDirection() {
		return direction;
	}

	public static ModuleManager getModuleManager() {
		return InfMain.INSTANCE.init.moduleManager;
	}

	public static HookManager getHookManager() {
		return InfMain.INSTANCE.init.hookManager;
	}

	public static ConfigManager getConfigManager() {
		return InfMain.INSTANCE.init.configManager;
	}

	public static AccountManager getAccountManager() {
		return InfMain.INSTANCE.init.accountManager;
	}

	public static CommandManager getCommandManager() {
		return InfMain.INSTANCE.init.commandManager;
	}

	public static MacroManager getMacroManager() {
		return InfMain.INSTANCE.init.macroManager;
	}

	public static Friend getFriend() {
		return InfMain.INSTANCE.init.friend;
	}

	public static InfChatHud getChatHud() {
		return InfMain.INSTANCE.init.chatHud;
	}

	public static IRCClient getIrc() {
		return InfMain.INSTANCE.init.irc;
	}

	public static IHandler getHandler() {
		return InfMain.INSTANCE.init.handler;
	}
	
	public static User getUser() {
		return InfMain.INSTANCE.init.user;
	}

	public static void setUser(User user) {
		InfMain.INSTANCE.init.user = user;
	}

	public static void resetTimer() {
		TIMER = 1.0f;
	}
}