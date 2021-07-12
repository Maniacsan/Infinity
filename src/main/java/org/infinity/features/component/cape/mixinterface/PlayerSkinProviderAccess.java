package org.infinity.features.component.cape.mixinterface;

import java.io.File;

import org.infinity.features.component.cape.cape.AbstractCapeProvider;

import net.minecraft.client.texture.TextureManager;

public interface PlayerSkinProviderAccess {
	public AbstractCapeProvider getCapeProvider();
	
	public void setCapeProvider(AbstractCapeProvider capeProvider);
	
	public TextureManager getTextureManager();
	
	public File getSkinCacheDir();
}
