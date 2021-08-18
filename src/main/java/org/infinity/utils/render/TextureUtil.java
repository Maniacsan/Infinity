package org.infinity.utils.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.infinity.features.component.cape.util.ImageUtil;
import org.infinity.utils.Helper;
import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class TextureUtil extends HashMap<String, AbstractTexture> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void bindTexture(String resourceLocation, boolean localDirectory) {
		if (!containsKey(resourceLocation)) {
			BufferedImage bufferedImage;
			AbstractTexture texture = null;
			try {
				if (localDirectory)
					bufferedImage = ImageIO.read(TextureUtil.class.getResourceAsStream(resourceLocation));
				else
					bufferedImage = ImageIO.read(new File(resourceLocation));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, "png", baos);
				byte[] bytes = baos.toByteArray();

				ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
				data.flip();

				texture = new NativeImageBackedTexture(NativeImage.read(data));
			} catch (IOException e) {
				e.printStackTrace();
			}
			assert texture != null;
			put(resourceLocation, texture);
		}

		RenderSystem.setShaderTexture(0, get(resourceLocation).getGlId());
	}

	public void bindTexture(String url) {
		if (!containsKey(url)) {
			downloadImageFromUrl(null, url);
		}
		RenderSystem.setShaderTexture(0, get(url).getGlId());

	}

	public void downloadImageFromUrl(Identifier id, String url) {
		try {
			AbstractTexture texture = null;

			if (!containsKey(url)) {

				if (url != null && !url.isEmpty()) {
					NativeImage image = NativeImage.read(new URL(url).openStream());

					texture = new NativeImageBackedTexture(image);
					if (id != null) {
						Helper.MC.getTextureManager().destroyTexture(id);
						Helper.MC.getTextureManager().registerTexture(id, texture);
					}
				}

				put(url, texture);
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	public void downloadCapeFromUrl(Identifier id, String url) {
		try {
			AbstractTexture texture = null;

			if (!containsKey(url)) {

				if (url != null && !url.isEmpty()) {
					NativeImage image = NativeImage.read(new URL(url).openStream());

					texture = new NativeImageBackedTexture(transformCapeImage(image));
					if (id != null) {
						Helper.MC.getTextureManager().destroyTexture(id);
						Helper.MC.getTextureManager().registerTexture(id, texture);
					}
				}

				put(url, texture);
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	private NativeImage transformCapeImage(NativeImage capeImage) {
		NativeImage transformed;

		if (capeImage.getWidth() % 46 == 0 && capeImage.getHeight() % 22 == 0) {
			int scale = capeImage.getWidth() / 46;
			transformed = ImageUtil.resizeCanvas(capeImage, scale * 64, scale * 32);
		} else if (capeImage.getWidth() % 22 == 0 && capeImage.getHeight() % 17 == 0) {
			int scale = capeImage.getWidth() / 22;
			transformed = ImageUtil.resizeCanvas(capeImage, scale * 64, scale * 32);
		} else if (capeImage.getWidth() % 355 == 0 && capeImage.getHeight() % 275 == 0) {
			int scale = capeImage.getWidth() / 355;
			transformed = ImageUtil.cropAndResizeCanvas(capeImage, scale * 1024, scale * 512, scale * 2, scale * 2,
					scale, scale);
		} else if (capeImage.getWidth() % 352 == 0 && capeImage.getHeight() % 275 == 0) {
			int scale = capeImage.getWidth() / 352;
			transformed = ImageUtil.cropAndResizeCanvas(capeImage, scale * 1024, scale * 512, 0, scale * 2, 0, scale);
		} else if (capeImage.getWidth() % 355 == 0 && capeImage.getHeight() % 272 == 0) {
			int scale = capeImage.getWidth() / 355;
			transformed = ImageUtil.cropAndResizeCanvas(capeImage, scale * 1024, scale * 512, scale * 2, 0, scale, 0);
		} else {
			transformed = capeImage;
		}
		return transformed;
	}

}
