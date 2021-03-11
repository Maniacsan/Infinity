package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.UpdateUtil;
import net.minecraft.item.Items;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically takes the shield in the off hand", key = -2, name = "AutoShield", visible = true)
public class AutoShield extends Module {

	private Settings ignoreTotem = new Settings(this, "Ignore Totem", false, () -> true);

	@Override
	public void onPlayerTick() {
		if (!UpdateUtil.canUpdate())
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
