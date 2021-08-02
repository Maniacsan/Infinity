package org.infinity.protect.impl;

import org.infinity.event.OpenScreenEvent;
import org.infinity.protect.Handler;
import org.infinity.protect.ui.AuthUI;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import me.protect.utils.PHelper;

public class OpenScreenLocker extends Handler {

	@EventTarget
	public void onOpenScreen(OpenScreenEvent event) {
		if (!Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS) && !(event.getScreen() instanceof AuthUI)) {
			PHelper.makeCrash();
		} else 
			setInit(false);
	}

}
