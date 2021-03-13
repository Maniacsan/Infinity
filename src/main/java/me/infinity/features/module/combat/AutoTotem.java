package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.UpdateUtil;
import net.minecraft.item.Items;

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
		
		int totem = InvUtil.findItemFullInv(Items.TOTEM_OF_UNDYING);

		int slot = totem < 9 ? totem + 36 : totem;
		if (Helper.getPlayer().getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
			if (totem != -2) {
				InvUtil.switchItem(slot, 0);
				InvUtil.switchItem(offHand, 0);
				InvUtil.switchItem(slot, 1);
			}
		}
	}
}
