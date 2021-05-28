package org.infinity.main;

import java.io.File;
import java.io.IOException;

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
import org.infinity.utils.user.User;

import net.minecraft.client.MinecraftClient;

public class Initialize {

	public IHandler handler;

	public static File dir = new File(InfMain.getDirection() + File.separator);
	public CommandManager commandManager;
	public ConfigManager configManager;
	public ModuleManager moduleManager;
	public HookManager hookManager;
	public AccountManager accountManager;
	public MacroManager macroManager;
	public Friend friend;
	public User user;

	public InfChatHud chatHud;
	public IRCClient irc;

	public boolean infChat;

	public ClickMenu menu;
	public AuthUI authUI;

	public Initialize() {
		chatHud = new InfChatHud(MinecraftClient.getInstance());

		//
		handler = new HandlerManager();
		authUI = new AuthUI();

		moduleManager = new ModuleManager();
		hookManager = new HookManager();
		accountManager = new AccountManager();
		commandManager = new CommandManager();
		friend = new Friend();
		
		// loads
		friend.load();
		accountManager.load();

		moduleManager.getModuleByClass(HUD.class).enable();
		moduleManager.getModuleByClass(DiscordRPCMod.class).enable();
	}

	public void shutDown() {
		if (irc == null)
			return;

		if (irc.isActive()) {

			try {
				irc.quit("Client disconnected.", false);
			} catch (IOException e) {
			}
		}
	}

}