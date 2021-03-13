package me.infinity.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.infinity.features.Module.Category;
import me.infinity.features.module.combat.*;
import me.infinity.features.module.movement.*;
import me.infinity.features.module.visual.*;

public class ModuleManager {

	private static List<Module> list = Arrays.asList(
			new KillAura(),
			new HUD(),
			new Sprint(),
			new GuiMod(),
			new TestValues(),
			new Criticals(),
			new BowAim(),
			new XRay()
			);

	public List<Module> getList() {
		return list;
	}

	/**
	 * Importing feature module from class
	 * 
	 * @param clas
	 * @return
	 */
	public Module getModuleByClass(Class<?> clas) {
		Iterator<Module> iteratorMod = list.iterator();
		Module module;
		do {
			if (!iteratorMod.hasNext()) {
				return null;
			}
			module = (Module) iteratorMod.next();
		} while (module.getClass() != clas);
		return module;
	}

	/**
	 * Importing feature module from name
	 * 
	 * @param name
	 * @return
	 */
	public Module getModuleByName(String name) {
		for (Module m : list) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

    public ArrayList<Module> getModulesForCategory(Category c) {
        ArrayList<Module> modules = new ArrayList<>();

        for (Module m : list) {
            if (m.getCategory().equals(c)) {
                modules.add(m);
            }
        }

        return modules;
    }
}
