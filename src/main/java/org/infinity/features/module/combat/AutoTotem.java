package org.infinity.features.module.combat;

import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;

import net.minecraft.item.Items;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically takes the totem in the off hand", key = -2, name = "AutoTotem", visible = true)
public class AutoTotem extends Module {

	private Settings health = new Settings(this, "Health", 20D, 0D, 20D, () -> true);

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().getHealth() <= this.health.getCurrentValueDouble()) {

			int totem = InvUtil.findItemFullInv(Items.TOTEM_OF_UNDYING);

			int slot = totem < 9 ? totem + 36 : totem;
			if (Helper.getPlayer().getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
				if (totem != -2) {
					InvUtil.switchItem(slot, 0);
					InvUtil.switchItem(45, 0);
					InvUtil.switchItem(slot, 1);
				}
			}
		}
	}
}
