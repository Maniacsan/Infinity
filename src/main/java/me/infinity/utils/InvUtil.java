package me.infinity.utils;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.SlotActionType;

public class InvUtil {

	// Find item on full inventory
	public static int findItemFullInv(Item item) {
		int find = -2;
		for (int i = 0; i <= 44; i++) {
			if (Helper.getPlayer().inventory.getStack(i).getItem() == item) {
				find = i;
			}
		}
		return find;
	}

	// Find item only on hotbat
	public static int findItemOnHotbar(Item item) {
		int find = -2;
		for (int i = 0; i <= 8; i++) {
			if (Helper.getPlayer().inventory.getStack(i).getItem() == item) {
				find = i;
			}
		}
		return find;
	}

	// Find item only internal inventory , no hotbar
	public static int findItemInternalInv(Item item) {
		int find = -2;
		for (int i = 9; i <= 44; i++) {
			if (Helper.getPlayer().inventory.getStack(i).getItem() == item) {
				find = i;
			}
		}
		return find;
	}

	// Find item only internal potion , no hotbar
	public static int findPotionInternalInv(StatusEffect effect) {
		int find = -2;
		for (int i = 9; i <= 44; i++) {
			ItemStack stack = Helper.getPlayer().inventory.getStack(i);
			if (stack.getItem() != Items.SPLASH_POTION)
				return -2;
				if (hasEffect(stack, effect))
					find = i;

		}
		return find;
	}

	public static int checkFreeHotbatSlots() {
		int slot = -2;
		for (int i = 0; i <= 8; i++) {
			if (Helper.getPlayer().inventory.getStack(i).getItem() == null) {
				slot = i;
			}
		}
		return slot;
	}

	public static boolean hasEffect(ItemStack stack, StatusEffect effect) {
		for (StatusEffectInstance effectInstance : PotionUtil.getPotionEffects(stack)) {
			if (effectInstance.getEffectType() != effect)
				continue;

			return true;
		}

		return false;
	}

	public static void switchItem(int slot, int button) {
		Helper.minecraftClient.interactionManager.clickSlot(button, slot, 1, SlotActionType.PICKUP, Helper.getPlayer());
	}

}
