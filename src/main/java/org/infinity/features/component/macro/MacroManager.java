package org.infinity.features.component.macro;

import java.util.ArrayList;
import java.util.List;

import org.infinity.file.MacrosFile;
import org.infinity.utils.Helper;

import net.minecraft.util.Formatting;

public class MacroManager {

	private MacrosFile macrosFile = new MacrosFile();
	private List<Macro> list = new ArrayList<>();

	public List<Macro> getList() {
		return list;
	}
	
	public void save() {
		macrosFile.saveMacro();
	}
	
	public void load() {
		macrosFile.loadMacro();
	}

	public void del(int key) {
		for (Macro macro : getList()) {
			if (macro.getKey() == key) {
				list.remove(macro);
			} else {
				Helper.infoMessage(Formatting.GRAY + "This key not binded");
			}
		}
	}

}
