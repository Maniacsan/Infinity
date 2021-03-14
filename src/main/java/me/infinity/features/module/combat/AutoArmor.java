package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author Alexander01998
 *
 */
@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically puts on the best armor", key = -2, name = "AutoArmor", visible = true)
public class AutoArmor extends Module {

	private Settings onInv = new Settings(this, "On Open Inventory", false, () -> true);

	private Settings delay = new Settings(this, "Delay", 1.0D, 0D, 15.0D, () -> true);

	private int currentProt;
	private int delayLeft = (int) delay.getCurrentValueDouble();

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().abilities.creativeMode)
			return;
		if (onInv.isToggle() && !(Helper.minecraftClient.currentScreen instanceof InventoryScreen))
			return;
		if (delayLeft > 0) {
			delayLeft--;
			return;
		} else {
			delayLeft = (int) delay.getCurrentValueDouble();
		}
		ItemStack itemStack;
		for (int a = 0; a < 4; a++) {
			itemStack = Helper.getPlayer().inventory.getArmorStack(a);
			currentProt = 0;
			if (EnchantmentHelper.hasBindingCurse(itemStack))
				continue;
			if (itemStack.getItem() instanceof ArmorItem) {
				getCurrentScore(itemStack);
			}
			int bestSlot = -1;
			int bestScore = 0;
			for (int i = 0; i < 36; i++) {
				ItemStack stack = Helper.getPlayer().inventory.getStack(i);
				if (stack.getItem() instanceof ArmorItem
						&& (((ArmorItem) stack.getItem()).getSlotType().getEntitySlotId() == a)) {
					int temp = getItemScore(stack);
					if (bestScore < temp) {
						bestScore = temp;
						bestSlot = i;
					}
				}
			}
			if (bestSlot > -1) {
				InvUtil.quickItem(8 - a);
				InvUtil.quickItem(bestSlot);
			}
		}
	}

	private int getItemScore(ItemStack itemStack) {
		int score = 0;
		score += 2 * (EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack) - currentProt);
		if ((EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack)) > 0)
			score += 2;
		return score;
	}

	private void getCurrentScore(ItemStack itemStack) {
		currentProt = EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack);
	}
}