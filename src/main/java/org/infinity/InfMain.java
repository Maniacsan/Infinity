package org.infinity;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import org.infinity.chat.InfChatHud;
import org.infinity.chat.IRC.IRCClient;
import org.infinity.clickmenu.ClickMenu;
import org.infinity.features.HookManager;
import org.infinity.features.ModuleManager;
import org.infinity.features.command.CommandManager;
import org.infinity.features.component.friends.Friend;
import org.infinity.features.component.macro.MacroManager;
import org.infinity.features.module.hidden.DiscordRPCMod;
import org.infinity.features.module.visual.HUD;
import org.infinity.file.config.ConfigManager;
import org.infinity.protect.HandlerManager;
import org.infinity.protect.IHandler;
import org.infinity.protect.ui.AuthUI;
import org.infinity.ui.account.main.AccountManager;
import org.infinity.utils.Helper;
import org.infinity.utils.user.User;

import net.minecraft.client.MinecraftClient;

public class InfMain {

	public static InfMain INSTANCE = new InfMain();
	private static String name = "Infinity";
	private static String version = "1.01";
	private static File infDirection;

	private static IHandler handler;

	public static CommandManager commandManager;
	public static ConfigManager configManager;
	public static ModuleManager moduleManager;
	private static HookManager hookManager;
	private static AccountManager accountManager;
	public static MacroManager macroManager;
	private static Friend friend;
	private static User user;

	public static InfChatHud chatHud;
	public static IRCClient irc;

	public static boolean infChat;

	public static ClickMenu menu;
	
	public static AuthUI authUI;

	public static Supplier<Boolean> selfDestruct;
	public static boolean firstStart; /* Check first start mod */
	public static boolean reLogin; /* Re-Loginitsya pri pervom zapuske mira */

	/* Minecraft timer */
	public static float TIMER = 1.0f;

	public void initialize() {

		chatHud = new InfChatHud(MinecraftClient.getInstance());

		infDirection = new File(Helper.minecraftClient.runDirectory + File.separator + "Infinity");

		//
		handler = new HandlerManager();
		authUI = new AuthUI();

		moduleManager = new ModuleManager();
		hookManager = new HookManager();
		accountManager = new AccountManager();
		commandManager = new CommandManager();
		friend = new Friend();

		if (!infDirection.exists()) {
			firstStart = true;
		}
		reLogin = true;

		// loads
		friend.load();
		accountManager.load();
		
		InfMain.moduleManager.getModuleByClass(HUD.class).enable();
		InfMain.moduleManager.getModuleByClass(DiscordRPCMod.class).enable();
			
	}

	// shutdown process
	public static void onShutdown() {
		if (irc == null)
			return;
		
		if (irc.isActive()) {

			try {
				irc.quit("Client disconnected.", false);
			} catch (IOException e) {
			}
		}
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

	public static AccountManager getAccountManager() {
		return accountManager;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static MacroManager getMacroManager() {
		return macroManager;
	}

	public static Friend getFriend() {
		return friend;
	}

	public static InfChatHud getChatHud() {
		return chatHud;
	}

	public static IRCClient getIrc() {
		return irc;
	}

	public static IHandler getHandler() {
		return handler;
	}

	public static User getUser() {
		return user;
	}
	
	public static void setUser(User user) {
		InfMain.user = user;
	}

	public static void resetTimer() {
		TIMER = 1.0f;
	}
}