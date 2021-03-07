package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.UpdateUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically takes the totem in the off hand", key = -2, name = "AutoTotem", visible = true)
public class AutoTotem extends Module {

	private Settings health = new Settings(this, "Health", 20D, 0D, 20D, () -> true);

	private int offHand = 45;

	@Override
	public void onPlayerTick() {
		if (!UpdateUtil.canUpdate())
			return;

		if (Helper.getPlayer().getHealth() < this.health.getCurrentValueDouble())
			return;

		int slot = findTotem() < 9 ? findTotem() + 36 : findTotem();
		if (Helper.getPlayer().getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
			if (findTotem() != -2) {
				switchItem(slot, 0);
				switchItem(offHand, 0);
				switchItem(slot, 1);
			}
		}
	}

	private void switchItem(int slot, int button) {
		Helper.minecraftClient.interactionManager.clickSlot(button, slot, 1, SlotActionType.PICKUP, Helper.getPlayer());
	}

	private int findTotem() {
		int totem = -2;
		for (int i = 0; i <= 44; i++) {
			if (Helper.getPlayer().inventory.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
				totem = i;
			}
		}
		return totem;
	}
}
