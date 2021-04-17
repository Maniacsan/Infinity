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
		super(host, port, username);

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

		try {
			sendMessage(getChannel(), message);
			Helper.ircMessage("> " + ColorUtils.getUserRoleColor() + getPrefix() + " " + Formatting.GRAY + getUsername()
					+ ": " + Formatting.WHITE + message);
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

		String name = line.substring(1, line.indexOf(":"));
		String message = line.substring(line.lastIndexOf(":") + 1, line.length());

		Helper.ircMessage(ColorUtils.getStringUserColor(prefix) + prefix + " " + Formatting.GRAY + name + ": "
				+ Formatting.WHITE + message);
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void exception(Exception e) {
	}

}
