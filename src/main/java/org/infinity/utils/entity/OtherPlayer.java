package org.infinity.utils.entity;

import org.infinity.utils.Helper;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class OtherPlayer extends OtherClientPlayerEntity {

	public OtherPlayer() {
		this(Helper.getPlayer());
	}

	public OtherPlayer(PlayerEntity player) {
		this(player, player.getX(), player.getY(), player.getZ());
	}

	public OtherPlayer(PlayerEntity player, double x, double y, double z) {
		super(Helper.getWorld(), player.getGameProfile());
		copyPositionAndRotation(player);
		yaw = headYaw = bodyYaw = player.yaw;
		inventory.main.set(inventory.selectedSlot, player.getMainHandStack());
		inventory.offHand.set(0, player.getOffHandStack());
		inventory.armor.set(0, player.inventory.armor.get(0));
		inventory.armor.set(1, player.inventory.armor.get(1));
		inventory.armor.set(2, player.inventory.armor.get(2));
		inventory.armor.set(3, player.inventory.armor.get(3));
	}

	public void spawn() {
		Helper.getWorld().addEntity(this.getEntityId(), this);
	}

	public void despawn() {
		Helper.getWorld().removeEntity(this.getEntityId());
	}

}
