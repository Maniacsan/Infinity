package me.infinity.features.module.player;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.mixin.IMinecraftClient;
import me.infinity.utils.Helper;
import net.minecraft.item.Items;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Throw experience bubbles faster", key = -2, name = "FastEXP", visible = true)
public class FastEXP extends Module {

	@Override
	public void onPlayerTick() {
		// Super module, Very hard
		if (Helper.getPlayer().getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
			((IMinecraftClient) Helper.minecraftClient).setItemCooldown(0);
		}
	}

}
