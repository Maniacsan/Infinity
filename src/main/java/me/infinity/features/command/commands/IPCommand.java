package me.infinity.features.command.commands;

import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import me.infinity.utils.Helper;
import net.minecraft.util.Formatting;

@CommandInfo(name = "ip", desc = "See server ip address")
public class IPCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		if (Helper.minecraftClient.isInSingleplayer()) {
			set(Formatting.GRAY + "You playing " + Formatting.WHITE + "SinglePlayer");
		} else
		set(Formatting.GRAY + "Server IP addres: "
				+ Helper.getPlayer().networkHandler.getConnection().getAddress().toString());

	}

	@Override
	public void error() {
		set("Please use: " + Formatting.AQUA + prefix + "ip");

	}

}
