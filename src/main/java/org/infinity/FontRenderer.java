package org.infinity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.infinity.utils.Helper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

/**
 * @author Tigermouthbear 10/3/20
 */
public class FontRenderer {
	private static final Map<Integer, GlyphPage> glyphPageMap = new HashMap<>();

	private final String ttf;
	private final int size;

	public FontRenderer(String ttf, int size) {
		this.ttf = ttf;
		this.size = size;
	}

	private GlyphPage getGlyphPage() {
		GlyphPage glyphPage = glyphPageMap.get((int) Helper.minecraftClient.getWindow().getScaleFactor());
		if (glyphPage == null) {
			glyphPage = GlyphPage.create(ttf, (int) (size * Helper.minecraftClient.getWindow().getScaleFactor() / 2));
			glyphPageMap.put((int) (Helper.minecraftClient.getWindow().getScaleFactor()), glyphPage);
		}
		return glyphPage;
	}

	public double drawChar(MatrixStack matrices, char c, double x, double y) {
		return getGlyphPage().drawChar(matrices, c, x, y);
	}

	public void drawString(MatrixStack matrices, String text, double x, double y) {
		getGlyphPage().drawString(matrices, text, x, y);
	}

	public void drawString(MatrixStack matrices, String text, double x, double y, Color color, boolean shadow) {
		getGlyphPage().drawString(matrices, text, x, y, color, shadow);
	}

	public void drawString(MatrixStack matrices, String text, double x, double y, Color color) {
		drawString(matrices, text, x, y, color, false);
	}

	public void drawStringWithShadow(MatrixStack matrices, String text, double x, double y, Color color) {
		drawString(matrices, text, x, y, color, true);
	}

	public int drawStringWithCustomWidthWithShadow(MatrixStack matrices, String text, double x, double y, Color color,
			double width) {
		return drawStringWithCustomWidth(matrices, text, x, y, color, width, true);
	}

	public int drawStringWithCustomWidth(MatrixStack matrices, String text, double x, double y, Color color,
			double width) {
		return drawStringWithCustomWidth(matrices, text, x, y, color, width, false);
	}

	public int drawStringWithCustomWidth(MatrixStack matrices, String text, double x, double y, Color color,
			double width, boolean shadow) {
		double scale = width / getStringWidth(text);
		matrices.scale((float) scale, (float) scale, 1f);
		drawString(matrices, text, x / scale, y / scale, color, shadow);
		matrices.scale((float) (1f / scale), (float) (1f / scale), 1f);
		return (int) (getFontHeight() * scale);
	}

	public int drawStringWithCustomHeightWithShadow(MatrixStack matrices, String text, double x, double y, Color color,
			double height) {
		return drawStringWithCustomHeight(matrices, text, x, y, color, height, true);
	}

	public int drawStringWithCustomHeight(MatrixStack matrices, String text, double x, double y, Color color,
			double height) {
		return drawStringWithCustomHeight(matrices, text, x, y, color, height, false);
	}

	public int drawStringWithCustomHeight(MatrixStack matrices, String text, double x, double y, Color color,
			double height, boolean shadow) {
		double scale = height / getFontHeight();
		matrices.scale((float) scale, (float) scale, 1f);
		drawString(matrices, text, x / scale, y / scale, color, shadow);
		matrices.scale((float) (1f / scale), (float) (1f / scale), 1f);

		return (int) (getStringWidth(text) * scale);
	}

	public int drawSplitString(MatrixStack matrices, String text, double x, double y, Color color, double width) {
		return getGlyphPage().drawSplitString(matrices, text, x, y, color, width);
	}

	public double drawSplitString(MatrixStack matrices, String text, double x, double y, Color color, double width,
			double height) {
		double scale = height / getFontHeight();
		matrices.scale((float) scale, (float) scale, 1f);
		height = drawSplitString(matrices, text, x / scale, y / scale, color, width / scale);
		matrices.scale((float) (1f / scale), (float) (1f / scale), 1f);

		return height * scale;
	}

	public double getCharWidth(char c) {
		return getGlyphPage().getCharWidth(c);
	}

	public double getStringWidth(String text) {
		return getGlyphPage().getStringWidth(text);
	}

