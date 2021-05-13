package org.infinity.features.module.player;

import org.infinity.InfMain;
import org.infinity.event.ClickButtonEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.module.world.MClickFriend;
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

		if (Helper.minecraftClient.currentScreen != null)
			return;

		if (InfMain.getModuleManager().getModuleByClass(MClickFriend.class).isEnabled()
				&& Helper.minecraftClient.targetedEntity != null)
			return;

		if (event.getButton() == 2) {

			if (pearlSlot != -2) {
				int preSlot = Helper.getPlayer().inventory.selectedSlot;

				Helper.getPlayer().inventory.selectedSlot = pearlSlot;
				Helper.minecraftClient.interactionManager.interactItem(Helper.getPlayer(), Helper.getWorld(),
						Hand.MAIN_HAND);

				Helper.getPlayer().inventory.selectedSlot = preSlot;
			}
		}
	}

}
