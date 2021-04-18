package me.infinity;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.infinity.chat.InfChatHud;
import me.infinity.chat.IRC.IRCClient;
import me.infinity.clickmenu.ClickMenu;
import me.infinity.features.HookManager;
import me.infinity.features.ModuleManager;
import me.infinity.features.command.CommandManager;
import me.infinity.features.component.friends.Friend;
import me.infinity.features.component.macro.MacroManager;
import me.infinity.features.module.hidden.DiscordRPCMod;
import me.infinity.features.module.visual.HUD;
import me.infinity.file.config.ConfigManager;
import me.infinity.ui.account.main.AccountManager;
import me.infinity.utils.Helper;
import me.infinity.utils.user.User;
import net.minecraft.client.MinecraftClient;

public class InfMain {

	private static final Logger LOGGER = LogManager.getLogger();
	public static InfMain INSTANCE = new InfMain();
	private static String name = "Infinity";
	private static String version = "1.5";
	private static File infDirection;
	private static CommandManager commandManager;
	private static ConfigManager configManager;
	private static ModuleManager moduleManager;
	private static HookManager hookManager;
	private static AccountManager accountManager;
	private static MacroManager macroManager;
	private static Friend friend;
	private static User user;

	private static InfChatHud chatHud;
	private static IRCClient irc;

	public static boolean infChat;

	public static ClickMenu menu;

	public static boolean firstStart; /* Check first start mod */

	/* Minecraft timer */
	public static float TIMER = 1.0f;

	public void initialize() {
		infDirection = new File(Helper.minecraftClient.runDirectory + File.separator + "Infinity");
		//
		configManager = new ConfigManager();
		moduleManager = new ModuleManager();
		hookManager = new HookManager();
		accountManager = new AccountManager();
		commandManager = new CommandManager();
		macroManager = new MacroManager();
		friend = new Friend();
		chatHud = new InfChatHud(MinecraftClient.getInstance());

		if (!infDirection.exists()) {
			firstStart = true;
		}

		// dbuser set
		user = new User("spray", "ADMIN");
		irc = new IRCClient("irc.w3.org", 6667, user.getName(), user.getRole().getName(), "#InfinityModChat");

		// loads
		friend.load();
		configManager.loadConfig(false);
		accountManager.load();
		macroManager.load();

		// start modules
		moduleManager.getModuleByClass(HUD.class).enable();
		moduleManager.getModuleByClass(DiscordRPCMod.class).enable();

		menu = new ClickMenu();

		if (!irc.isActive()) {
			try {
				irc.start();
				irc.joinChannel(irc.getChannel());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		LOGGER.info("Injected bullshit");
	}

	// shutdown process
	public static void onShutdown() {
		if (irc.isActive()) {

			try {
				irc.quit("Client disconnected.", false);
			} catch (IOException e) {
				e.printStackTrace();
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

	public static User getUser() {
		return user;
	}

	public static void resetTimer() {
		TIMER = 1.0f;
	}
}