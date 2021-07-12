package org.infinity.features.command.commands;

import org.infinity.features.command.Command;
import org.infinity.features.command.CommandInfo;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.util.Formatting;

@CommandInfo(name = "friend", desc = "Adding / Removing friend")
public class FriendCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		if (args[0].equalsIgnoreCase("add")) {
			InfMain.getFriend().add(args[1]);
		} else if (args[0].equalsIgnoreCase("del")) {
			InfMain.getFriend().remove(args[1]);
		} else if (args[0].equalsIgnoreCase("clear")) {
			InfMain.getFriend().getFriendList().clear();
			InfMain.getFriend().save();
			Helper.infoMessage(Formatting.GRAY + "Friend list successfully" + Formatting.WHITE + "cleared");
		} else if (args[0].equalsIgnoreCase("list")) {
			InfMain.getFriend().getFriendList().forEach(friends -> {
				Helper.infoMessage(Formatting.GRAY + "[Friend] " + Formatting.WHITE + friends);
			});
		}
		
	}

	@Override
	public void error() {
		send(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		send(Formatting.WHITE + prefix + "friend add " + Formatting.AQUA + "name");
		send(Formatting.WHITE + prefix + "bind del " + Formatting.AQUA + "name");
		send(Formatting.WHITE + prefix + "bind list");
		send(Formatting.WHITE + prefix + "bind clear");
		
	}

}
