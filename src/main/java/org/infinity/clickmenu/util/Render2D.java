package org.infinity.clickmenu.util;

import java.awt.Color;

import org.infinity.utils.Helper;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
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
		fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
	}

	public static void drawRect(MatrixStack matrices, double x1, double y1, double x2, double y2, int color) {
		fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
	}

	public static void drawRectWH(MatrixStack matrices, int x, int y, int width, int height, int color) {
		fill(matrices.peek().getModel(), x, y, x + width, y + height, color);
	}

	public static void drawRectWH(MatrixStack matrices, double x, double y, double width, double height, int color) {
		fill(matrices.peek().getModel(), x, y, x + width, y + height, color);
	}

	public static void drawUnfilledCircle(double x, double y, float radius, float lineWidth, int color) {
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

	}

	public static void verticalGradient(MatrixStack matrix, double x1, double y1, double x2, double y2, int color1,
			int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
		float red1 = (color1 >> 16 & 255) / 255.0F;
		float green1 = (color1 >> 8 & 255) / 255.0F;
		float blue1 = (color1 & 255) / 255.0F;
		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2 = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2 = (color2 & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(x2, y1, 0).color(red1, green1, blue1, alpha1).next();
		bufferBuilder.vertex(x1, y1, 0).color(red1, green1, blue1, alpha1).next();
		bufferBuilder.vertex(x1, y2, 0).color(red2, green2, blue2, alpha2).next();
		bufferBuilder.vertex(x2, y2, 0).color(red2, green2, blue2, alpha2).next();
		tessellator.draw();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	public static void horizontalGradient(MatrixStack matrix, double x1, double y1, double x2, double y2, int color1,
			int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
		float red1 = (color1 >> 16 & 255) / 255.0F;
		float green1 = (color1 >> 8 & 255) / 255.0F;
		float blue1 = (color1 & 255) / 255.0F;
		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2 = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2 = (color2 & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(x1, y1, 0).color(red1, green1, blue1, alpha1).next();
		bufferBuilder.vertex(x1, y2, 0).color(red1, green1, blue1, alpha1).next();
		bufferBuilder.vertex(x2, y2, 0).color(red2, green2, blue2, alpha2).next();
		bufferBuilder.vertex(x2, y1, 0).color(red2, green2, blue2, alpha2).next();
		tessellator.draw();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();

	}

	public static void drawBorderedRect(MatrixStack matrices, double xPos, double yPos, double width, double height,
			float lineWidth, int lineColor, int bgColor) {
		drawRectWH(matrices, xPos, yPos, width, height, bgColor);
		float f = (float) (lineColor >> 24 & 255) / 255.0F;
		float f1 = (float) (lineColor >> 16 & 255) / 255.0F;
		float f2 = (float) (lineColor >> 8 & 255) / 255.0F;
		float f3 = (float) (lineColor & 255) / 255.0F;

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
		float f = (float) (color >> 24 & 255) / 255.0F;
		float f1 = (float) (color >> 16 & 255) / 255.0F;
		float f2 = (float) (color >> 8 & 255) / 255.0F;
		float f3 = (float) (color & 255) / 255.0F;

	}

	public static void drawOutline(MatrixStack matrixStack, double x1, double x2, double y1, double y2, int color) {
		float f = (float) (color >> 24 & 255) / 255.0F;
		float f1 = (float) (color >> 16 & 255) / 255.0F;
		float f2 = (float) (color >> 8 & 255) / 255.0F;
		float f3 = (float) (color & 255) / 255.0F;

		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		RenderSystem.setShaderColor(f1, f2, f3, f);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0F).next();
		bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0F).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0F).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0F).next();
		bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0F).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public static void drawBorderedCircle(float x, float y, float radius, int lineWidth, int outsideC, int insideC) {
		drawCircle(x, y, radius, insideC);
		drawUnfilledCircle(x, y, radius, (float) lineWidth, outsideC);
	}

	public static void drawCircle(double x, double y, double radius, int color) {
		float f = (float) (color >> 24 & 255) / 255.0F;
		float f1 = (float) (color >> 16 & 255) / 255.0F;
		float f2 = (float) (color >> 8 & 255) / 255.0F;
		float f3 = (float) (color & 255) / 255.0F;

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
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void drawRightTriangle(int x, int y, int size, int color) {
		float f = (color >> 24 & 0xFF) / 255.0F;
		float f1 = (color >> 16 & 0xFF) / 255.0F;
		float f2 = (color >> 8 & 0xFF) / 255.0F;
		float f3 = (color & 0xFF) / 255.0F;
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

	}

	public static void startScissor(MatrixStack matrices, double x, double y, double width, double height) {
		double scaleWidth = (double) Helper.minecraftClient.getWindow().getWidth()
				/ Helper.minecraftClient.getWindow().getScaledWidth();
		double scaleHeight = (double) Helper.minecraftClient.getWindow().getHeight()
				/ Helper.minecraftClient.getWindow().getScaledHeight();

		RenderSystem.enableScissor((int) (x * scaleWidth),
				(int) ((Helper.minecraftClient.getWindow().getHeight()) - (int) ((y + height) * scaleHeight)),
				(int) (width * scaleWidth), (int) (height * scaleHeight));
	}

	public static void stopScissor(MatrixStack matrices) {
		RenderSystem.disableScissor();
	}
}