	public double getStringWidth(String text, double height) {
		return getStringWidth(text) * (height / getFontHeight());
	}

	public int getFontHeight() {
		return getGlyphPage().getFontHeight();
	}

	public double getFontHeightWithCustomWidth(String text, double width) {
		return width / getStringWidth(text) * getFontHeight();
	}

	static class GlyphPage {
		private final Map<Character, Glyph> characterGlyphMap = new HashMap<>();
		private final int[] colorCodes = new int[32];
		private final int width;
		private final int height;
		private final double glyphSize;
		private final BufferedImage bufferedImage;
		private final AbstractTexture texture;
		private double charHeight;

		GlyphPage(Font font, int size) {
			// generate color codes
			for (int i = 0; i < 32; ++i) {
				int j = (i >> 3 & 1) * 85;
				int k = (i >> 2 & 1) * 170 + j;
				int l = (i >> 1 & 1) * 170 + j;
				int i1 = (i & 1) * 170 + j;

				if (i == 6)
					k += 85;

				if (i >= 16) {
					k /= 4;
					l /= 4;
					i1 /= 4;
				}

				colorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
			}

			// find scale based on size
			this.glyphSize = 256 / (double) size * 0.04f;

			// generate ascii characters
			char[] chars = new char[256];
			for (int i = 0; i < chars.length; i++)
				chars[i] = (char) i;

			AffineTransform affineTransform = new AffineTransform();
			FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, true);

			// calculate character and image width and height
			double charWidth = 0;
			charHeight = 0;
			for (char c : chars) {
				Rectangle2D bounds = font.getStringBounds(Character.toString(c), fontRenderContext);

				double width = bounds.getWidth();
				double height = bounds.getHeight();

				if (width > charWidth)
					charWidth = width;
				if (height > charHeight)
					charHeight = height;
			}
			width = (int) (charWidth * 16);
			height = (int) (charHeight * 16);

			// create image and setup graphics
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

			// setup font and colors
			graphics2D.setFont(font);
			graphics2D.setColor(new java.awt.Color(255, 255, 255, 0));
			graphics2D.fillRect(0, 0, width, height);
			graphics2D.setColor(java.awt.Color.WHITE);

			// set fractional metrics and antialiasing
			graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// get font metrics
			FontMetrics fontMetrics = graphics2D.getFontMetrics();

			// draw chars and create glyph objects
			for (int i = 0; i < chars.length; i++) {
				// calculate x y width and height of glyph
				int x = (int) (i % 16 * charWidth);
				int y = (int) (i / 16 * charHeight);
				Rectangle2D bounds = fontMetrics.getStringBounds(Character.toString(chars[i]), graphics2D);

				// create glyph and add to map
				Glyph glyph = new Glyph(x, y, bounds.getWidth(), bounds.getHeight());
				characterGlyphMap.put(chars[i], glyph);

				// draw glyph on glyphPagge
				graphics2D.drawString(Character.toString(chars[i]), x, y + fontMetrics.getAscent());
			}

			// create texture
			AbstractTexture texture1;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, "png", baos);
				byte[] bytes = baos.toByteArray();

				ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
				data.flip();

