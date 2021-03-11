package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

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

	// slots , I very big brain with this shit code
	private Settings slot1 = new Settings(this, "Slot 1", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot2 = new Settings(this, "Slot 2", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot3 = new Settings(this, "Slot 3", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot4 = new Settings(this, "Slot 4", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot5 = new Settings(this, "Slot 5", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot6 = new Settings(this, "Slot 6", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot7 = new Settings(this, "Slot 7", true, () -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot8 = new Settings(this, "Slot 8", true, () -> mode.getCurrentMode().equalsIgnoreCase("Select"));
	private Settings slot9 = new Settings(this, "Slot 9", true, () -> mode.getCurrentMode().equalsIgnoreCase("Select"));

	@Override
	public void onPlayerTick() {
		int find = InvUtil.findPotionInternalInv(StatusEffects.INSTANT_HEALTH);
		int freeSlots = InvUtil.checkFreeHotbatSlots();

		if (mode.getCurrentMode().equalsIgnoreCase("FreeSlots")) {
			if (find != -2 && freeSlots != -2) {
				switchPotion(find, freeSlots);
			}
		} else if (mode.getCurrentMode().equalsIgnoreCase("Select")) {

			// 100000IQ Senior code
			if (find != -2) {
				if (slot1.isToggle()) {
					switchPotion(find, 0);

				} else if (slot2.isToggle()) {
					switchPotion(find, 1);

				} else if (slot3.isToggle()) {
					switchPotion(find, 2);

				} else if (slot4.isToggle()) {
					switchPotion(find, 3);

				} else if (slot5.isToggle()) {
					switchPotion(find, 4);

				} else if (slot6.isToggle()) {
					switchPotion(find, 5);

				} else if (slot7.isToggle()) {
					switchPotion(find, 6);

				} else if (slot8.isToggle()) {
					switchPotion(find, 7);

				} else if (slot9.isToggle()) {
					switchPotion(find, 8);

				}
			}
		}
	}

	private void switchPotion(int from, int slot) {
		if (Helper.getPlayer().inventory.getStack(slot).getItem() != Items.SPLASH_POTION) {
			InvUtil.switchItem(from, 1);
			InvUtil.switchItem(slot, 1);
		}
	}

}
