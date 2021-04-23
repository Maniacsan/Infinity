package org.infinity.protect.impl;

import org.infinity.InfMain;
import org.infinity.event.TickEvent;
import org.infinity.protect.Handler;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.Protect;
import me.protect.connection.LoginUtil;
import me.protect.connection.Auth.AuthType;

public class ReLoginProcess extends Handler {

	@EventTarget
	public void onTick(TickEvent event) {
		if (Helper.getWorld() != null) {
			if (InfMain.reLogin) {
				LoginUtil.username.forEach(name -> {
					LoginUtil.password.forEach(pass -> {
						if (name == null || pass == null) {
							me.protect.utils.PHelper.makeCrash();
							return;
						}

						if (Protect.LOGIN.login(name, pass).getType().equals(AuthType.SUCCESS)) {
							InfMain.reLogin = false;
							setInit(false);
						} else {
							me.protect.utils.PHelper.makeCrash();
						}
					});
				});
			}
		}
	}

}
