package org.infinity.protect.impl;

import org.infinity.event.protect.ButtonPressEvent;
import org.infinity.event.protect.StartProcessEvent;
import org.infinity.event.protect.SuccessEvent;
import org.infinity.main.InfMain;
import org.infinity.protect.Handler;
import org.infinity.utils.ConnectUtil;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.protect.Protect;
import net.minecraft.client.gui.screen.TitleScreen;

public class AuthHandler extends Handler {

	@EventTarget
	public void onPostStart(StartProcessEvent event) {
		if (event.getType().equals(EventType.POST)) {
			Helper.openScreen(InfMain.INSTANCE.init.authUI);
		}
	}

	@EventTarget(5)
	public void onButtonPress(ButtonPressEvent event) {
		if (Protect.CHECK.getResult().get().equalsIgnoreCase("true")) {
			SuccessEvent successEvent = new SuccessEvent();
			EventManager.call(successEvent);

					Helper.openScreen(new TitleScreen(true));

		}
	}

}
