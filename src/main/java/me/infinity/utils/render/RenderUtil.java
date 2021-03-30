package me.infinity.utils.render;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

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

	public static MatrixStack drawGuiItem(double x, double y, double z, double offX, double offY, double scale,
			ItemStack item) {
		MatrixStack matrix = matrixFrom(x, y, z);

		Camera camera = Helper.minecraftClient.gameRenderer.getCamera();
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

		matrix.scale((float) scale, (float) scale, 0.001f);
		matrix.translate(offX, offY, 0);

		if (item.isEmpty())
			return matrix;

		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f));

		Helper.minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();

		DiffuseLighting.disableGuiDepthLighting();
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		Helper.minecraftClient.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
				OverlayTexture.DEFAULT_UV, matrix,
				Helper.minecraftClient.getBufferBuilders().getEntityVertexConsumers());

		Helper.minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-180f));

		return matrix;
	}

	public static MatrixStack drawText(String str, double x, double y, double z, double scale) {
		MatrixStack matrix = matrixFrom(x, y, z);

		Camera camera = Helper.minecraftClient.gameRenderer.getCamera();
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_ALWAYS);

		matrix.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);

		int i = Helper.minecraftClient.textRenderer.getWidth(str) / 2;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, VertexFormats.POSITION_COLOR);
		float f = Helper.minecraftClient.options.getTextBackgroundOpacity(0.3F);
		bufferbuilder.vertex(matrix.peek().getModel(), -i - 1, -1, 0.0f).color(0.0F, 0.0F, 0.0F, f).next();
		bufferbuilder.vertex(matrix.peek().getModel(), -i - 1, 8, 0.0f).color(0.0F, 0.0F, 0.0F, f).next();
		bufferbuilder.vertex(matrix.peek().getModel(), i + 1, 8, 0.0f).color(0.0F, 0.0F, 0.0F, f).next();
		bufferbuilder.vertex(matrix.peek().getModel(), i + 1, -1, 0.0f).color(0.0F, 0.0F, 0.0F, f).next();
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Helper.minecraftClient.textRenderer.draw(matrix, str, -i, 0, 553648127);
		Helper.minecraftClient.textRenderer.draw(matrix, str, -i, 0, -1);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		return matrix;
	}

	public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrix = new MatrixStack();

		Camera camera = Helper.minecraftClient.gameRenderer.getCamera();
		matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

		matrix.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrix;
	}

	public static void drawItem(ItemStack itemStack, int x, int y, boolean overlay) {
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();
		DiffuseLighting.enable();
		Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(itemStack, x, y);
		if (overlay)
			Helper.minecraftClient.getItemRenderer().renderGuiItemOverlay(Helper.minecraftClient.textRenderer,
					itemStack, x, y, null);
		DiffuseLighting.disable();
		DiffuseLighting.disable();
		RenderSystem.enableDepthTest();
	}

	public static void drawTexture(MatrixStack matrices, Identifier ident, double x, double y, double width,
			double height) {
		Helper.minecraftClient.getTextureManager().bindTexture(ident);
		DrawableHelper.drawTexture(matrices, (int) x, (int) y, 0, 0, (int) width, (int) height, (int) width,
				(int) height);
	}

}
