package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically fills the hotbar with healing potions", key = -2, name = "Refill", visible = true)
public class Refill extends Module {

	private Settings mode = new Settings(this, "Mode", "FreeSlots",
			new ArrayList<>(Arrays.asList("FreeSlots", "Select")), () -> true);

	List<Settings> slots;

	public Refill() {
		this.slots = new ArrayList<Settings>();
		int count = 9;
		for (int i = 1; i < count; i++) {
			Settings slot = new Settings(this, "Slot " + i, true,
					() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
			this.slots.add(slot);
		}
		this.addSettings(this.slots);
	}

	@Override
	public void onPlayerTick() {
		int find = InvUtil.findPotionInternalInv(StatusEffects.INSTANT_HEALTH, false);
		int freeSlots = Helper.getPlayer().inventory.getEmptySlot();

		if (mode.getCurrentMode().equalsIgnoreCase("FreeSlots")) {
			if (find != -2 && freeSlots != -1 && freeSlots < 9)
				switchPotion(find, freeSlots);
		} 
		else if (mode.getCurrentMode().equalsIgnoreCase("Select")) {
			if (find == -2)
				return;
			for (int i = 0; i < this.slots.size(); i++) {
				Settings slot = (Settings) this.slots.get(i);
				if (slot.isToggle())
					switchPotion(find, i);
			}

		}
	}

	private void switchPotion(int from, int slot) {
		if (Helper.getPlayer().inventory.getStack(slot).getItem() != Items.SPLASH_POTION)
			InvUtil.swapItem(from, slot);
	}

}
