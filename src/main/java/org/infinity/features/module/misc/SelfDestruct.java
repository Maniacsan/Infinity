package org.infinity.features.module.misc;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.components.window.SelfDestructWindow;
import org.infinity.utils.Helper;
import org.infinity.utils.system.memory.EfficientString;

@ModuleInfo(category = Category.MISC, desc = "Self destruct infinity, no way out", key = -2, name = "SelfDestruction", visible = false)
public class SelfDestruct extends Module {

	private Setting putDelete = new Setting(this, "Path Delete (All Files)", true);
	private Setting memoryClear = new Setting(this, "Memory Clear", true);

	@Override
	public void onEnable() {
		Helper.openScreen(new SelfDestructWindow(Helper.MC.currentScreen));
		setEnabled(false);
	}

	public void destruct() {
		InfMain.getModuleManager().getFullEnableModules().forEach(modules -> modules.setEnabled(false));
		InfMain.INSTANCE.self = true;

		File path = InfMain.getDirection();
		try {
			if (this.putDelete.isToggle() && path.exists())
				FileUtils.deleteDirectory(path);

			if (memoryClear.isToggle())
				EfficientString.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Helper.openScreen(null);
	}
}
