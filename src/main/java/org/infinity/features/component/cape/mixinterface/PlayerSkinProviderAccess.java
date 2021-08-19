package org.infinity.features.component.cape.mixinterface;

import java.io.File;

import org.infinity.features.component.cape.cape.CapeProvider;

import net.minecraft.client.texture.TextureManager;

public interface PlayerSkinProviderAccess {
	public CapeProvider getCapeProvider();
	
	public void setCapeProvider(CapeProvider capeProvider);
	
	public TextureManager getTextureManager();
	
	public File getSkinCacheDir();
}
