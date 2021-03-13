package me.infinity.utils;

import net.minecraft.entity.EquipmentSlot;
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

	// Find item only internal inv potion , no hotbar
	public static int findPotionInternalInv(StatusEffect effect) {
		for (int i = 9; i <= 44; i++) {
			ItemStack stack = Helper.getPlayer().inventory.getStack(i);
			if (stack.getItem() != Items.SPLASH_POTION)
				continue;
			if (hasEffect(stack, effect))
				return i;

		}
		return -2;
	}

	// Find item from hotbar potion
	public static int findPotionHotbar(StatusEffect effect) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = Helper.getPlayer().inventory.getStack(i);
			if (stack.getItem() != Items.SPLASH_POTION)
				continue;
			if (hasEffect(stack, effect))
				return i;
		}
		return -2;
	}

	public static boolean checkArmorEmpty(EquipmentSlot slot) {
		return Helper.getPlayer().inventory.getArmorStack(slot.getEntitySlotId()).isEmpty();
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
	
	public static void swapItem(int from, int slot) {
		Helper.minecraftClient.interactionManager.clickSlot(Helper.getPlayer().currentScreenHandler.syncId, from,
				slot, SlotActionType.SWAP, Helper.getPlayer());
	}

}
