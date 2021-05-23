package org.infinity.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.infinity.features.module.combat.AimBot;
import org.infinity.features.module.combat.AutoArmor;
import org.infinity.features.module.combat.AutoClicker;
import org.infinity.features.module.combat.AutoShield;
import org.infinity.features.module.combat.AutoShift;
import org.infinity.features.module.combat.AutoTotem;
import org.infinity.features.module.combat.BetterBow;
import org.infinity.features.module.combat.BowAim;
import org.infinity.features.module.combat.ClickAura;
import org.infinity.features.module.combat.Criticals;
import org.infinity.features.module.combat.CrossbowAim;
import org.infinity.features.module.combat.HitBoxes;
import org.infinity.features.module.combat.KillAura;
import org.infinity.features.module.combat.Reach;
import org.infinity.features.module.combat.TriggerBot;
import org.infinity.features.module.combat.Velocity;
import org.infinity.features.module.hidden.AntiFabric;
import org.infinity.features.module.hidden.DiscordRPCMod;
import org.infinity.features.module.movement.AntiWaterPush;
import org.infinity.features.module.movement.Eagle;
import org.infinity.features.module.movement.Fly;
import org.infinity.features.module.movement.InvWalk;
import org.infinity.features.module.movement.Jesus;
import org.infinity.features.module.movement.SafeWalk;
import org.infinity.features.module.movement.Speed;
import org.infinity.features.module.movement.Sprint;
import org.infinity.features.module.movement.Step;
import org.infinity.features.module.movement.TargetStrafe;
import org.infinity.features.module.player.AntiAim;
import org.infinity.features.module.player.AutoEat;
import org.infinity.features.module.player.AutoLeave;
import org.infinity.features.module.player.AutoPotion;
import org.infinity.features.module.player.AutoTool;
import org.infinity.features.module.player.ChatCalculator;
import org.infinity.features.module.player.ChestSteal;
import org.infinity.features.module.player.FakeLags;
import org.infinity.features.module.player.FastBreak;
import org.infinity.features.module.player.FastEXP;
import org.infinity.features.module.player.FreeCam;
import org.infinity.features.module.player.MClickPearl;
import org.infinity.features.module.player.NoSlow;
import org.infinity.features.module.player.PacketKick;
import org.infinity.features.module.player.PingSpoof;
import org.infinity.features.module.player.Refill;
import org.infinity.features.module.player.Scaffold;
import org.infinity.features.module.visual.ArmorHUD;
import org.infinity.features.module.visual.ESP;
import org.infinity.features.module.visual.FullBright;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.features.module.visual.HUD;
import org.infinity.features.module.visual.ItemESP;
import org.infinity.features.module.visual.NameTags;
import org.infinity.features.module.visual.ShulkerView;
import org.infinity.features.module.visual.StorageESP;
import org.infinity.features.module.visual.Tracers;
import org.infinity.features.module.visual.XRay;
import org.infinity.features.module.world.MClickFriend;
import org.infinity.features.module.world.NoClip;
import org.infinity.features.module.world.SelfDestruction;
import org.infinity.features.module.world.Timer;

public class ModuleManager {

	private static List<Module> list = Arrays.asList(
			// hidden
			new DiscordRPCMod(), new AntiFabric(),

			new KillAura(), new HUD(), new Sprint(), new GuiMod(), new Criticals(), new BowAim(), new XRay(),
			new Velocity(), new Scaffold(), new SafeWalk(), new HitBoxes(), new AimBot(), new ClickAura(), new Timer(),
			new AutoTotem(), new AutoClicker(), new MClickPearl(), new AutoTool(), new NoSlow(), new FastEXP(),
			new AutoShield(), new Refill(), new AutoPotion(), new BetterBow(), new ChestSteal(), new FreeCam(),
			new AutoEat(), new ChatCalculator(), new ESP(), new Tracers(), new ItemESP(), new AutoLeave(),
			new InvWalk(), new Reach(), new TriggerBot(), new AutoArmor(), new TargetStrafe(), new StorageESP(),
			new FullBright(), new Jesus(), new Speed(), new PacketKick(), new FastBreak(), new ArmorHUD(),
			new NameTags(), new NoClip(), new FakeLags(), new AntiAim(), new PingSpoof(), new Step(),
			new MClickFriend(), new AntiWaterPush(), new AutoShift(), new CrossbowAim(), new Fly(), new ShulkerView(),
			new Eagle(), new SelfDestruction());

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

	public List<Module> getModulesByCategory(Category category) {
		return getList().stream().filter(m -> m.getCategory().equals(category)).collect(Collectors.toList());
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

	public List<Module> getFullEnableModules() {
		List<Module> eList = new ArrayList<>();
		for (Module m : getList()) {
			if (m.isEnabled()) {
				eList.add(m);
			}
		}
		return eList;
	}
}
