package me.infinity.features.module.player;

import java.util.HashMap;
import java.util.Map.Entry;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.entity.EntityUtil;
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
		this.potions.put(StatusEffects.FIRE_RESISTANCE, new Settings(this, "Fire Resistance", false, () -> true));
		this.potions.put(StatusEffects.JUMP_BOOST, new Settings(this, "Jump Boost", false, () -> true));
		
		this.addSettings(this.potions.values());
	}

	private Settings delay = new Settings(this, "Delay", 1D, 0D, 20D, () -> true);

	private int timer;

	private int next;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			
			if (!Helper.getPlayer().isOnGround())
				return;
			
			int i = 1;
			for(Entry entry : this.potions.entrySet()) {
				StatusEffect effect = (StatusEffect)entry.getKey();
				Settings sett = (Settings)entry.getValue();
				int slot = InvUtil.findPotionHotbar(effect, true);
				
				if(sett.isToggle() && !EntityUtil.checkActivePotion(effect))
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
