package org.infinity.protect.impl;

import org.infinity.InfMain;
import org.infinity.event.protect.StartProcessEvent;
import org.infinity.protect.Handler;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

public class ProtectHandler extends Handler {

	@EventTarget
	public void onPostStart(StartProcessEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			InfMain.getHandler().getHandler(AuthHandler.class).enable();
			InfMain.getHandler().getHandler(ConnectLocker.class).enable();
		} else if (event.getType().equals(EventType.POST)) {
			InfMain.getHandler().getHandler(ReLoginProcess.class).enable();
		}
	}
}
