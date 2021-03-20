package me.infinity.utils.render;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class RenderUtil {

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
