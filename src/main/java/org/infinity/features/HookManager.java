package org.infinity.features;

import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.client.util.math.MatrixStack;

public class HookManager {

	/**
	 * Keyboard class , method = onKey
	 * 
	 * @param keyCode
	 */
	public void onKeyPressed(int keyCode) {
		InfMain.getModuleManager().getList().forEach(m -> {
			if (m.getKey() == keyCode)
				m.enable();
		});
	}

	/**
	 * Update Client Player ticks (old method name onUpdate)
	 */
	public void onPlayerTick() {
		InfMain.getModuleManager().getList().forEach(m -> {
			if (m.isEnabled()) {
				m.onPlayerTick();
			}
		});
	}

	/**
	 * Rendering HUD in game (2D)
	 * 
	 * @param scaledWidth
	 * @param scaledHeight
	 */
	public void onRender(MatrixStack matrices, float tickDelta, int scaledWidth, int scaledHeight) {
		InfMain.getModuleManager().getList().forEach(m -> {
			if (m.isEnabled()) {
				m.onRender(matrices, tickDelta, scaledWidth, scaledHeight);
			}
		});
	}

	/**
	 * Make macro binds
	 * 
	 * @param key
	 */
	public void onMacro(int key) {
		InfMain.getMacroManager().getList().forEach(macro -> {
			if (macro.getKey() == key) {
				Helper.getPlayer().sendChatMessage(macro.getMessage());
			}
		});
	}

}
