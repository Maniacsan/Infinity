package org.infinity.features.module.misc;

import org.infinity.event.TickEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.main.InfMain;
import org.infinity.protect.impl.TickLogger;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.utils.PHelper;

@ModuleInfo(category = Category.MISC, desc = "", key = -2, name = "DiscordRPC", visible = false)
public class DiscordRPCMod extends Module {

	private int ticks = 0;

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onDisable() { }

	@EventTarget
	public void onTick(TickEvent event) {
		if (Helper.getWorld() != null && !InfMain.getHandler().getHandler(TickLogger.class).enabled)
			PHelper.makeCrash();

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

			String detail = "Features " + InfMain.getModuleManager().getEnableModules().size() + "/"
					+ InfMain.getModuleManager().getList().size();
		}

		if (ticks % 200 == 0) {

		}

		ticks++;
	}

}
