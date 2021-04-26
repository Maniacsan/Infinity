package org.infinity.features.module.player;

import org.infinity.event.PacketEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.MathAssist;
import org.infinity.utils.TimeHelper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.ScreenHandler;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically takes things from the chest", key = -2, name = "ChestSteal", visible = true)
public class ChestSteal extends Module {

	public Settings maxDelay = new Settings(this, "Max Delay", 160.0D, 10D, 300D, () -> true);
	public Settings minDelay = new Settings(this, "Min Delay", 90.0D, 10D, 300D, () -> true);

	private Settings autoClose = new Settings(this, "Auto Close", false, () -> true);

	private TimeHelper timer = new TimeHelper();
	private InventoryS2CPacket packet;

	public double delay;

	@Override
	public void onEnable() {
		delay = MathAssist.random(minDelay.getCurrentValueDouble(), maxDelay.getCurrentValueDouble());
	}

	@Override
	public void onDisable() {
		packet = null;
	}

	@Override
	public void onPlayerTick() {
		if (packet != null && Helper.getPlayer().currentScreenHandler.syncId == packet.getSyncId()) {
			if (Helper.minecraftClient.currentScreen instanceof ShulkerBoxScreen
					|| Helper.minecraftClient.currentScreen instanceof GenericContainerScreen) {
				if (!isContainerEmpty(Helper.getPlayer().currentScreenHandler)) {
					for (int i = 0; i < Helper.getPlayer().currentScreenHandler.slots.size() - 36; ++i) {
						net.minecraft.screen.slot.Slot slot = Helper.getPlayer().currentScreenHandler.getSlot(i);
						if (slot.hasStack() && slot.getStack() != null) {

							if (timer.hasReached(delay)) {
								quickItem(i);
							}
						}
					}
				}
			}

			if (isContainerEmpty(Helper.getPlayer().currentScreenHandler)) {
				if (autoClose.isToggle())
					Helper.getPlayer().closeScreen();
				if (packet != null)
					packet = null;
			}
		}
	}

	private void quickItem(int slot) {
		InvUtil.quickItem(slot);

		timer.reset();
		delay = MathAssist.random(minDelay.getCurrentValueDouble(), maxDelay.getCurrentValueDouble());
	}

	/* GishCode */
	private boolean isContainerEmpty(ScreenHandler container) {
		boolean temp = true;
		int i = 0;
		for (int slotAmount = container.slots.size() == 90 ? 54 : 27; i < slotAmount; i++) {
			if (container.getSlot(i).hasStack()) {
				temp = false;
			}
		}
		return temp;
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof InventoryS2CPacket) {
				packet = (InventoryS2CPacket) event.getPacket();
			}
		}
	}

}