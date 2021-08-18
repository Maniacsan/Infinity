package org.infinity.features.module.misc;

import org.infinity.event.TickEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.main.InfMain;
import org.infinity.protect.impl.TickLogger;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.protect.utils.PHelper;

@ModuleInfo(category = Category.MISC, desc = "", key = -2, name = "DiscordRPC", visible = false)
public class DiscordRPCMod extends Module {

	private static final DiscordRichPresence rpc = new DiscordRichPresence();
	private static final DiscordRPC instance = DiscordRPC.INSTANCE;
	private int ticks = 0;

	@Override
	public void onEnable() {
		DiscordEventHandlers handlers = new DiscordEventHandlers();

		instance.Discord_Initialize("827576969478537226", handlers, true, null);
		rpc.startTimestamp = System.currentTimeMillis() / 1000L;
		rpc.largeImageKey = "rpclogo";
		ticks = 0;
		rpc.largeImageText = InfMain.NAME + " " + InfMain.VERSION;
		if (InfMain.getUser().getName().isEmpty() || InfMain.getUser().getName() == null)
			PHelper.makeCrash();

		rpc.smallImageKey = "infinity";
		rpc.smallImageText = InfMain.getUser().getName();

		instance.Discord_UpdatePresence(rpc);
		instance.Discord_RunCallbacks();
	}

	@Override
	public void onDisable() {
		instance.Discord_ClearPresence();
		instance.Discord_Shutdown();
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (Helper.getWorld() != null && !InfMain.getHandler().getHandler(TickLogger.class).enabled)
			PHelper.makeCrash();

		if (ticks % 50 == 0) {

			String gameStatus = "Idle";

			if (Helper.MC.isInSingleplayer())
				gameStatus = "in SinglePlayer";
			else if (Helper.MC.getCurrentServerEntry().address != null)
				gameStatus = "Playing on: " + Helper.MC.getCurrentServerEntry().address;
			else
				gameStatus = "Idle";

			String detail = "Features " + InfMain.getModuleManager().getEnableModules().size() + "/"
					+ InfMain.getModuleManager().getList().size();

			rpc.details = gameStatus;
			rpc.state = detail;
			instance.Discord_RunCallbacks();
		}

		if (ticks % 200 == 0) {
			instance.Discord_UpdatePresence(rpc);
		}

		ticks++;
	}

}
