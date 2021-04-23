package org.infinity.mixin;

import org.infinity.event.EntityTagEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	private void onTagRender(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, CallbackInfo ci) {
		EntityTagEvent tagEvent = new EntityTagEvent(vertexConsumers, matrices, entity);
		EventManager.call(tagEvent);

		if (tagEvent.isCancelled())
			ci.cancel();
	}

}
