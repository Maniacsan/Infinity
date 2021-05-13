package org.infinity.features.module.world;

import java.io.File;

import org.infinity.InfMain;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.features.module.hidden.StringCleaner;
import org.infinity.ui.features.SelfDestructUI;
import org.infinity.utils.ConnectUtil;
import org.infinity.utils.Helper;

@ModuleInfo(category = Category.WORLD, desc = "Self destruct infinity, no way out", key = -2, name = "SelfDestruct", visible = false)
public class SelfDestruction extends Module {

	private Setting putDelete = new Setting(this, "Path Delete (All Files)", true);
	private Setting deleteJar = new Setting(this, "Deleta Jar", true);

	@Override
	public void onEnable() {
		Helper.openScreen(new SelfDestructUI());
	}

	public void destruct() {
		InfMain.selfDestruct = (() -> true);

		Helper.minecraftClient.currentScreen.onClose();

		InfMain.onShutdown();

		// disable enabled modules
		InfMain.getModuleManager().getFullEnableModules().forEach(modules -> {
			modules.setEnabled(false);
		});

		if (putDelete.isToggle()) {
			File path = new File(Helper.minecraftClient.runDirectory + File.separator + "Infinity");
			if (path.exists()) {
				path.delete();
			}
		}

		try {
			if (deleteJar.isToggle())
				ConnectUtil.seldDestructionJar(false);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		// memory clear
		InfMain.getModuleManager().getModuleByClass(StringCleaner.class).enable();
	}

}
