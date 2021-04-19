package me.infinity.chat.IRC;

import java.io.IOException;
import java.util.Arrays;

import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.utils.Helper;
import net.minecraft.util.Formatting;

public class IRCClient extends IIRC {

	private String channel;

	private String prefix;

	public IRCClient(String host, int port, String username, String prefix, String channel) {
		super(host, port, prefix + "_" + username);

		this.channel = channel;
		this.prefix = prefix;
	}

	public String getChannel() {
		return channel;
	}

	public String getPrefix() {
		return prefix;
	}

	public void runIRC(String message) {
		String[] args = message.split(" (?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
		args = Arrays.stream(args).map(s -> s.replace("\"", "")).toArray(size -> new String[size]);

		String prefix = getUsername().split("_")[0];
		String name = getUsername().split("_")[1];
		try {
			sendMessage(getChannel(), message);
			Helper.ircMessage("> " + ColorUtils.getUserRoleColor() + prefix + " " + Formatting.GRAY + name + ": "
					+ Formatting.WHITE + message);
		} catch (IOException e) {
			e.printStackTrace();
			Helper.ircMessage(
					Formatting.RED + "Can't send the chat message. " + Formatting.GRAY + "(" + e.getMessage() + ")");
		}
	}

	@Override
	public void listener(String line) {
		if (!(isActive()))
			return;

		if (!(line.contains("!")) || !(line.contains(":")))
			return;

		String name = line.substring(1, line.indexOf("!"));
		String prefix = name.split("_")[0];
		String username = name.replace(name.split("_")[0], "");
		String message = line.substring(line.lastIndexOf(":") + 1, line.length());

		if (message.contains("#InfinityModChat") || message.isEmpty())
			return;

		Helper.ircMessage(ColorUtils.getStringUserColor(prefix) + prefix + " " + Formatting.GRAY + username.substring(1)
				+ ": " + Formatting.WHITE + message);
		System.out.println(username + ": " + message);
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void exception(Exception e) {
	}

}
