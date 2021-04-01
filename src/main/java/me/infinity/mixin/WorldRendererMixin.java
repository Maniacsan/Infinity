package me.infinity.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.event.OutlineRenderEvent;
import me.infinity.event.RenderEntityEvent;
import me.infinity.event.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
	private void render_swap(Profiler profiler, String string) {
		if (string.equals("entities")) {
			EventManager.call(new OutlineRenderEvent());
		} else if (string.equals("blockentities")) {
			EventManager.call(new OutlineRenderEvent());
		} else if (string.equals("blockentities")) {
			EventManager.call(new OutlineRenderEvent());
		}
	}

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

	@Redirect(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
	public <E extends Entity> void onRenderEntity(EntityRenderDispatcher dispatcher, E entity, double x, double y,
			double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light) {
		RenderEntityEvent renderEvent = new RenderEntityEvent(entity, matrices, vertexConsumers);
		EventManager.call(renderEvent);

		if (!renderEvent.isCancelled()) {
			dispatcher.render(renderEvent.getEntity(), x, y, z, yaw, tickDelta, renderEvent.getMatrix(),
					renderEvent.getVertex(), light);
		}
	}

}
