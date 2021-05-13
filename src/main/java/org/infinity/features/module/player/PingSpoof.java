package org.infinity.features.module.player;

import org.apache.commons.lang3.RandomUtils;
import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IKeepAliveC2SPacket;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

@ModuleInfo(category = Category.PLAYER, desc = "Sends KeepAlives packets increasing your ping", key = -2, name = "PingSpoof", visible = true)
public class PingSpoof extends Module {

	private Setting delay = new Setting(this, "Delay", 1000, 100, 4000);
	private Setting idSpoof = new Setting(this, "ID Spoof", true);

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
				KeepAliveC2SPacket kp = (KeepAliveC2SPacket) event.getPacket();
				
				if (idSpoof.isToggle()) {
					((IKeepAliveC2SPacket)event.getPacket()).setID(RandomUtils.nextInt(1000, 239812094));
				}
				
				try {
					Thread.sleep((long) delay.getCurrentValueInt());
				} catch (InterruptedException exception) {
				}
				
			}
		}
	}

}
