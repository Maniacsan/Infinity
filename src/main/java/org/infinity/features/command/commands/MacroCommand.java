package org.infinity.features.command.commands;

import org.infinity.features.command.Command;
import org.infinity.features.command.CommandInfo;
import org.infinity.features.component.macro.Macro;
import org.infinity.main.InfMain;

import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;

@CommandInfo(name = "macro", desc = "Binding message for key to chat")
public class MacroCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		String macroMsg = msg.replace(getName() + " " + args[0] + " " + args[1] + " ", "");
		if (args[0].equalsIgnoreCase("add")) {
			InfMain.getMacroManager().getList().add(new Macro(macroMsg,
					InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode()));
			send(Formatting.GRAY + "Macro added to key " + Formatting.WHITE + args[1]);
			InfMain.getMacroManager().save();
		} else if (args[0].equalsIgnoreCase("del")) {
			InfMain.getMacroManager()
					.del(InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode());
			send(Formatting.GRAY + "Macro deleted from key " + Formatting.WHITE + args[1]);
			InfMain.getMacroManager().save();
		} else if (args[0].equalsIgnoreCase("list")) {
			if (InfMain.getMacroManager().getList().isEmpty()) {
				send(Formatting.GRAY + "Macro binds is empty");
			} else {
				InfMain.getMacroManager().getList().forEach(macro -> {
					send(macro.getKey() + " -> " + Formatting.GRAY + macro.getMessage());
				});
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			InfMain.getMacroManager().getList().clear();
			send(Formatting.GRAY + "Macros cleared");
			InfMain.getMacroManager().save();
		}
	}

	@Override
	public void error() {
		send(Formatting.GRAY + "Please use" + Formatting.WHITE + ":");
		send(Formatting.WHITE + prefix + "macro add " + Formatting.GRAY + "<" + Formatting.AQUA + "key" + Formatting.GRAY
				+ ">" + Formatting.GRAY + " message");
		send(Formatting.WHITE + prefix + "macro del " + Formatting.AQUA + "key");
		send(Formatting.WHITE + prefix + "macro list");
		send(Formatting.WHITE + prefix + "macro clear");

	}

}
