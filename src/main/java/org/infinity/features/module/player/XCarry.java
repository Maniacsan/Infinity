package org.infinity.features.module.player;

import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.mixin.ICloseHandledScreenC2SPacket;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@ModuleInfo(category = Category.PLAYER, desc = "Does not allow items that are in the crafting slot to drop ", key = -2, name = "XCarry", visible = true)
public class XCarry extends Module {

	private boolean active;

	@Override
	public void onEnable() {
		active = false;
	}

	@Override
	public void onDisable() {
		if (active)
			Helper.sendPacket(new CloseHandledScreenC2SPacket(Helper.getPlayer().playerScreenHandler.syncId));
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (!event.getType().equals(EventType.SEND))
			return;

		if (event.getPacket() instanceof CloseHandledScreenC2SPacket
				&& ((ICloseHandledScreenC2SPacket) event.getPacket())
						.getSyncId() == Helper.getPlayer().playerScreenHandler.syncId) {
			active = true;
			event.cancel();
		}
	}

}
