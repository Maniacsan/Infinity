package org.infinity.features.component.cape.cape;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.infinity.mixin.IElytraFeatureRenderer;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinProvider.SkinTextureAvailableCallback;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public abstract class AbstractCapeProvider {
	private static final Identifier DEFAULT_ELYTRA = IElytraFeatureRenderer.getElytraTexture();
	
	private File skinCacheDir;
	private TextureManager textureManager;
	private Executor executor;
	private Proxy proxy;
	
	public AbstractCapeProvider(File skinCacheDir, TextureManager textureManager, Executor executor, Proxy proxy) {
		this.skinCacheDir = skinCacheDir;
		this.textureManager = textureManager;
		this.executor = executor;
		this.proxy = proxy;
	}
	
	public void loadCape(GameProfile gameProfile, MinecraftProfileTexture mojangCape, SkinTextureAvailableCallback callback) {
		//AnyCapes.LOGGER.debug("Loading cape for profile " + gameProfile);
		String hash = Hashing.sha1().hashUnencodedChars("cape-" + gameProfile.getId().toString()).toString();
		Identifier identifier = new Identifier("skins/" + hash);
		AbstractTexture texture = textureManager.getTexture(identifier);
		if (texture != null) {
			if (callback != null) {
				if (texture instanceof CapeTexture) {
					if (!((CapeTexture) texture).hasElytra()) {
						callback.onSkinTextureAvailable(Type.ELYTRA, DEFAULT_ELYTRA, null);
					}
				}
				callback.onSkinTextureAvailable(Type.CAPE, identifier, null);
			}
		} else {
			File cacheFile = null;
			if (useCaching()) {
				cacheFile = new File(new File(skinCacheDir, hash.length() > 2 ? hash.substring(0, 2) : "xx"), hash);
			}
			getCape(gameProfile, mojangCape == null ? null : mojangCape.getUrl(), cacheFile, (nativeImage, url) -> {
				MinecraftClient.getInstance().execute(() -> {
					CapeTransformResult result = transformCapeImage(nativeImage);
					NativeImage capeImage = result.transformedImage;
					boolean hasElytra = result.hasElytra;
					if (!hasElytra) {
						callback.onSkinTextureAvailable(Type.ELYTRA, DEFAULT_ELYTRA, null);
					}
					CapeTexture capeTexture = new CapeTexture(capeImage, hasElytra);
					textureManager.registerTexture(identifier, capeTexture);
					if (callback != null) {
						callback.onSkinTextureAvailable(Type.CAPE, identifier, url == null ? null : new MinecraftProfileTexture(url.toString(), null));
					}
					//AnyCapes.LOGGER.debug("Loaded cape for profile " + gameProfile + " from " + url);
				});
			});
		}
	}
	
	public void getCape(GameProfile gameProfile, String mojangCapeUrl, File cacheFile, ImageDownloadCallback callback) {
		if (cacheFile != null && cacheFile.isFile()) {
			NativeImage nativeImage = null;
			try {
				FileInputStream fileInputStream = new FileInputStream(cacheFile);
				nativeImage = NativeImage.read(fileInputStream);
			} catch (Exception exception) {
				cacheFile.delete();
			}
			if (nativeImage != null) {
				callback.onSuccess(nativeImage, null);
				return;
			}
		}

		downloadCape(gameProfile, mojangCapeUrl, cacheFile, callback);
	}
	
	public void downloadCape(GameProfile gameProfile, String mojangCapeUrl, File cacheFile, ImageDownloadCallback callback) {
		CompletableFuture.runAsync(() -> {
			downloadCape(getCapeUrls(), 0, gameProfile, mojangCapeUrl, cacheFile, callback);
		}, executor);
	}
	
	private void downloadCape(List<String> urls, int index, GameProfile gameProfile, String mojangCapeUrl, File cacheFile, ImageDownloadCallback callback) {
		if (index >= urls.size()) {
			return;
		}
		URL url = formatUrl(urls.get(index), gameProfile, mojangCapeUrl);
		if (url == null) {
			downloadCape(urls, index+1, gameProfile, mojangCapeUrl, cacheFile, callback);
			return;
		}
		CompletableFuture<NativeImage> future = downloadImage(url, cacheFile);
		future.whenCompleteAsync((nativeImage, throwable) -> {
			if (nativeImage != null && throwable == null) {
				callback.onSuccess(nativeImage, url);
			} else {
				downloadCape(urls, index+1, gameProfile, mojangCapeUrl, cacheFile, callback);
			}
		}, executor);
	}
	
	public CompletableFuture<NativeImage> downloadImage(URL url, File cacheFile) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection httpURLConnection = null;
			NativeImage nativeImage = null;
			try {
				httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
				httpURLConnection.connect();
				if (httpURLConnection.getResponseCode() / 100 == 2) {
					InputStream inputStream = httpURLConnection.getInputStream();
					nativeImage = NativeImage.read(inputStream);
					if (cacheFile != null) {
						nativeImage.writeFile(cacheFile);
					}
				}
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			} finally {
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}
			return nativeImage;
		}, executor);
	}
	
	public abstract List<String> getCapeUrls();
	
	public abstract boolean useCaching();
	
	public abstract URL formatUrl(String urlStr, GameProfile gameProfile, String mojangCapeUrl);
	
	public abstract CapeTransformResult transformCapeImage(NativeImage capeImage);
}