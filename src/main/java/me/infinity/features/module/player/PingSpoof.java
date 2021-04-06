package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Creates fake lags in movements", key = -2, name = "PingSpoof", visible = true)
public class PingSpoof extends Module {

	private Settings delay = new Settings(this, "Delay", 10, 0, 3000, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(delay.getCurrentValueInt()));
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (Helper.minecraftClient.isInSingleplayer())
				return;
			
			if (event.getPacket() instanceof KeepAliveS2CPacket) {
			
				(new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep((long) delay.getCurrentValueInt());

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}		
			

		} else if (event.getType().equals(EventType.SEND)) {
			if (Helper.minecraftClient.isInSingleplayer())
				return;
			if (event.getPacket() instanceof KeepAliveC2SPacket) {	
			try {
				Thread.sleep((long) (delay.getCurrentValueInt()));
				
				Helper.sendPacket(new KeepAliveC2SPacket());
			} catch (InterruptedException exception) {
			}
			}
		}
	}

}
