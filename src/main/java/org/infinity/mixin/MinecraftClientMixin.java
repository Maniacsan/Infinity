package org.infinity.mixin;

import org.infinity.event.ClickEvent;
import org.infinity.event.OpenScreenEvent;
import org.infinity.event.TickEvent;
import org.infinity.event.protect.StartProcessEvent;
import org.infinity.features.component.cape.Capes;
import org.infinity.main.InfMain;
import org.infinity.ui.FirstStartUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	public ClientWorld world;

	@Shadow
	public abstract Profiler getProfiler();

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public void init(RunArgs runArgs, CallbackInfo ci) {
		StartProcessEvent event = new StartProcessEvent(EventType.ON);
		EventManager.call(event);
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V", shift = At.Shift.AFTER))
	public void postInit(RunArgs runArgs, CallbackInfo ci) {
		StartProcessEvent event = new StartProcessEvent(EventType.PRE);
		EventManager.call(event);
	}

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void onScreenInit(RunArgs runArgs, CallbackInfo ci) {
		StartProcessEvent event = new StartProcessEvent(EventType.POST);
		EventManager.call(event);
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		TickEvent tickEvent = new TickEvent();
		EventManager.call(tickEvent);

		if (world != null) {
			if (InfMain.firstStart)
				((MinecraftClient) (Object) this).openScreen(new FirstStartUI());
		}
	}

	@Inject(at = {
			@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0) }, method = {
					"doAttack()V" }, cancellable = true)
	private void onClick(CallbackInfo info) {
		ClickEvent clickEvent = new ClickEvent();
		EventManager.call(clickEvent);

		if (clickEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "stop", at = @At("HEAD"))
	public void stop(CallbackInfo info) {
		InfMain.INSTANCE.shutDown();
	}

	@Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
	private void onOpenScreen(Screen screen, CallbackInfo ci) {
		OpenScreenEvent openScreenEvent = new OpenScreenEvent(screen);
		EventManager.call(openScreenEvent);

	}

}
