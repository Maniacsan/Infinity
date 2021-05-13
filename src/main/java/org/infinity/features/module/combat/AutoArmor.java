package org.infinity.features.module.combat;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author Alexander01998
 *
 */
@ModuleInfo(category = Category.COMBAT, desc = "Automatically puts on the best armor", key = -2, name = "AutoArmor", visible = true)
public class AutoArmor extends Module {

	private Setting onInv = new Setting(this, "On Open Inventory", false);

	private Setting delay = new Setting(this, "Delay", 1.0D, 0D, 20.0D);

	private int currentProt, currentBlast, currentFire, currentProj, currentArmour, currentUnbreaking;
	private float currentToughness = 0;
	private int tickDelay = 0;
	
	@Override
	public void onEnable() {
		tickDelay = (int) delay.getCurrentValueDouble();
	}

	@Override
	public void onPlayerTick() {	

		if (onInv.isToggle() && !(Helper.minecraftClient.currentScreen instanceof HandledScreen))
			return;

		ItemStack itemStack;
		for (int a = 0; a < 4; a++) {
			itemStack = Helper.getPlayer().inventory.getArmorStack(a);
			currentProt = 0;
			currentBlast = 0;
			currentFire = 0;
			currentProj = 0;
			currentArmour = 0;
			currentToughness = 0;
			currentUnbreaking = 0;
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
			
			if (tickDelay > 0) {
				tickDelay--;
				return;
			}
			
			if (bestSlot > -1) {
				InvUtil.quickItem(8 - a);
				InvUtil.quickItem(InvUtil.indexSlot(bestSlot));
				tickDelay = (int) delay.getCurrentValueDouble();
			}
		}
	}

	private int getItemScore(ItemStack itemStack) {
		int score = 0;
		score += 2 * (EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack) - currentProt);
		score += 2 * (EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, itemStack) - currentBlast);
		score += 2 * (EnchantmentHelper.getLevel(Enchantments.FIRE_PROTECTION, itemStack) - currentFire);
		score += 2 * (EnchantmentHelper.getLevel(Enchantments.PROJECTILE_PROTECTION, itemStack) - currentProj);
		score += 2 * (((ArmorItem) itemStack.getItem()).getProtection() - currentArmour);
		score += 2 * (((ArmorItem) itemStack.getItem()).method_26353() - currentToughness);
		score += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack) - currentUnbreaking;
		return score;
	}

	private void getCurrentScore(ItemStack itemStack) {
		currentProt = EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack);
		currentBlast = EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, itemStack);
		currentFire = EnchantmentHelper.getLevel(Enchantments.FIRE_PROTECTION, itemStack);
		currentProj = EnchantmentHelper.getLevel(Enchantments.PROJECTILE_PROTECTION, itemStack);
		currentArmour = ((ArmorItem) itemStack.getItem()).getProtection();
		currentToughness = ((ArmorItem) itemStack.getItem()).method_26353();
		currentUnbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
	}
}