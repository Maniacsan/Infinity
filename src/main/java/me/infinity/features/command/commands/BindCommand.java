package me.infinity.features.command.commands;

import me.infinity.InfMain;
import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;

@CommandInfo(name = "bind", desc = "Bind a module")
public class BindCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
			InfMain.getModuleManager().getList().forEach(m -> {
				if (m.getKey() != -2) {
					set(Formatting.GRAY + m.getName() + " - " + Formatting.WHITE + m.getKey());
				}
			});
		}
		me.infinity.features.Module module = InfMain.getModuleManager().getModuleByName(args[1]);
		if (args[0].equalsIgnoreCase("add")) {
			module.setKey(InputUtil.fromTranslationKey("key.keyboard." + args[2].toLowerCase()).getCode());
			set(Formatting.GRAY + "Module - " + Formatting.DARK_BLUE + module.getName() + Formatting.GRAY
					+ " binded to " + Formatting.AQUA + args[2]);
		} else if (args[0].equalsIgnoreCase("del")) {
			module.setKey(-2);
			set(Formatting.AQUA + module.getName() + Formatting.GRAY + " removed binds");
		} else if (args[0].equalsIgnoreCase("clear")) {
			InfMain.getModuleManager().getList().forEach(m -> {
				m.setKey(-2);
			});
			set(Formatting.GRAY + "Binds cleared");
		}
	}

	@Override
	public void error() {
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "bind add " + Formatting.AQUA + "module" + Formatting.GRAY + "<"
				+ Formatting.AQUA + "key" + Formatting.GRAY + ">");
		set(Formatting.WHITE + prefix + "bind del " + Formatting.AQUA + "module");
		set(Formatting.WHITE + prefix + "bind list");
		set(Formatting.WHITE + prefix + "bind clear");
	}

}