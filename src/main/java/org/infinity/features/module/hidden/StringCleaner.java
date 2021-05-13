package org.infinity.features.module.hidden;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.system.memory.EfficientString;

@ModuleInfo(category = Category.HIDDEN, desc = "", key = -2, name = "String Cleaner", visible = false)
public class StringCleaner extends Module {
	
	public void onEnable() {
		// sozdal class chtobi mojno bilo vkluchat / vikluchat
		EfficientString.clear();
	}

}
