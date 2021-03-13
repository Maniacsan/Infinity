package me.infinity.features.module.player;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically throws certain potions", key = -2, name = "AutoPotion", visible = true)
public class AutoPotion extends Module {

	private Settings strength = new Settings(this, "Strength Potion", true, () -> true);
	private Settings speed = new Settings(this, "Speed Potion", true, () -> true);
	private Settings fire = new Settings(this, "Fire Resistance", false, () -> true);
	private Settings jump = new Settings(this, "Jump Boost", false, () -> true);

	private int next;

	@Override
	public void onPlayerTick() {
		int strSlot = InvUtil.findPotionHotbar(StatusEffects.STRENGTH);
		int speedSlot = InvUtil.findPotionHotbar(StatusEffects.SPEED);
		int fireSlot = InvUtil.findPotionHotbar(StatusEffects.FIRE_RESISTANCE);
		int jumpSlot = InvUtil.findPotionHotbar(StatusEffects.JUMP_BOOST);

		if (!EntityUtil.checkActivePotion(StatusEffects.STRENGTH)) {
			next = 0;
		} else
			next = 10;

		// Na normalnuyu nasledstvennost vremeni netu izuchat
		// A tak 10000iq code

		if (strength.isToggle() && strSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.STRENGTH)) {
			if (next == 0) {
				baff(strSlot);
				next += 1;
			}
		} else {
			next += 1;
		}
		if (speed.isToggle() && speedSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.SPEED)) {
			if (next == 1) {
				baff(speedSlot);
				next += 1;
			}
		} else {
			next += 1;
		}

		if (fire.isToggle() && fireSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.FIRE_RESISTANCE)) {
			if (next == 2) {
				baff(fireSlot);
				next += 1;
			}
		} else {
			next += 1;
		}

		if (jump.isToggle() && jumpSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.JUMP_BOOST)) {
			if (next == 3) {
				baff(jumpSlot);
				next = 10;
			}
		} else {
			next = 10;
		}

	}

	private void baff(int slot) {
		int preSlot = Helper.getPlayer().inventory.selectedSlot;
		Helper.getPlayer().inventory.selectedSlot = slot;
		Helper.sendPacket(
				new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, 90.0F, Helper.getPlayer().isOnGround()));
		Helper.minecraftClient.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(), Hand.MAIN_HAND);

		// reset
		Helper.getPlayer().inventory.selectedSlot = preSlot;
		Helper.sendPacket(new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, Helper.getPlayer().pitch,
				Helper.getPlayer().isOnGround()));
	}

}
