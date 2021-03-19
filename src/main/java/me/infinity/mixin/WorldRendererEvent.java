package me.infinity.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.event.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

@Mixin(WorldRenderer.class)
public class WorldRendererEvent {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void renderPre(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
			CallbackInfo info) {
		RenderEvent event = new RenderEvent(EventType.PRE, tickDelta);
		EventManager.call(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void renderPost(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
			CallbackInfo info) {
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		RenderEvent event = new RenderEvent(EventType.POST, tickDelta);
		EventManager.call(event);
	}

}
