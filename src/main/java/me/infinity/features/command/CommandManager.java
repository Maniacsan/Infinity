package me.infinity.features.command;

import java.util.Arrays;
import java.util.List;

import me.infinity.features.command.commands.*;
import me.infinity.utils.Helper;
import net.minecraft.util.Formatting;

public class CommandManager {

	private List<Command> commands = Arrays.asList(
			new BindCommand(),
			new HelpCommand(),
			new MacroCommand(),
			new FriendCommand(),
			new ToggleCommand(),
			new HClipCommand(),
			new VClipCommand(),
			new IPCommand()
			);

	public List<Command> getCommands() {
		return commands;
	}

	public void callCommand(String input) {
		String[] split = input.split(" ", -1);
		String command = split[0];
		String args = input.substring(command.length()).trim();
		for (Command c : getCommands()) {
			if (c.getName().equalsIgnoreCase(command)) {
				try {
					c.command(args.split(""), command);
				} catch (Exception e) {
					c.error();
					e.printStackTrace();
				}
				return;
			}
		}
		Helper.infoMessage(Formatting.GRAY + "Command not found. " + Formatting.WHITE + "Please see "
				+ Formatting.DARK_AQUA + Command.prefix + "help");
	}
}
