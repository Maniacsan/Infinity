package org.infinity.features.module.world;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.infinity.InfMain;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.ui.features.SelfDestructUI;
import org.infinity.utils.Helper;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.JarDirectory;
import org.infinity.utils.system.memory.EfficientString;

@ModuleInfo(category = Category.WORLD, desc = "Self destruct infinity, no way out", key = -2, name = "SelfDestruct", visible = false)
public class SelfDestruction extends Module {

	private Setting putDelete = new Setting(this, "Path Delete (All Files)", true);
	private Setting renameJar = new Setting(this, "Rename Jar", true);

	@Override
	public void onEnable() {
		Helper.openScreen(new SelfDestructUI());
		setEnabled(false);
	}

	public void destruct() {
		// disable enabled modules
		InfMain.getModuleManager().getFullEnableModules().forEach(modules -> {
			modules.setEnabled(false);
		});

		File path = InfMain.getDirection();

		try {
			String jarPath = JarDirectory.getCurrentJARDirectory();
			File jarFile = JarDirectory.getCurrentJARFilePath();
			String[] rename = new String[] { "BetterSprinting-1.16.3-v3.2.0", "agape_space-0.0.11", "witheran-1.0.3",
					"fpsdisplay-1.16.4-v1.3.0", "CoordinateMod-1.3-1.16.x", "cr-compass-ribbon-fabric-2.7.2+1.16.5" };

			int randomI = RandomUtils.nextInt(0, rename.length - 1);
			
			String fileRename = rename[randomI];

			if (putDelete.isToggle() && path.exists())
				FileUtils.deleteDirectory(path);

			if (renameJar.isToggle())
				if (FileUtil.renameFile(jarFile, new File(jarPath + File.separator + fileRename))) {
					System.out.println("Renamed sucessfully to: " + fileRename);
				}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// memory clear
		EfficientString.clear();
	}

}
