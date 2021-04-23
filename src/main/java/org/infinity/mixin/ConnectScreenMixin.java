package org.infinity.mixin;

import org.infinity.event.protect.ServerConnectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import net.minecraft.client.gui.screen.ConnectScreen;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

	@Inject(method = "connect", at = @At("HEAD"), cancellable = true)
	private void connect(final String address, final int port, CallbackInfo ci) {
		ServerConnectEvent connectEvent = new ServerConnectEvent();
		EventManager.call(connectEvent);

		if (connectEvent.isCancelled()) {
			ci.cancel();
		}
	}

}
