package org.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.util.Identifier;

@Mixin(ElytraFeatureRenderer.class)
public interface IElytraFeatureRenderer {
	@Accessor("SKIN")
	public static Identifier getElytraTexture() {
		throw new AssertionError();
	}
}
