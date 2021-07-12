package org.infinity.features.component.cape.cape;

import java.net.URL;

import net.minecraft.client.texture.NativeImage;

public interface ImageDownloadCallback {
	public void onSuccess(NativeImage nativeImage, URL url);
}
