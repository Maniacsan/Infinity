package org.infinity.ui.account.main;

import java.net.Proxy;

import org.infinity.main.InfMain;
import org.infinity.mixin.IMinecraftClient;
import org.infinity.utils.Helper;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

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
		InfMain.getCape().remove(Helper.MC.getSession().getUsername());
		if (alt.isNickname()) {
			setSession(new Session(alt.getUsername(), "", "", "mojang"));
			InfMain.getCape().update();
			return;
		}
		Session auth = createSession(alt.getUsername(), alt.getPassword());

		if (auth == null)
			return;

		AccountManager.lastAlt = new Account(alt.getUsername(), alt.getPassword());
		alt.setMask(auth.getUsername());
		setSession(auth);
		InfMain.getCape().update();
	}
}
