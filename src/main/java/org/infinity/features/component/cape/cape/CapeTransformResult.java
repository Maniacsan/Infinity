package org.infinity.features.component.cape.cape;

import net.minecraft.client.texture.NativeImage;

public class CapeTransformResult {
	public NativeImage transformedImage;
	public boolean hasElytra;
	
	public CapeTransformResult(NativeImage transformedImage, boolean hasElytra) {
		this.transformedImage = transformedImage;
		this.hasElytra = hasElytra;
	}
}
