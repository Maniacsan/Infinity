package org.infinity.utils.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class TextureUtil extends HashMap<String, AbstractTexture> {

	public void bindTexture(String resourceLocation) {
		if (!containsKey(resourceLocation)) {
			BufferedImage bufferedImage;
			AbstractTexture texture = null;
			try {
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

		RenderSystem.bindTexture(get(resourceLocation).getGlId());
	}

}
