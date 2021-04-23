package me.protect.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import me.protect.imain.IImage;

public class ImageUtil implements IImage {

	@Override
	public void downloadImage(String urlPath, String path) {
		BufferedImage image = null;
		try {

			URL url = new URL(urlPath);

			image = ImageIO.read(url);

			ImageIO.write(image, "png", new File(path));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
