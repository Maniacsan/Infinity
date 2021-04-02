package me.infinity.features.module.world;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.InfMain;
import me.infinity.event.ClickButtonEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
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
