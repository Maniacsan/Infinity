package me.infinity.features.command.commands;

import me.infinity.InfMain;
import me.infinity.features.command.Command;
import me.infinity.features.command.CommandInfo;
import me.infinity.features.impl.macro.Macro;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;

@CommandInfo(name = "macro", desc = "Binding message for key to chat")
public class MacroCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		if (args[0].equalsIgnoreCase("add")) {
			InfMain.getMacroManager().getList().add(new Macro(args[2],
					InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode()));
			set(Formatting.GRAY + "Macro added to key " + Formatting.WHITE + args[1]);
			InfMain.getMacroManager().save();
		} else if (args[0].equalsIgnoreCase("del")) {
			InfMain.getMacroManager()
					.del(InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode());
			set(Formatting.GRAY + "Macro deleted from key " + Formatting.WHITE + args[1]);
			InfMain.getMacroManager().save();
		} else if (args[0].equalsIgnoreCase("list")) {
			if (InfMain.getMacroManager().getList().isEmpty()) {
				set(Formatting.GRAY + "Macro binds is empty");
			} else {
				InfMain.getMacroManager().getList().forEach(macro -> {
					set(macro.getKey() + " -> " + Formatting.GRAY + macro.getMessage());
				});
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			InfMain.getMacroManager().getList().clear();
			set(Formatting.GRAY + "Macros cleared");
			InfMain.getMacroManager().save();
		}
	}

	@Override
	public void error() {
		set(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		set(Formatting.WHITE + prefix + "macro add " + Formatting.GRAY + "<" + Formatting.AQUA + "key" + Formatting.GRAY
				+ ">" + Formatting.GRAY + " message");
		set(Formatting.WHITE + prefix + "macro del " + Formatting.AQUA + "key");
		set(Formatting.WHITE + prefix + "macro list");
		set(Formatting.WHITE + prefix + "macro clear");

	}

}
