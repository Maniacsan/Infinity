package me.infinity.features;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.infinity.features.module.combat.*;
import me.infinity.features.module.movement.*;
import me.infinity.features.module.player.*;
import me.infinity.features.module.visual.*;
import me.infinity.features.module.world.*;

public class ModuleManager {

	private static List<Module> list = Arrays.asList(
			new KillAura(),
			new HUD(),
			new Sprint(),
			new GuiMod(),
			new Criticals(),
			new BowAim(),
			new XRay(),
			new Velocity(),
			new Scaffold(),
			new SafeWalk(),
			new HitBoxes(),
			new AimAssist(),
			new ClickAura(),
			new Timer(),
			new AutoTotem(),
			new AutoClicker(),
			new MiddleClickPearl(),
			new AutoTool(),
			new NoSlow(),
			new FastEXP(),
			new AutoShield()
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
}
