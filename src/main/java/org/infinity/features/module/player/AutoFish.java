package org.infinity.features.module.player;

import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.mixin.IMinecraftClient;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

@ModuleInfo(category = Category.PLAYER, desc = "Automatically uses the fishing rod when a fish hits and resets it back", key = -2, name = "AutoFish", visible = true)
public class AutoFish extends Module {

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof PlaySoundS2CPacket) {
				PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket) event.getPacket();

				if (soundPacket.getSound().getId().getPath().equals("entity.fishing_bobber.splash"))
					useFish();
			}
		}
	}

	private void useFish() {
		new Thread() {

			@Override
			public void run() {
				try {
					((IMinecraftClient) Helper.MC).rightClick();
					Thread.sleep(300);
					((IMinecraftClient) Helper.MC).rightClick();
				} catch (Exception exception) {

				}
			}
		}.start();
	}
}
