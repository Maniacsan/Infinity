package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

/**
 * 
 * @author Alexander01998
 *
 */
@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically puts on the best armor", key = -2, name = "AutoArmor", visible = true)
public class AutoArmor extends Module {

	private Settings onInv = new Settings(this, "On Open Inventory", false, () -> true);

	private Settings enchant = new Settings(this, "EnchantCheck", true, () -> true);

	private Settings delay = new Settings(this, "Delay", 1.0D, 0D, 15.0D, () -> true);

	private int timer;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			// wait for timer
			if (timer > 0) {
				timer--;
				return;
			}

			// check screen
			if (onInv.isToggle() && !(Helper.minecraftClient.currentScreen instanceof InventoryScreen))
				return;

			PlayerInventory inventory = Helper.getPlayer().inventory;


			int[] bestArmorSlots = new int[4];
			int[] bestArmorValues = new int[4];

			for (int type = 0; type < 4; type++) {
				bestArmorSlots[type] = -1;

				ItemStack stack = inventory.getArmorStack(type);
				if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem))
					continue;

				ArmorItem item = (ArmorItem) stack.getItem();
				bestArmorValues[type] = getArmorValue(item, stack);
			}

			for (int slot = 0; slot < 36; slot++) {
				ItemStack stack = inventory.getStack(slot);

				if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem))
					continue;

				ArmorItem item = (ArmorItem) stack.getItem();
				int armorType = item.getSlotType().getEntitySlotId();
				int armorValue = getArmorValue(item, stack);

				if (armorValue > bestArmorValues[armorType]) {
					bestArmorSlots[armorType] = slot;
					bestArmorValues[armorType] = armorValue;
				}
			}

			ArrayList<Integer> types = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
			Collections.shuffle(types);
			for (int type : types) {
				int slot = bestArmorSlots[type];
				if (slot == -1)
					continue;

				ItemStack oldArmor = inventory.getArmorStack(type);
				if (!oldArmor.isEmpty() && inventory.getEmptySlot() == -1)
					continue;

				if (slot < 9)
					slot += 36;

				if (!oldArmor.isEmpty())
					Helper.minecraftClient.interactionManager.clickSlot(0, 8 - type, 0, SlotActionType.QUICK_MOVE,
							Helper.getPlayer());
				Helper.minecraftClient.interactionManager.clickSlot(0, slot, 0, SlotActionType.QUICK_MOVE,
						Helper.getPlayer());

				break;
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof ClickSlotC2SPacket)
				timer = (int) delay.getCurrentValueDouble();
		}
	}

	private int getArmorValue(ArmorItem item, ItemStack stack) {
		int armorPoints = item.getProtection();
		int prtPoints = 0;
		int armorToughness = 0;
		int armorType = item.getMaterial().getProtectionAmount(EquipmentSlot.LEGS);

		if (enchant.isToggle()) {
			Enchantment protection = Enchantments.PROTECTION;
			int prtLvl = EnchantmentHelper.getLevel(protection, stack);

			DamageSource dmgSource = DamageSource.player(Helper.getPlayer());
			prtPoints = protection.getProtectionAmount(prtLvl, dmgSource);
		}

		return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
	}

}
