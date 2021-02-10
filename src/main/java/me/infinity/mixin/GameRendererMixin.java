package me.infinity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import me.infinity.event.RenderEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private Camera camera;

	@Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {
			"ldc=hand" }))
	private void render(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
		RenderEvent renderEvent = new RenderEvent(tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);
		EventManager.call(renderEvent);
	}

}
