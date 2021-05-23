package org.infinity.features.command.commands;

import org.infinity.features.command.Command;
import org.infinity.features.command.CommandInfo;
import org.infinity.main.InfMain;

import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;

@CommandInfo(name = "bind", desc = "Bind a module")
public class BindCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		org.infinity.features.Module module = InfMain.getModuleManager().getModuleByName(args[1]);
		if (args[0].equalsIgnoreCase("add")) {
			module.setKey(InputUtil.fromTranslationKey("key.keyboard." + args[2].toLowerCase()).getCode());
			set(Formatting.GRAY + "Module - " + Formatting.WHITE + module.getName() + Formatting.GRAY + " binded to "
					+ Formatting.AQUA + args[2]);
		} else if (args[0].equalsIgnoreCase("del")) {
			module.setKey(-2);
			set(Formatting.AQUA + module.getName() + Formatting.GRAY + " removed binds");
		} else if (args[0].equalsIgnoreCase("clear")) {
			for (org.infinity.features.Module m : InfMain.getModuleManager().getList())
				m.setKey(-2);

			set(Formatting.GRAY + "Binds cleared");
		}
	}

	@Override
	public void error() {
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "bind add " + Formatting.AQUA + "module" + Formatting.GRAY + "<"
				+ Formatting.AQUA + "key" + Formatting.GRAY + ">");
		set(Formatting.WHITE + prefix + "bind del " + Formatting.AQUA + "module");
		set(Formatting.WHITE + prefix + "bind clear");
	}

}
