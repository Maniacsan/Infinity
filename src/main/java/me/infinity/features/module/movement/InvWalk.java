package me.infinity.features.module.movement;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Allows you to walk with an open inventory", key = -2, name = "InvWalk", visible = true)
public class InvWalk extends Module {

	@Override
	public void onPlayerTick() {
		if (!skip()) {
			tickSneakJumpAndSprint();
		}
	}

	// KeyboardInput onTick
	public void onTick() {
		if (skip())
			return;

		Helper.getPlayer().input.movementForward = 0;
		Helper.getPlayer().input.movementSideways = 0;

		if (Helper.minecraftClient.options.keyForward.isPressed()) {
			Helper.getPlayer().input.pressingForward = true;
			Helper.getPlayer().input.movementForward++;
		} else
			Helper.getPlayer().input.pressingForward = false;

		if (Helper.minecraftClient.options.keyBack.isPressed()) {
			Helper.getPlayer().input.pressingBack = true;
			Helper.getPlayer().input.movementForward--;
		} else
			Helper.getPlayer().input.pressingBack = false;

		if (Helper.minecraftClient.options.keyRight.isPressed()) {
			Helper.getPlayer().input.pressingRight = true;
			Helper.getPlayer().input.movementSideways--;
		} else
			Helper.getPlayer().input.pressingRight = false;

		Helper.getPlayer().input.jumping = Helper.minecraftClient.options.keyJump.isPressed();
		Helper.getPlayer().setSprinting(Helper.minecraftClient.options.keySprint.isPressed());

		if (Helper.minecraftClient.options.keyLeft.isPressed()) {
			Helper.getPlayer().input.pressingLeft = true;
			Helper.getPlayer().input.movementSideways++;
		} else
			Helper.getPlayer().input.pressingLeft = false;
	}

	private void tickSneakJumpAndSprint() {
		Helper.getPlayer().input.jumping = Helper.minecraftClient.options.keyJump.isPressed();
		Helper.getPlayer().setSprinting(Helper.minecraftClient.options.keySprint.isPressed());
	}

	private boolean skip() {
		return Helper.minecraftClient.currentScreen == null
				|| Helper.minecraftClient.currentScreen instanceof ChatScreen
				|| Helper.minecraftClient.currentScreen instanceof SignEditScreen
				|| Helper.minecraftClient.currentScreen instanceof AnvilScreen
				|| Helper.minecraftClient.currentScreen instanceof AbstractCommandBlockScreen
				|| Helper.minecraftClient.currentScreen instanceof StructureBlockScreen;
	}

}
