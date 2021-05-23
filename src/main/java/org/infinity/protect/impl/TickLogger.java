package org.infinity.protect.impl;

import org.infinity.event.OpenScreenEvent;
import org.infinity.main.InfMain;
import org.infinity.protect.Handler;
import org.infinity.protect.ui.AuthUI;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.utils.PHelper;

public class TickLogger extends Handler {

	@EventTarget
	public void onOpenScreen(OpenScreenEvent event) {
		if (InfMain.getUser().getName() == null || InfMain.getUser() == null) {
			if (!(event.getScreen() instanceof AuthUI))
			PHelper.makeCrash();
		}
	}
}
