package org.infinity.features.module.combat;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;

import net.minecraft.item.Items;

@ModuleInfo(category = Category.COMBAT, desc = "Automatically takes the shield in the off hand", key = -2, name = "AutoShield", visible = true)
public class AutoShield extends Module {

	private Setting ignoreTotem = new Setting(this, "Ignore Totem", false);

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer() == null && Helper.getWorld() == null)
			return;

		int shield = InvUtil.findItemFullInv(Items.SHIELD);

		if (!ignoreTotem.isToggle() && Helper.getPlayer().getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
			return;

		int slot = shield < 9 ? shield + 36 : shield;
		if (Helper.getPlayer().getOffHandStack().getItem() != Items.SHIELD) {
			if (shield != -2) {
				InvUtil.switchItem(slot, 0);
				InvUtil.switchItem(45, 0);
				InvUtil.switchItem(slot, 1);
			}
		}
	}

}
