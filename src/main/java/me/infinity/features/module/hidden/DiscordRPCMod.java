package me.infinity.features.module.hidden;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.InfMain;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
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
		if (ticks % 40 == 0) {
			long start = 0L;
			start = System.currentTimeMillis() - (ticks * 50);

			String gameStatus = "Idle";

			if (Helper.minecraftClient.isInSingleplayer())
				gameStatus = "in SinglePlayer";
			else if (Helper.minecraftClient.getCurrentServerEntry().address != null) {
				gameStatus = "Playing on: " + Helper.minecraftClient.getCurrentServerEntry().address;
			}

			String detail = "Enabled features " + InfMain.getModuleManager().getEnableModules().size() + "/"
					+ InfMain.getModuleManager().getList().size();

			DiscordRichPresence rich = new DiscordRichPresence.Builder(detail).setBigImage("infinitycosmoslogo", "infinitycosmoslogo")
					.setDetails(gameStatus).setStartTimestamps(start).build();
			DiscordRPC.discordUpdatePresence(rich);
		}

		if (ticks % 200 == 0) {
			DiscordRPC.discordRunCallbacks();
		}

		ticks++;
	}

}