				texture1 = new NativeImageBackedTexture(NativeImage.read(data));
			} catch (Exception e) {
				texture1 = null;
				e.printStackTrace();
			}
			texture = texture1;
		}

		public static GlyphPage create(String name, int type, int size) {
			return new GlyphPage(new Font(name, type, size), size);
		}

		public static GlyphPage create(String ttf, int size) {
			// read font from input stream
			Font font = null;
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, FontRenderer.class.getResourceAsStream(ttf)).deriveFont(60f);
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);

			return new GlyphPage(font, size);
		}

		public double drawChar(MatrixStack stack, char c, double x, double y) {
			Glyph glyph = characterGlyphMap.get(c);
			if (glyph == null)
				return 0;

			// calculate texture coords
			double texX = glyph.x / (double) width;
			double texY = glyph.y / (double) height;
			double texWidth = glyph.width / (double) width;
			double texHeight = glyph.height / (double) height;

			// calculate scaled width and height
			double scaledWidth = glyph.width * glyphSize;
			double scaledHeight = glyph.height * glyphSize;

			if (texture != null) {
				RenderSystem.bindTexture(texture.getGlId());
				RenderSystem.setShaderTexture(0, texture.getGlId());

				Matrix4f matrices = stack.peek().getModel();
				BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				bufferBuilder.vertex(matrices, (float) x, (float) (y + scaledHeight), 0)
						.texture((float) texX, (float) (texY + texHeight)).next();
				bufferBuilder.vertex(matrices, (float) (x + scaledWidth), (float) (y + scaledHeight), 0)
						.texture((float) (texX + texWidth), (float) (texY + texHeight)).next();
				bufferBuilder.vertex(matrices, (float) (x + scaledWidth), (float) y, 0)
						.texture((float) (texX + texWidth), (float) texY).next();
				bufferBuilder.vertex(matrices, (float) x, (float) y, 0).texture((float) texX, (float) texY).next();
				bufferBuilder.end();

				RenderSystem.enableTexture();
				RenderSystem.setShader(GameRenderer::getPositionTexShader);

				BufferRenderer.draw(bufferBuilder);

			}

			return glyph.width * glyphSize;
		}

		// draw string no shadow keep color
		public void drawString(MatrixStack matrices, String text, double x, double y) {
			// do regular text
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);

				if (c == 167 && i + 1 < text.length()) {
					int colorCode = "0123456789abcdefklmnor"
							.indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));
					color(colorCodes[colorCode]);
					++i;
				} else
					x += drawChar(matrices, c, x, y);
			}
		}

		public void drawString(MatrixStack matrices, String text, double x, double y, Color color, boolean shadow) {
			if (shadow) {
				RenderSystem.setShaderColor(0, 0, 0, 0.8f);

				double shadowX = x + 0.2;
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					if (c == 167 && i + 1 < text.length())
						++i;
					else
						shadowX += drawChar(matrices, c, shadowX, y);
				}

				RenderSystem.setShaderColor(1, 1, 1, 1);
			}

			RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			drawString(matrices, text, x, y);
		}

		// returns height of split string
		public int drawSplitString(MatrixStack matrices, String text, double x, double y, Color color, double width) {
			// then text
			List<String> lines = new ArrayList<>();
			StringBuilder currLine = new StringBuilder();
			double currWidth = 0;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);

				// dont add formatting to width
				if (c == 167 && i + 1 < text.length()) {
					currLine.append(c).append(text.charAt(i + 1));
					++i;
				}

				// break lines at \n
				else if (c == '\n') {
					if (i == text.length() - 1)
						continue;
					lines.add(currLine.toString());
					currLine = new StringBuilder();
					currWidth = 0;
				}

				// write char if it fits, if it doesnt, make a new line
				else {
					if (currWidth + getCharWidth(c) > width) {
						lines.add(currLine.toString());
						currLine = new StringBuilder().append(c);
						currWidth = 0;
					} else {
						currLine.append(c);
						currWidth += getCharWidth(c);
					}
				}
			}
			if (!currLine.toString().equals(""))
				lines.add(currLine.toString());

			RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			for (String line : lines) {
				drawString(matrices, line, x, y);
				y += getFontHeight();
			}

			return lines.size() * getFontHeight();
		}

		public double getCharWidth(char c) {
			Glyph glyph = characterGlyphMap.get(c);
			if (glyph == null)
				return 0;
			return glyph.width * glyphSize;
		}

		public double getStringWidth(String text) {
			double width = 0;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == 167 && i + 1 < text.length())
					i++;
				else
					width += getCharWidth(c);
			}

			return width;
		}

		public double getStringWidth(String text, double height) {
			return getStringWidth(text) * (height / getFontHeight());
		}

		public int getFontHeight() {
			return (int) (charHeight * glyphSize);
		}

		private void color(int color) {
			float red = (float) (color >> 16 & 255) / 255.0F;
			float blue = (float) (color >> 8 & 255) / 255.0F;
			float green = (float) (color & 255) / 255.0F;
			RenderSystem.setShaderColor(red, blue, green, 1);
		}
	}

	static class Glyph {
		double x;
		double y;
		double width;
		double height;

		Glyph(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
}