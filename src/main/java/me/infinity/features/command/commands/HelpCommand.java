package me.infinity.features.command.commands;

import me.infinity.InfMain;
import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import net.minecraft.util.Formatting;

@CommandInfo(name = "help", desc = "See commands")
public class HelpCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		for (Command commands : InfMain.getCommandManager().getCommands()) {
			set(commands.getName() + " - " + Formatting.GRAY + commands.getDesc());
		}
	}

	@Override
	public void error() {
		set("Please use: " + Formatting.AQUA + prefix + "help");
	}

}
