package me.infinity.features.module.combat;

import java.util.HashMap;
import java.util.Map.Entry;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author Alexander01998
 *
 */
@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically puts on the best armor", key = -2, name = "AutoArmor", visible = true)
public class AutoArmor extends Module {

	private Settings onInv = new Settings(this, "On Open Inventory", false, () -> true);

	private Settings delay = new Settings(this, "Delay", 1.0D, 0D, 20.0D, () -> true);

	private int tickDelay = 0;

	@Override
	public void onPlayerTick() {

		if (tickDelay > 0) {
			tickDelay--;
			return;
		}

		tickDelay = (int) delay.getCurrentValueDouble();

		if (onInv.isToggle() && !(Helper.minecraftClient.currentScreen instanceof HandledScreen))
			return;

		HashMap<EquipmentSlot, int[]> armorMap = new HashMap<>(4);
		armorMap.put(EquipmentSlot.FEET,
				new int[] { 36, getProtection(Helper.getPlayer().inventory.getStack(36)), -1, -1 });
		armorMap.put(EquipmentSlot.LEGS,
				new int[] { 37, getProtection(Helper.getPlayer().inventory.getStack(37)), -1, -1 });
		armorMap.put(EquipmentSlot.CHEST,
				new int[] { 38, getProtection(Helper.getPlayer().inventory.getStack(38)), -1, -1 });
		armorMap.put(EquipmentSlot.HEAD,
				new int[] { 39, getProtection(Helper.getPlayer().inventory.getStack(39)), -1, -1 });

		for (int s = 0; s < 36; s++) {
			int prot = getProtection(Helper.getPlayer().inventory.getStack(s));

			if (prot > 0) {
				EquipmentSlot slot = (Helper.getPlayer().inventory.getStack(s).getItem() instanceof ElytraItem
						? EquipmentSlot.CHEST
						: ((ArmorItem) Helper.getPlayer().inventory.getStack(s).getItem()).getSlotType());

				for (Entry<EquipmentSlot, int[]> e : armorMap.entrySet()) {
					if (e.getKey() == slot) {
						if (prot > e.getValue()[1] && prot > e.getValue()[3]) {
							e.getValue()[2] = s;
							e.getValue()[3] = prot;
						}
					}
				}
			}
		}

		for (Entry<EquipmentSlot, int[]> e : armorMap.entrySet()) {
			if (e.getValue()[2] != -1) {
				if (e.getValue()[1] == -1 && e.getValue()[2] < 9) {
					InvUtil.quickItem(36 + e.getValue()[2]);
				} else if (Helper.getPlayer().playerScreenHandler == Helper.getPlayer().currentScreenHandler) {
					/* Convert inventory slots to container slots */
					int armorSlot = (e.getValue()[0] - 34) + (39 - e.getValue()[0]) * 2;
					int newArmorslot = e.getValue()[2] < 9 ? 36 + e.getValue()[2] : e.getValue()[2];

					InvUtil.switchItem(newArmorslot, 0);
					InvUtil.switchItem(armorSlot, 0);

					if (e.getValue()[1] != -1)
						InvUtil.switchItem(newArmorslot, 0);
				}

				return;
			}
		}
	}

	private int getProtection(ItemStack is) {
		if (is.getItem() instanceof ArmorItem) {
			int prot = 0;

			if (is.getItem() instanceof ElytraItem) {
				if (!ElytraItem.isUsable(is))
					return 0;

				prot = 1;

			} else if (is.getMaxDamage() - is.getDamage() < 7) {
				return 0;
			}

			if (is.hasEnchantments()) {
				for (Entry<Enchantment, Integer> e : EnchantmentHelper.get(is).entrySet()) {
					if (e.getKey() instanceof ProtectionEnchantment)
						prot += e.getValue();
				}
			}

			return (is.getItem() instanceof ArmorItem ? ((ArmorItem) is.getItem()).getProtection() : 0) + prot;
		} else if (!is.isEmpty()) {
			return 0;
		}

		return -1;
	}
}