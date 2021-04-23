package org.infinity.clickmenu.util;

import java.awt.Color;

import org.infinity.InfMain;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.utils.Helper;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class Render2D {

	public static int getScaledWidth() {
		return MinecraftClient.getInstance().getWindow().getScaledWidth();
	}

	public static int getScaledHeight() {
		return MinecraftClient.getInstance().getWindow().getScaledHeight();
	}

	public static void drawRect(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
		DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
	}

	public static void drawRect(MatrixStack matrices, double x1, double y1, double x2, double y2, int color) {
		fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
	}

	public static void drawRectWH(MatrixStack matrices, int x, int y, int width, int height, int color) {
		DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
	}

	public static void drawRectWH(MatrixStack matrices, double x, double y, double width, double height, int color) {
		fill(matrices.peek().getModel(), x, y, x + width, y + height, color);
	}

	public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glLineWidth(lineWidth);
		GL11.glEnable(2848);
		GL11.glBegin(2);
		for (int i = 0; i <= 360; i++) {
			GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius,
					y + Math.cos(i * 3.141526D / 180.0D) * radius);
		}
		GL11.glEnd();
		GL11.glDisable(2848);
	}

	public static void fillGradient(MatrixStack matrices, double xStart, double yStart, double xEnd, double yEnd,
			int colorStart, int colorEnd) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		fillGradient(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, 0, colorStart, colorEnd);
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	protected static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, double xStart, double yStart,
			double xEnd, double yEnd, int z, int colorStart, int colorEnd) {
		float f = (float) (colorStart >> 24 & 255) / 255.0F;
		float g = (float) (colorStart >> 16 & 255) / 255.0F;
		float h = (float) (colorStart >> 8 & 255) / 255.0F;
		float i = (float) (colorStart & 255) / 255.0F;
		float j = (float) (colorEnd >> 24 & 255) / 255.0F;
		float k = (float) (colorEnd >> 16 & 255) / 255.0F;
		float l = (float) (colorEnd >> 8 & 255) / 255.0F;
		float m = (float) (colorEnd & 255) / 255.0F;
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yStart, (float) z).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yStart, (float) z).color(0, 0, 0, 100).next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yEnd, (float) z).color(g, h, i, f).next();
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yEnd, (float) z).color(k, l, m, j).next();
	}

	public static void drawHorizontalLine(MatrixStack matrices, int i, int j, int k, int l) {
		if (j < i) {
			int m = i;
			i = j;
			j = m;
		}

		drawRect(matrices, i, k, j + 1, k + 1, l);
	}

	public static void drawVerticalLine(MatrixStack matrices, int i, int j, int k, int l) {
		if (k < j) {
			int m = j;
			j = k;
			k = m;
		}

		drawRect(matrices, i, j + 1, i + 1, k, l);
	}

	public static void drawAngleRect(MatrixStack matrices, double x, double y, double width, double height, int color) {
		drawFullCircle(x + 8, y + height / 2, height / 2, color);
		drawFullCircle(x + width - 3, y + height / 2, height / 2, color);
		drawRectWH(matrices, x + 8, y, width - 11, height, color);
	}

	public static void drawFullCircle(double cx, double cy, double r, int c) {
		r *= 2.0;
		cx *= 2;
		cy *= 2;
		float f = (float) (c >> 24 & 255) / 255.0f;
		float f1 = (float) (c >> 16 & 255) / 255.0f;
		float f2 = (float) (c >> 8 & 255) / 255.0f;
		float f3 = (float) (c & 255) / 255.0f;
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
		GL11.glBegin((int) 6);
		int i = 0;
		while (i <= 360) {
			double x = Math.sin((double) i * 3.141592653589793 / 180.0) * r;
			double y = Math.cos((double) i * 3.141592653589793 / 180.0) * r;
			GL11.glVertex2d((double) ((double) cx + x), (double) ((double) cy + y));
			++i;
		}
		GL11.glEnd();
		GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public static void fill(Matrix4f matrix4f, double x1, double y1, double x2, double y2, int color) {
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
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * Treugolnik povernutiy na pravo
	 * 
	 * @param x
	 * @param y
	 * @param size
	 * @param color
	 */
	public static void drawRightTriangle(int x, int y, int size, int color) {
		float f = (color >> 24 & 0xFF) / 255.0F;
		float f1 = (color >> 16 & 0xFF) / 255.0F;
		float f2 = (color >> 8 & 0xFF) / 255.0F;
		float f3 = (color & 0xFF) / 255.0F;
		GL11.glPushMatrix();
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glBegin(4);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d((x - size), (y - size));
		GL11.glVertex2d((x - size), (y + size));
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public static void setColor(Color color) {
		GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f,
				color.getAlpha() / 255.0f);
	}

	public static void setColor(int rgba) {
		int r = rgba & 0xFF;
		int g = rgba >> 8 & 0xFF;
		int b = rgba >> 16 & 0xFF;
		int a = rgba >> 24 & 0xFF;
		GL11.glColor4b((byte) r, (byte) g, (byte) b, (byte) a);
	}

	public static int toRGBA(Color c) {
		return c.getRed() | c.getGreen() << 8 | c.getBlue() << 16 | c.getAlpha() << 24;
	}

	public static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
	}

	public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
		return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
	}

	public static boolean isFillHovered(double x, double y, double minX, double minY, double maxX, double maxY) {
		if (x > minX && x < maxX && y > minY && y < maxY)
			return true;
		return false;
	}

	public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
		return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
	}

	public static void drawTriangle(int x, int y, int size, int rotation, int color) {
		GL11.glTranslated(x, y, 0);
		GL11.glRotatef(180 + rotation, 0F, 0F, 1.0F);

		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;

		GL11.glColor4f(red, green, blue, alpha);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(1);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);

		GL11.glVertex2d(0, (1.0F * size));
		GL11.glVertex2d((1 * size), -(1.0F * size));
		GL11.glVertex2d(-(1 * size), -(1.0F * size));

		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glRotatef(-180 - rotation, 0F, 0F, 1.0F);
		GL11.glTranslated(-x, -y, 0);
	}

	// scroll
	public static void startScissor(double x, double y, double width, double height) {
		double scaleWidth = (double) Helper.minecraftClient.getWindow().getWidth()
				/ Helper.minecraftClient.getWindow().getScaledWidth();
		double scaleHeight = (double) Helper.minecraftClient.getWindow().getHeight()
				/ Helper.minecraftClient.getWindow().getScaledHeight();

		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glScissor((int) (x * scaleWidth),
				(int) ((Helper.minecraftClient.getWindow().getHeight()) - (int) ((y + height) * scaleHeight)),
				(int) (width * scaleWidth), (int) (height * scaleHeight));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	// for clickmenu scale
	public static void startMenuScissor(double x, double y, double width, double height) {
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();
		double scaleWidth = (double) Helper.minecraftClient.getWindow().getWidth()
				/ Helper.minecraftClient.getWindow().getScaledWidth();
		double scaleHeight = (double) Helper.minecraftClient.getWindow().getHeight()
				/ Helper.minecraftClient.getWindow().getScaledHeight();

		scaleWidth *= scale;
		scaleHeight *= scale;

		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glScissor((int) (x * scaleWidth),
				(int) ((Helper.minecraftClient.getWindow().getHeight()) - (int) ((y + height) * scaleHeight)),
				(int) (width * scaleWidth), (int) (height * scaleHeight));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	public static void stopScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopAttrib();
	}
}
