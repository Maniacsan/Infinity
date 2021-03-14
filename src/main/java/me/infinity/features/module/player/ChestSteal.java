package me.infinity.features.module.player;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.MathAssist;
import me.infinity.utils.TimeHelper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically takes things from the chest", key = -2, name = "ChestSteal", visible = true)
public class ChestSteal extends Module {

	public Settings maxDelay = new Settings(this, "Max Delay", 40.0D, 0D, 190D, () -> true);
	public Settings minDelay = new Settings(this, "Min Delay", 10.0D, 0D, 190D, () -> true);

	private double getDelay() {
		return MathAssist.random(minDelay.getCurrentValueDouble(), maxDelay.getCurrentValueDouble());
	}

	private int getRows(ScreenHandler handler) {
		return (handler instanceof GenericContainerScreenHandler ? ((GenericContainerScreenHandler) handler).getRows()
				: 3);
	}

	private void moveSlots(ScreenHandler handler, int start, int end) {
		for (int i = start; i < end; i++) {
			if (!handler.getSlot(i).hasStack())
				continue;

			int sleep = (int) getDelay();
			if (sleep > 0)
				TimeHelper.sleep(sleep);

			if (Helper.minecraftClient.currentScreen == null)
				break;

			InvUtil.quickItem(i);
		}
	}

	public void steal(ScreenHandler handler) {
		TimeHelper.runInThread(() -> moveSlots(handler, 0, getRows(handler) * 9));
	}

}
