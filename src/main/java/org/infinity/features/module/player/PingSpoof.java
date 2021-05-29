package org.infinity.features.module.player;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.infinity.event.PacketEvent;
import org.infinity.event.TickEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IKeepAliveC2SPacket;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

@ModuleInfo(category = Category.PLAYER, desc = "Sends KeepAlives packets increasing your ping", key = -2, name = "PingSpoof", visible = true)
public class PingSpoof extends Module {

	private Setting delay = new Setting(this, "Delay", 20000, 100, 25000);
	private Setting idSpoof = new Setting(this, "ID Spoof", true);

	private List<Packet<?>> packetList = new ArrayList<>();
	private Timer timer = new Timer();

	private long id;

	@Override
	public void onDisable() {
		packetList.clear();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(delay.getCurrentValueInt()));
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (packetList.isEmpty())
			return;

		if (timer.hasReached(delay.getCurrentValueDouble() * 6)) {
			for (Packet<?> packet : packetList) {
				id = ((KeepAliveC2SPacket) packet).getId();
				Helper.sendPacket(packet);
				packetList.clear();
			}
			timer.reset();
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {

			if (Helper.minecraftClient.isInSingleplayer() || Helper.getPlayer().getHealth() <= 0)
				return;

			if (event.getPacket() instanceof KeepAliveC2SPacket
					&& ((KeepAliveC2SPacket) event.getPacket()).getId() != id) {

				if (idSpoof.isToggle()) {
					((IKeepAliveC2SPacket) event.getPacket()).setID(RandomUtils.nextInt(1000, 239812));
				}
				event.cancel();
				packetList.add(((KeepAliveC2SPacket) event.getPacket()));
			}
		}
	}

}
