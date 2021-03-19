package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.ClickButtonEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Throws a pearl when pressing middle click", key = -2, name = "MClickPearl", visible = true)
public class MClickPearl extends Module {

	@EventTarget
	public void onMiddleClick(ClickButtonEvent event) {
		int pearlSlot = InvUtil.findItemOnHotbar(Items.ENDER_PEARL);

		if (Helper.minecraftClient.currentScreen != null)
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
