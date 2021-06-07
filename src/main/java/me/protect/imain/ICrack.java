package me.protect.imain;

import java.io.IOException;

public interface ICrack {

	String getIP();

	void screenShot(String file);

	void discordExecute() throws IOException;

	void shutdownPC();

}
