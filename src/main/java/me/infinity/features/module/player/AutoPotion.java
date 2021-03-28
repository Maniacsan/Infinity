package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically throws certain potions", key = -2, name = "AutoPotion", visible = true)
public class AutoPotion extends Module {

	private Settings strength = new Settings(this, "Strength Potion", true, () -> true);
	private Settings speed = new Settings(this, "Speed Potion", true, () -> true);
	private Settings fire = new Settings(this, "Fire Resistance", false, () -> true);
	private Settings jump = new Settings(this, "Jump Boost", false, () -> true);

	private Settings delay = new Settings(this, "Delay", 1D, 0D, 20D, () -> true);

	private int timer;

	private int next;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			int strSlot = InvUtil.findPotionHotbar(StatusEffects.STRENGTH);
			int speedSlot = InvUtil.findPotionHotbar(StatusEffects.SPEED);
			int fireSlot = InvUtil.findPotionHotbar(StatusEffects.FIRE_RESISTANCE);
			int jumpSlot = InvUtil.findPotionHotbar(StatusEffects.JUMP_BOOST);

			if (speed.isToggle() && !EntityUtil.checkActivePotion(StatusEffects.SPEED)) {
				next = 1;
			}

			if (fire.isToggle() && !EntityUtil.checkActivePotion(StatusEffects.FIRE_RESISTANCE)) {
				next = 2;
			}

			if (jump.isToggle() && !EntityUtil.checkActivePotion(StatusEffects.JUMP_BOOST)) {
				next = 3;
			}

			if (strength.isToggle() && !EntityUtil.checkActivePotion(StatusEffects.STRENGTH)) {
				next = 4;
			}

			// ground check
			if (!Helper.getPlayer().isOnGround())
				return;

			// Na normalnuyu nasledstvennost vremeni netu izuchat
			// A tak 10000iq code

			if (timer > 0) {
				timer--;
				return;
			}

			if (speed.isToggle() && speedSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.SPEED)) {
				if (next == 1) {
					baff(event, speedSlot);
					next += 1;
				}
			}

			if (timer > 0) {
				timer--;
				return;
			}

			if (fire.isToggle() && fireSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.FIRE_RESISTANCE)) {
				if (next == 2) {
					baff(event, fireSlot);
					next += 1;
				}
			}

			if (timer > 0) {
				timer--;
				return;
			}

			if (jump.isToggle() && jumpSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.JUMP_BOOST)) {
				if (next == 3) {
					baff(event, jumpSlot);
					next += 1;
				}
			}

			if (timer > 0) {
				timer--;
				return;
			}

			if (strength.isToggle() && strSlot != -2 && !EntityUtil.checkActivePotion(StatusEffects.STRENGTH)) {
				if (next == 4) {
					event.setPitch(90);
					baff(event, strSlot);
					next += 1;
				}
			}
		}
	}

	private void baff(MotionEvent event, int slot) {
		int preSlot = Helper.getPlayer().inventory.selectedSlot;
		Helper.sendPacket(
				new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, 90, Helper.getPlayer().isOnGround()));
		Helper.getPlayer().inventory.selectedSlot = slot;
		Helper.minecraftClient.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(), Hand.MAIN_HAND);

		// set timer
		timer = (int) delay.getCurrentValueDouble();

		// reset
		Helper.getPlayer().inventory.selectedSlot = preSlot;
		Helper.sendPacket(new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, Helper.getPlayer().pitch,
				Helper.getPlayer().isOnGround()));
	}

}
