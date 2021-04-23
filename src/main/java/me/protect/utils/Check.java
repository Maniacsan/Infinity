package me.protect.utils;

import java.util.function.Supplier;

import org.infinity.InfMain;
import org.infinity.utils.user.User;

import me.protect.Protect;
import me.protect.connection.LoginUtil;
import me.protect.imain.ICheck;

public class Check implements ICheck {

	private Supplier<String> result;

	public Check() {
		this.result = null;
	}

	@Override
	public Supplier<String> getResult() {
		return result;
	}

	@Override
	public void setResult(Supplier<String> result, String username) {
		LoginUtil.username.forEach(name -> {
			LoginUtil.password.forEach(pass -> {
				if (name == null || pass == null)
					return;

				InfMain.setUser(new User(username, "ADMIN"));
				this.result = result;

			});
		});
	}

}
