package org.infinity.features.module.combat;

import org.infinity.event.ClickEvent;
import org.infinity.event.PacketEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.PacketUtil;
import org.infinity.utils.entity.EntityUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Hit a player from a long distance", key = -2, name = "Reach", visible = true)
public class Reach extends Module {

	public Settings maxReach = new Settings(this, "Max Range", 4.1D, 0D, 6.0D, () -> true);
	public Settings minReach = new Settings(this, "Min Range", 3.8D, 0D, 6.0D, () -> true);

	public Settings packetSpoof = new Settings(this, "Packets Spoof", true, () -> true);

	private double reach;
	private double lastReach = 0;
	
	@Override
	public void onEnable() {
		lastReach = 0;
	}

	@Override
	public void onPlayerTick() {
		reach = MathAssist.random(minReach.getCurrentValueDouble(), maxReach.getCurrentValueDouble());

		setSuffix(String.valueOf(MathAssist.round(lastReach, 1)));
	}

	@EventTarget
	public void onClick(ClickEvent event) {

		EntityUtil.updateTargetRaycast(Helper.minecraftClient.targetedEntity, reach, Helper.getPlayer().yaw,
				Helper.getPlayer().pitch);

	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (packetSpoof.isToggle()) {
			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket
					&& ((PlayerInteractEntityC2SPacket) event.getPacket())
							.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
				lastReach = Helper.getPlayer()
						.distanceTo(((PlayerInteractEntityC2SPacket) event.getPacket()).getEntity(Helper.getWorld()));
				PacketUtil.cancelKeepAlive(event);
			}
		}
	}
}
