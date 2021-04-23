package org.infinity.protect.impl;

import org.infinity.InfMain;
import org.infinity.event.protect.ButtonPressEvent;
import org.infinity.event.protect.StartProcessEvent;
import org.infinity.event.protect.SuccessEvent;
import org.infinity.protect.Handler;
import org.infinity.utils.Helper;
import org.infinity.utils.UpdaterUtil;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import net.minecraft.client.gui.screen.TitleScreen;

public class AuthHandler extends Handler {

	@EventTarget
	public void onPostStart(StartProcessEvent event) {
		if (event.getType().equals(EventType.POST)) {
			Helper.openScreen(InfMain.authUI);
		}
	}

	@EventTarget
	public void onButtonPress(ButtonPressEvent event) {
		if (Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {

			SuccessEvent successEvent = new SuccessEvent();
			EventManager.call(successEvent);

			if (UpdaterUtil.checkUpdate())
			Helper.openScreen(new TitleScreen(true));
			
			setInit(false);
		}
	}

}
