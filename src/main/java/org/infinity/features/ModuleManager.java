package org.infinity.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.infinity.features.Module.Category;
import org.infinity.features.module.combat.*;
import org.infinity.features.module.hidden.*;
import org.infinity.features.module.movement.*;
import org.infinity.features.module.player.*;
import org.infinity.features.module.visual.*;
import org.infinity.features.module.world.*;

public class ModuleManager {

	private static List<Module> list = Arrays.asList(
			// hidden
			new DiscordRPCMod(),
			new AntiFabric(),
			
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
			new AimBot(),
			new ClickAura(),
			new Timer(),
			new AutoTotem(),
			new AutoClicker(),
			new MClickPearl(),
			new AutoTool(),
			new NoSlow(),
			new FastEXP(),
			new AutoShield(),
			new Refill(),
			new AutoPotion(),
			new BetterBow(),
			new ChestSteal(),
			new FreeCam(),
			new AutoEat(),
			new ChatCalculator(),
			new ESP(),
			new Tracers(),
			new ItemESP(),
			new AutoLeave(),
			new InvWalk(),
			new Reach(),
			new TriggerBot(),
			new AutoArmor(),
			new TargetStrafe(),
			new StorageESP(),
			new FullBright(),
			new Jesus(),
			new Speed(),
			new PacketKick(),
			new FastBreak(),
			new ArmorHUD(),
			new NameTags(),
			new NoClip(),
			new FakeLags(),
			new AntiAim(),
			new PingSpoof(),
			new Step(),
			new MClickFriend(),
			new AntiWaterPush(),
			new AutoShift(),
			new CrossbowAim(),
			new Fly(),
			new ShulkerView(),
			new Eagle()
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
	
	public List<Module> getEnableModules() {
		List<Module> eList = new ArrayList<>();
		for (Module m : getList()) {
			if (m.isEnabled() && m.getCategory() != Category.HIDDEN) {
				eList.add(m);
			}
		}
		return eList;
	}
}