package me.infinity.utils;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * Only 2D please
 *
 */
public class RenderUtil {

	public static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	private final static MatrixStack matrixStack = new MatrixStack();

	public static MatrixStack getMatrixStack() {
		return matrixStack;
	}

	public static Matrix4f getMatrix() {
		return matrixStack.peek().getModel();
	}

	public static int getStringWidth(String str) {
		return textRenderer.getWidth(str);
	}

	public static int getFontHeight() {
		return textRenderer.fontHeight;
	}

	public void setColor(Color color) {
		GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f,
				color.getAlpha() / 255.0f);
	}

	public void setColor(int rgba) {
		int r = rgba & 0xFF;
		int g = rgba >> 8 & 0xFF;
		int b = rgba >> 16 & 0xFF;
		int a = rgba >> 24 & 0xFF;
		GL11.glColor4b((byte) r, (byte) g, (byte) b, (byte) a);
	}

	public int toRGBA(Color c) {
		return c.getRed() | c.getGreen() << 8 | c.getBlue() << 16 | c.getAlpha() << 24;
	}

	/**
	 * drawing String text with help TextRenderer
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public void draw(String text, float x, float y, int color) {
		MatrixStack matrix = getMatrixStack();
		Helper.minecraftClient.textRenderer.draw(matrix, text, x, y, color);
	}

	public static void drawCenteredString(String text, double x, double y, int color) {
		MatrixStack matrix = getMatrixStack();
		Helper.minecraftClient.textRenderer.draw(matrix, text, (float) x - textRenderer.getWidth(text) / 2, (float) y,
				color);
	}

	/**
	 * method for other drawRect with support GL
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 * @param mode
	 */
	public static void fill(int mode, double x1, double y1, double x2, double y2, int color) {
		Matrix4f matrix = getMatrix();
		double j;
		if (x1 < x2) {
			j = x1;
			x1 = x2;
			x2 = j;
		}

		if (y1 < y2) {
			j = y1;
			y1 = y2;
			y2 = j;
		}

		float f = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float h = (float) (color >> 8 & 255) / 255.0F;
		float k = (float) (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(mode, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * Full drawRect for render ui
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param color
	 */
	public static void drawRect(double x, double y, double w, double h, int color) {
		fill(GL11.GL_QUADS, x / 2.0, y / 2.0, x / 2.0 + w / 2.0, y / 2.0 + h / 2.0, color);
	}

	/**
	 * Draw Outline (box) method for render ui
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param color
	 */
	public static void drawOutline(double x, double y, double w, double h, int color) {
		fill(GL11.GL_LINE_LOOP, x / 2.0, y / 2.0, x / 2.0 + w / 2.0, y / 2.0 + h / 2.0, color);
	}

	private static void bindSkinTexture(String name) {
		Identifier location = AbstractClientPlayerEntity.getSkinId(name);

		try {
			PlayerSkinTexture img = AbstractClientPlayerEntity.loadSkin(location, name);
			img.load(Helper.minecraftClient.getResourceManager());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Helper.minecraftClient.getTextureManager().bindTexture(location);
	}

	public static void drawFace(MatrixStack matrixStack, String name, int x, int y, int w, int h, boolean selected) {
		try {
			bindSkinTexture(name);
			GL11.glEnable(GL11.GL_BLEND);

			if (selected)
				GL11.glColor4f(1, 1, 1, 1);
			else
				GL11.glColor4f(0.9F, 0.9F, 0.9F, 1);

			int fw = 192;
			int fh = 192;
			float u = 24;
			float v = 24;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			GL11.glDisable(GL11.GL_BLEND);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
