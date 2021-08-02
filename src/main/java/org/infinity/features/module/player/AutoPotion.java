package org.infinity.features.module.player;

import java.util.HashMap;
import java.util.Map.Entry;

import org.infinity.event.MotionEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.entity.EntityUtil;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(category = Category.PLAYER, desc = "Automatically throws certain potions", key = -2, name = "AutoPotion", visible = true)
public class AutoPotion extends Module {

	HashMap<StatusEffect, Setting> potions;

	public AutoPotion() {
		this.potions = new HashMap<StatusEffect, Setting>();

		this.potions.put(StatusEffects.STRENGTH, new Setting(this, "Strength Potion", true));
		this.potions.put(StatusEffects.SPEED, new Setting(this, "Speed Potion", true));
		this.potions.put(StatusEffects.FIRE_RESISTANCE, new Setting(this, "Fire Resistance", true));
		this.potions.put(StatusEffects.JUMP_BOOST, new Setting(this, "Jump Boost", false));

		this.addSettings(this.potions.values());
	}

	private Setting delay = new Setting(this, "Delay", 5.2D, 3D, 20D);

	private int timer;

	private int next;

	@Override
	public void onDisable() {
		Helper.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(Helper.getPlayer().getYaw(), Helper.getPlayer().getPitch(),
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
				Setting sett = (Setting) entry.getValue();
				int slot = InvUtil.findPotionHotbar(effect, true);

				if (sett.isToggle() && !EntityUtil.checkActivePotion(effect))
					next = i;
				if (timer > 0) {
					timer--;
					return;
				}

				if (sett.isToggle() && slot != -2 && !EntityUtil.checkActivePotion(effect)) {
					if (next == i) {
						baff(slot);
						next += 1;
					}
				}
				i++;
			}
		}
	}

	private synchronized void baff(int slot) {
		int preSlot = Helper.getPlayer().getInventory().selectedSlot;
		Helper.sendPacket(
				new PlayerMoveC2SPacket.LookAndOnGround(Helper.getPlayer().getYaw(), 90, Helper.getPlayer().isOnGround()));

			Helper.getPlayer().getInventory().selectedSlot = slot;
			Helper.MC.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(),
					Hand.MAIN_HAND);

			// set timer
			timer = (int) delay.getCurrentValueDouble();

		// reset
		Helper.getPlayer().getInventory().selectedSlot = preSlot;
		Helper.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(Helper.getPlayer().getYaw(), Helper.getPlayer().getPitch(),
				Helper.getPlayer().isOnGround()));
	}

}
