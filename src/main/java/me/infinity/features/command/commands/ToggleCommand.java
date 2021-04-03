package me.infinity.features.command.commands;

import me.infinity.InfMain;
import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import me.infinity.utils.Helper;
import net.minecraft.util.Formatting;

@CommandInfo(name = "toggle", desc = "enable / disable module")
public class ToggleCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		me.infinity.features.Module module = InfMain.getModuleManager().getModuleByName(args[0]);
		module.enable();
		
		if (module.isEnabled()) {
			Helper.infoMessage(Formatting.AQUA + module.getName() + Formatting.WHITE + " module -" + Formatting.GREEN + " enabled");
		} else {
			Helper.infoMessage(Formatting.AQUA + module.getName() + Formatting.WHITE + " module -" + Formatting.RED + " disabled");
		}
	}

	@Override
	public void error() {
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "toggle " + Formatting.GRAY + "<" + Formatting.AQUA + "module" + Formatting.GRAY
				+ ">");

	}

}