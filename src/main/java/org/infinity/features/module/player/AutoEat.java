package org.infinity.features.module.player;

import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.mixin.IKeyBinding;
import org.infinity.utils.Helper;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically eats food when there is a certain hunger", key = -2, name = "AutoEat", visible = true)
public class AutoEat extends Module {

	private Settings hunger = new Settings(this, "Hunger", 19, 0, 19, () -> true);

	private int lastSlot = -1;
	private boolean eating = false;

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().abilities.creativeMode)
			return;
		if (eating && !Helper.getPlayer().isUsingItem()) {
			if (lastSlot != -1) {
				Helper.getPlayer().inventory.selectedSlot = lastSlot;
				lastSlot = -1;
			}
			KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyUse).getBoundKey(), false);
			eating = false;
			return;
		}

		if (eating)
			return;

		if (Helper.getPlayer().getOffHandStack().getItem().getGroup() == ItemGroup.FOOD) {
			if (Helper.getPlayer().getHungerManager().getFoodLevel() <= hunger.getCurrentValueInt()) {
				eating = true;
				use();
			}

		} else {
			if (Helper.getPlayer().getHungerManager().getFoodLevel() <= hunger.getCurrentValueInt()) {
				int foodSlot = findFood();
				if (Helper.getPlayer().getMainHandStack().getItem().getGroup() == ItemGroup.FOOD) {
					eating = true;
					use();
				} else {

					if (foodSlot != -2) {
						lastSlot = Helper.getPlayer().inventory.selectedSlot;
						Helper.getPlayer().inventory.selectedSlot = foodSlot;
						if (Helper.getPlayer().getMainHandStack().getItem().getGroup() == ItemGroup.FOOD) {
							eating = true;
							use();
						}
					}

				}
			}
		}
	}

	private void use() {
		KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyUse).getBoundKey(), true);
		Helper.minecraftClient.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(), Hand.MAIN_HAND);
	}

	private int findFood() {
		for (int i = 0; i <= 8; i++) {
			ItemStack stack = Helper.getPlayer().inventory.getStack(i);
			if (hasFood(stack))
				return i;
		}
		return -2;
	}

	private boolean hasFood(ItemStack stack) {
		if (stack.getItem().getGroup() == ItemGroup.FOOD) {
	        if (stack.getItem() == Items.ROTTEN_FLESH) return false;
	        if (stack.getItem() == Items.SPIDER_EYE) return false;
	        if (stack.getItem() == Items.POISONOUS_POTATO) return false;
			return true;
		}
		return false;
	}

}
