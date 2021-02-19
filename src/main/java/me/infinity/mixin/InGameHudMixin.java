package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import me.infinity.InfMain;
import me.infinity.event.HudRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Inject(at = @At(value = "RETURN"), method = "render")
	private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo info) {
		InfMain.getHookManager().onRender(matrices, scaledWidth, scaledHeight);
		HudRenderEvent hudRenderEvent = new HudRenderEvent(MinecraftClient.getInstance().getWindow(), scaledWidth,
				scaledHeight, matrices);
		EventManager.call(hudRenderEvent);
	}

}
