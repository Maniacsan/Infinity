package org.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infinity.event.PacketEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Creates fake lags in movements", key = -2, name = "FakeLags", visible = true)
public class FakeLags extends Module {

	private Settings mode = new Settings(this, "Mode", "Pulse",
			new ArrayList<>(Arrays.asList("Pulse", "Always", "Legit")), () -> true);

	private Settings pulseDelay = new Settings(this, "Pulse Delay", 8, 0.0, 30.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Pulse"));

	private Settings delay = new Settings(this, "Delay", 0.2, 0.0, 30.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Always"));

	private Settings legitDelay = new Settings(this, "Delay", 2.1, 0.0, 30.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Legit"));

	// legit
	private double legitTicks;

	// pulse
	private List<Packet<?>> packetList;
	private double pulseTicks;

	// always
	private PlayerMoveC2SPacket.Both bothPacket;
	private double sendTimer;
	private boolean sendPackets;
	private double ticks;

	@Override
	public void onDisable() {
		sendPackets();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Pulse")) {
			if (Helper.getPlayer().isDead())
				return;

			if (pulseTicks >= pulseDelay.getCurrentValueDouble()) {
				try {
					for (Packet<?> unsentPacket : this.packetList)
						Helper.sendPacket(unsentPacket);
					packetList.clear();
					pulseTicks = 0;
				} catch (Exception exception) {
				}
			}
		} else if (mode.getCurrentMode().equalsIgnoreCase("Legit")) {
			if (Helper.getPlayer().isDead() || Helper.minecraftClient.isInSingleplayer())
				return;

			if (legitTicks >= legitDelay.getCurrentValueDouble()) {
				try {
					for (Packet<?> unsentPacket : this.packetList)
						Helper.sendPacket(unsentPacket);
				} catch (Exception exception) {
				}
				packetList.clear();
				legitTicks = 0;
			}
		}

		if (sendPackets) {
			if (sendTimer <= 0) {
				sendPackets = false;

				if (bothPacket == null) {
					ticks = 0;
					return;
				}
				Helper.minecraftClient.getNetworkHandler().sendPacket(bothPacket);

				ticks = 0;
				bothPacket = null;
			} else {
				sendTimer--;
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Legit")) {
				if (event.getPacket() instanceof PlayerMoveC2SPacket) {

					if (Helper.getPlayer().isDead())
						return;

					packetList.add(event.getPacket());
					event.cancel();
					legitTicks++;
				}

			} else if (mode.getCurrentMode().equalsIgnoreCase("Pulse")) {
				if (event.getPacket() instanceof PlayerMoveC2SPacket) {

					if (Helper.getPlayer().isDead() || pulseTicks != 0)
						return;

					packetList.add(event.getPacket());
					event.cancel();
					pulseTicks++;
				}

			} else if (mode.getCurrentMode().equalsIgnoreCase("Always")) {
				if (event.getPacket() instanceof PlayerMoveC2SPacket.Both) {

					if (ticks > 0)
						return;

					ticks++;
					bothDelay(event);
				}
			}
		}
	}

	public void sendPackets() {
		if (packetList != null) {
			try {
				for (Packet<?> unsentPacket : this.packetList)
					Helper.minecraftClient.getNetworkHandler().sendPacket(unsentPacket);
			} catch (Exception exception) {
			}
			this.packetList.clear();
		}
		ticks = 0;
		pulseTicks = 0;
		legitTicks = 0;

	}

	private void bothDelay(PacketEvent event) {
		if (!sendPackets) {
			sendPackets = true;
			sendTimer = this.delay.getCurrentValueDouble();
			bothPacket = (PlayerMoveC2SPacket.Both) event.getPacket();

			event.cancel();
		}
	}

}
