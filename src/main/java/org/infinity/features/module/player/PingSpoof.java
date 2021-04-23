package org.infinity.features.module.player;

import org.infinity.event.PacketEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Sends KeepAlives packets increasing your ping", key = -2, name = "PingSpoof", visible = true)
public class PingSpoof extends Module {

	private Settings delay = new Settings(this, "Delay", 1000, 100, 4000, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(delay.getCurrentValueInt()));
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (Helper.minecraftClient.isInSingleplayer() || !Helper.getPlayer().isAlive())
				return;

			if (event.getPacket() instanceof KeepAliveC2SPacket) {
				try {
					Thread.sleep((long) delay.getCurrentValueInt());
				} catch (InterruptedException exception) {
				}
			}
		}
	}

}
