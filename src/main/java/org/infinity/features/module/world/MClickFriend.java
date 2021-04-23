package org.infinity.features.module.world;

import org.infinity.InfMain;
import org.infinity.event.ClickButtonEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(category = Module.Category.WORLD, desc = "When you hover over a player and click the middle mouse, he is added to friends", key = -2, name = "MClickFriend", visible = true)
public class MClickFriend extends Module {

	@EventTarget
	public void onMiddleClick(ClickButtonEvent event) {

		if (Helper.minecraftClient.currentScreen != null)
			return;

		if (event.getButton() == 2) {
			if (Helper.minecraftClient.targetedEntity != null
					&& Helper.minecraftClient.targetedEntity instanceof PlayerEntity) {
				InfMain.getFriend().addOrDelate(Helper.minecraftClient.targetedEntity.getDisplayName().getString());
			}
		}
	}

}
