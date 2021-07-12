package org.infinity.features.command.commands;

import org.infinity.features.command.Command;
import org.infinity.features.command.CommandInfo;
import org.infinity.main.InfMain;

import net.minecraft.util.Formatting;

@CommandInfo(name = "help", desc = "See commands")
public class HelpCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		for (Command commands : InfMain.getCommandManager().getCommands()) {
			send(commands.getName() + " - " + Formatting.GRAY + commands.getDesc());
		}
	}

	@Override
	public void error() {
		send("Please use: " + Formatting.AQUA + prefix + "help");
	}

}
