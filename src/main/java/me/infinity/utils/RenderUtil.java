package me.infinity.utils;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
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
		Helper.minecraftClient.textRenderer.draw(matrix, text, (float) x - textRenderer.getWidth(text) / 2, (float) y, color);
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
	
	/**
	 * Pryamougolnik s kruglimi uglami (avtor hz nashol v flewke)
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param borderC
	 * @param insideC
	 */
    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
    }
    
    /**
     * Horizontal Line
     * @param par1
     * @param par2
     * @param par3
     * @param par4
     */
    public static void drawHLine(float par1, float par2, float par3, int par4) {
        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }
        drawRect(par1, par3, par2 + 1.0f, par3 + 1.0f, par4);
    }

    /**
     * Verical Line
     * @param par1
     * @param par2
     * @param par3
     * @param par4
     */
    public static void drawVLine(float par1, float par2, float par3, int par4) {
        if (par3 < par2) {
            float var5 = par2;
            par2 = par3;
            par3 = var5;
        }
        drawRect(par1, par2 + 1.0f, par1 + 1.0f, par3, par4);
    }


}
