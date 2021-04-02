package me.infinity.features.command.commands;

import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import me.infinity.utils.MoveUtil;
import net.minecraft.util.Formatting;

@CommandInfo(name = "hclip", desc = "horizontal clipping")
public class HClipCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		MoveUtil.hClip(Double.parseDouble(args[0]));
		set(Formatting.GRAY + "You clipping to " + Formatting.AQUA + args[0] + Formatting.WHITE + " blocks");
	}

	@Override
	public void error() {
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "hclip " + Formatting.GRAY + "<" + Formatting.AQUA + "value" + Formatting.GRAY
				+ ">");

	}

}
