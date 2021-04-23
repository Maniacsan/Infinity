package org.infinity.features.module.hidden;

import org.infinity.InfMain;
import org.infinity.event.TickEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.protect.impl.TickLogger;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

@ModuleInfo(category = Module.Category.HIDDEN, desc = "", key = -2, name = "DiscordRPC", visible = false)
public class DiscordRPCMod extends Module {

	private int ticks = 0;

	@Override
	public void onEnable() {
		ticks = 0;
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			System.out.println("Welcome " + user.username + "#" + user.discriminator);
		}).build();
		DiscordRPC.discordInitialize("827576969478537226", handlers, true);
	}

	@Override
	public void onDisable() {
		DiscordRPC.discordShutdown();
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (Helper.getWorld() != null && !InfMain.getHandler().getHandler(TickLogger.class).enabled)
			me.protect.utils.PHelper.makeCrash();

		if (ticks % 40 == 0) {
			long start = 0L;
			start = System.currentTimeMillis() - (ticks * 50);

			String gameStatus = "Idle";

			if (Helper.minecraftClient.isInSingleplayer())
				gameStatus = "in SinglePlayer";
			else if (Helper.minecraftClient.getCurrentServerEntry().address != null)
				gameStatus = "Playing on: " + Helper.minecraftClient.getCurrentServerEntry().address;
			else
				gameStatus = "Idle";

			String detail = "Enabled features " + InfMain.getModuleManager().getEnableModules().size() + "/"
					+ InfMain.getModuleManager().getList().size();

			DiscordRichPresence rich = new DiscordRichPresence.Builder(detail)
					.setBigImage("infinitycosmoslogo", InfMain.getName() + " - " + InfMain.getVersion())
					.setDetails(gameStatus).setStartTimestamps(start).build();
			DiscordRPC.discordUpdatePresence(rich);
		}

		if (ticks % 200 == 0) {
			DiscordRPC.discordRunCallbacks();
		}

		ticks++;
	}

}
