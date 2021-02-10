package me.infinity.features;

import me.infinity.InfMain;

public class HookManager {

	/**
	 * Keyboard class , method = onKey
	 * @param keyCode
	 */
	public void onKeyPressed(int keyCode) {
		for (Module module : InfMain.getModuleManager().getList()) {
			if (module.getKey() == keyCode)
				module.enable();
		}
	}

	/**
	 * Update Client Player ticks (old method name onUpdate)
	 */
	public void onPlayerTick() {
		for (Module m : InfMain.getModuleManager().getList()) {
			if (m.isEnabled()) {
				m.onPlayerTick();
			}
		}
	}
	

	/**
	 * Rendering HUD in game (2D)
	 * @param scaledWidth
	 * @param scaledHeight
	 */
	public void onRender(int scaledWidth, int scaledHeight) {
		for (Module m : InfMain.getModuleManager().getList()) {
			if (m.isEnabled()) {
				m.onRender(scaledWidth, scaledHeight);
			}
		}
	}

}
