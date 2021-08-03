package org.infinity.features.module.player;

import org.infinity.event.ClickButtonEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.module.world.MClickFriend;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@ModuleInfo(category = Category.PLAYER, desc = "Throws a pearl when pressing middle click", key = -2, name = "MClickPearl", visible = true)
public class MClickPearl extends Module {

	@EventTarget
	public void onMiddleClick(ClickButtonEvent event) {
		int pearlSlot = InvUtil.findItemOnHotbar(Items.ENDER_PEARL);

		if (Helper.MC.currentScreen != null)
			return;

		if (InfMain.getModuleManager().get(MClickFriend.class).isEnabled()
				&& Helper.MC.targetedEntity != null)
			return;

		if (event.getButton() == 2) {

			if (pearlSlot != -2) {
				int preSlot = Helper.getPlayer().getInventory().selectedSlot;

				Helper.getPlayer().getInventory().selectedSlot = pearlSlot;
				Helper.MC.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(),
						Hand.MAIN_HAND);

				Helper.getPlayer().getInventory().selectedSlot = preSlot;
			}
		}
	}

}
