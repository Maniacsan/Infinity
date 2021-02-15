package me.infinity.ui.account.main;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import me.infinity.mixin.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

public class AccountThread extends Thread {
	private Account alt;

	public AccountThread(Account alt) {
		super("AccountThread");
		this.alt = alt;
	}

	private Session createSession(final String username, final String password) {
		final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
				.createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(username);
		auth.setPassword(password);
		try {
			auth.logIn();
			return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(), "mojang");
		} catch (AuthenticationException localAuthenticationException) {
			localAuthenticationException.printStackTrace();
			return null;
		}
	}

    protected void setSession(Session session) {
        ((IMinecraftClient) MinecraftClient.getInstance()).setSession(session);
    }

	@Override
	public void run() {
		if (alt.isNickname()) {
			setSession(new Session(alt.getUsername(), "", "", "mojang"));
			return;
		}
		final Session auth = this.createSession(alt.getUsername(), alt.getPassword());
		if (auth != null) {
			AccountManager.lastAlt = new Account(alt.getUsername(), alt.getPassword());
			alt.setMask(auth.getUsername());
			setSession(auth);
		}
	}
}