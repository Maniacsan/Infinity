package org.infinity.protect.impl;

import org.infinity.event.protect.ServerConnectEvent;
import org.infinity.protect.Handler;
import org.infinity.protect.ui.AuthUI;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;

public class ConnectLocker extends Handler {

	@EventTarget
	public void onConnect(ServerConnectEvent event) {
		 CrashReport crashReport = CrashReport.create(null, "");
		if (Helper.minecraftClient.currentScreen instanceof AuthUI) {
			event.cancel();
			MinecraftClient.printCrashReport(crashReport);
		}
  
		if (!Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {
			MinecraftClient.printCrashReport(crashReport);
		}
	}

}
