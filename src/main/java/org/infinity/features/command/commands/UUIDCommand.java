package org.infinity.features.command.commands;

import org.infinity.features.command.Command;
import org.infinity.features.command.CommandInfo;
import org.infinity.utils.Helper;

import com.mojang.authlib.GameProfile;

import net.minecraft.util.Formatting;

@CommandInfo(name = "uuid", desc = "Get your uuid")
public class UUIDCommand extends Command {

	@Override
	public void command(String[] args, String msg) {
		GameProfile profile = Helper.getPlayer().getGameProfile();
		send("Your" + Formatting.GREEN + " UUID: " + Formatting.WHITE + profile.getId().toString());

	}

	@Override
	public void error() {
		// TODO Auto-generated method stub

	}

}
