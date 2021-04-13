package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
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
