package org.infinity.features.module.player;

import java.util.HashMap;
import java.util.Map.Entry;

import org.infinity.event.MotionEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.entity.EntityUtil;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically throws certain potions", key = -2, name = "AutoPotion", visible = true)
public class AutoPotion extends Module {

	HashMap<StatusEffect, Settings> potions;

	public AutoPotion() {
		this.potions = new HashMap<StatusEffect, Settings>();

		this.potions.put(StatusEffects.STRENGTH, new Settings(this, "Strength Potion", true, () -> true));
		this.potions.put(StatusEffects.SPEED, new Settings(this, "Speed Potion", true, () -> true));
		this.potions.put(StatusEffects.FIRE_RESISTANCE, new Settings(this, "Fire Resistance", true, () -> true));
		this.potions.put(StatusEffects.JUMP_BOOST, new Settings(this, "Jump Boost", false, () -> true));

		this.addSettings(this.potions.values());
	}

	private Settings delay = new Settings(this, "Delay", 5.2D, 3D, 20D, () -> true);

	private int timer;

	private int next;

	@Override
	public void onDisable() {
		Helper.sendPacket(new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, Helper.getPlayer().pitch,
				Helper.getPlayer().isOnGround()));
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			if (!Helper.getPlayer().isOnGround())
				return;

			int i = 1;
			for (Entry entry : this.potions.entrySet()) {
				StatusEffect effect = (StatusEffect) entry.getKey();
				Settings sett = (Settings) entry.getValue();
				int slot = InvUtil.findPotionHotbar(effect, true);

				if (sett.isToggle() && !EntityUtil.checkActivePotion(effect))
					next = i;
				if (timer > 0) {
					timer--;
					return;
				}

				if (sett.isToggle() && slot != -2 && !EntityUtil.checkActivePotion(effect)) {
					if (next == i) {
						baff(event, slot);
						next += 1;
					}
				}
				i++;
			}
		}
	}

	private void baff(MotionEvent event, int slot) {
		int preSlot = Helper.getPlayer().inventory.selectedSlot;
		float oldPitch = event.getPitch();
		event.setPitch(90);
		Helper.sendPacket(
				new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, 90, Helper.getPlayer().isOnGround()));

		if (event.getPitch() == 90) {
			Helper.getPlayer().inventory.selectedSlot = slot;
			Helper.minecraftClient.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(),
					Hand.MAIN_HAND);

			// set timer
			timer = (int) delay.getCurrentValueDouble();
		}

		// reset
		Helper.getPlayer().inventory.selectedSlot = preSlot;
		Helper.sendPacket(new PlayerMoveC2SPacket.LookOnly(Helper.getPlayer().yaw, Helper.getPlayer().pitch,
				Helper.getPlayer().isOnGround()));
		event.setPitch(oldPitch);
	}

}
