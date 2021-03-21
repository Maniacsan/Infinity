package me.infinity.features.module.movement;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Allows you to walk with an open inventory", key = -2, name = "InvWalk", visible = true)
public class InvWalk extends Module {

	@Override
	public void onPlayerTick() {
		if (Helper.minecraftClient.currentScreen != null
				&& !(Helper.minecraftClient.currentScreen instanceof ChatScreen)) {
			
			for (KeyBinding k : new KeyBinding[] { Helper.minecraftClient.options.keyForward,
					Helper.minecraftClient.options.keyBack, Helper.minecraftClient.options.keyLeft,
					Helper.minecraftClient.options.keyRight, Helper.minecraftClient.options.keyJump,
					Helper.minecraftClient.options.keySprint }) {
				k.setPressed(InputUtil.isKeyPressed(Helper.minecraftClient.getWindow().getHandle(),
						InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));
			}
		}
	}

}
