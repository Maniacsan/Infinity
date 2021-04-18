package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import me.infinity.InfMain;
import me.infinity.event.ClickEvent;
import me.infinity.event.TickEvent;
import me.infinity.ui.FirstStartUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	public ClientWorld world;

	@Shadow
	public abstract Profiler getProfiler();

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		TickEvent tickEvent = new TickEvent();
		EventManager.call(tickEvent);

		if (world != null) {
			if (InfMain.firstStart) {
				((MinecraftClient) (Object) this).openScreen(new FirstStartUI());
			}
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
		InfMain.onShutdown();
	}

}
