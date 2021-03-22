package me.infinity.utils.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WorldRender {

	public static void drawFill(BlockPos pos, int color) {
		drawFill(new Box(pos), color);
	}

	public static void drawFill(Box box, int color) {
		float a = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		gl11Setup();

		// Fill
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(5, VertexFormats.POSITION_COLOR);
		WorldRenderer.drawBox(buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a / 2f);
		tessellator.draw();

		gl11Cleanup();
	}

	public static void drawBox(Box box, float width, int color) {
		float a = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		gl11Setup();

		GL11.glLineWidth(width);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(3, VertexFormats.POSITION_COLOR);
		buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.minY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.maxY, box.minZ).color(r, g, b, a).next();
		buffer.vertex(box.minX, box.minY, box.maxZ).color(r, g, b, 0f).next();
		buffer.vertex(box.minX, box.maxY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.minY, box.maxZ).color(r, g, b, 0f).next();
		buffer.vertex(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).next();
		buffer.vertex(box.maxX, box.minY, box.minZ).color(r, g, b, 0f).next();
		buffer.vertex(box.maxX, box.maxY, box.minZ).color(r, g, b, a).next();
		tessellator.draw();

		gl11Cleanup();
	}

	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, float width,
			int color) {
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		gl11Setup();
		GL11.glLineWidth(width);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(3, VertexFormats.POSITION_COLOR);
		buffer.vertex(x1, y1, z1).color(r, g, b, 0.0F).next();
		buffer.vertex(x1, y1, z1).color(r, g, b, 1.0F).next();
		buffer.vertex(x2, y2, z2).color(r, g, b, 1.0F).next();
		tessellator.draw();

		gl11Cleanup();

	}

	public static void offsetRender() {
		Camera camera = BlockEntityRenderDispatcher.INSTANCE.camera;
		Vec3d camPos = camera.getPos();
		GL11.glRotated(MathHelper.wrapDegrees(camera.getPitch()), 1, 0, 0);
		GL11.glRotated(MathHelper.wrapDegrees(camera.getYaw() + 180.0), 0, 1, 0);
		GL11.glTranslated(-camPos.x, -camPos.y, -camPos.z);
	}

	public static void gl11Setup() {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glLineWidth(2.5F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		offsetRender();
	}

	public static void gl11Cleanup() {
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
