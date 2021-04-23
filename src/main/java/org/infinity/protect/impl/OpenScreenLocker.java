package org.infinity.protect.impl;

import org.infinity.event.OpenScreenEvent;
import org.infinity.protect.Handler;

import com.darkmagician6.eventapi.EventTarget;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;

public class OpenScreenLocker extends Handler {

	@EventTarget
	public void onOpenScreen(OpenScreenEvent event) {
		CrashReport crashReport = CrashReport.create(null, "");
		if (!Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {
			MinecraftClient.printCrashReport(crashReport);
		}
	}

}
