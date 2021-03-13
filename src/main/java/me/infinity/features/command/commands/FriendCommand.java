package me.infinity.features.command.commands;

import me.infinity.InfMain;
import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import me.infinity.utils.Helper;
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
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "friend add " + Formatting.AQUA + "name");
		set(Formatting.WHITE + prefix + "bind del " + Formatting.AQUA + "name");
		set(Formatting.WHITE + prefix + "bind list");
		set(Formatting.WHITE + prefix + "bind clear");
		
	}

}
