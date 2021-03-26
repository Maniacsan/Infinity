package me.infinity.features.module.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.ClickEvent;
import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.entity.PlayerSend;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Hit a player from a long distance", key = -2, name = "Reach", visible = true)
public class Reach extends Module {

	public Settings reach = new Settings(this, "Range", 4.1D, 0D, 6.0D, () -> true);

	public Settings packetSpoof = new Settings(this, "Packets Spoof", true, () -> true);
	private double x = 0, y , z = 0;

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(reach.getCurrentValueDouble()));
	}

	@EventTarget
	public void onClick(ClickEvent event) {

		EntityUtil.updateTargetRaycast(Helper.minecraftClient.targetedEntity, reach.getCurrentValueDouble(),
				Helper.getPlayer().yaw, Helper.getPlayer().pitch);

	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			Entity e = Helper.minecraftClient.targetedEntity;
			if (packetSpoof.isToggle()) {
				if (e == null)
					return;
				
				if (Helper.minecraftClient.options.keyAttack.isPressed()) {
					if (Helper.getPlayer().distanceTo(e) > 3.5) {
						
						x = Helper.getPlayer().getX();
						z = Helper.getPlayer().getZ();
						double entx = e.getX() - 3.5D * Math.cos(Math.toRadians((RotationUtils.getYaw(e) + 90.0F)));
						double entz = e.getZ() - 3.5D * Math.sin(Math.toRadians((RotationUtils.getYaw(e) + 90.0F)));
						
						PlayerSend.setX(entx);
						PlayerSend.setZ(entz);
					}
				}
			}
		} else if (event.getType().equals(EventType.POST)) {

		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (packetSpoof.isToggle()) {
			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket
					&& ((PlayerInteractEntityC2SPacket) event.getPacket())
							.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
				PacketUtil.cancelServerKeepAlive(event);
				PacketUtil.cancelKeepAlive(event);
			}
		}
	}
}
