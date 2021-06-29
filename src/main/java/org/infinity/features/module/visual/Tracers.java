package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.clickmenu.util.Render2D;
import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.WorldRender;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Category.VISUAL, desc = "Draw lines / arrows to entities", key = -2, name = "Tracers", visible = true)
public class Tracers extends Module {

	private Setting mode = new Setting(this, "Mode", "Arrows", new ArrayList<>(Arrays.asList("Lines", "Arrows")));

	// lines
	private Setting width = new Setting(this, "Width", 2.0f, 0.5f, 3.5f)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Lines"));

	// arrows
	private Setting radius = new Setting(this, "Radius", 1.4D, 0.5D, 8.0D)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Arrows"));
	private Setting size = new Setting(this, "Size", 9.0D, 5.0D, 20.0D)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Arrows"));

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", true).setVisible(() -> players.isToggle());

	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);

	// colors
	private Setting playerColor = new Setting(this, "Player Color", new Color(120, 97, 238))
			.setVisible(() -> players.isToggle());
	private Setting friendsColor = new Setting(this, "Friends Color", new Color(247, 251, 247))
			.setVisible(() -> players.isToggle() && friends.isToggle());
	private Setting mobsColor = new Setting(this, "Mobs Color", new Color(236, 173, 24))
			.setVisible(() -> mobs.isToggle());
	private Setting animalsColor = new Setting(this, "Animals Color", new Color(108, 234, 42))
			.setVisible(() -> animals.isToggle());

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		if (mode.getCurrentMode().equalsIgnoreCase("Arrows")) {

			for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle())) {

				if (Helper.minecraftClient.currentScreen != null)
					return;

				float[] toRot = RotationUtil.lookAtEntity(e);
				float yaw = RotationUtil.getYaw(e) + Helper.getPlayer().getYaw();
				yaw = (float) Math.toRadians(yaw);

				int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
						friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(),
						animalsColor.getColor().getRGB());

				Render2D.drawTriangle(matrices, (width / 2) - (int) getX(yaw), height / 2 - (int) getY(yaw),
						(int) size.getCurrentValueDouble(), (int) (toRot[0] - Helper.getPlayer().getYaw()), color);
			}
		}
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("Lines")) {

			for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle())) {

				Vec3d pos = e.getPos();

				Vec3d eyeVector = new Vec3d(0.0, 0.0, 75)
						.rotateX((float) (-Math.toRadians(Helper.minecraftClient.cameraEntity.getPitch())))
						.rotateY((float) (-Math.toRadians(Helper.minecraftClient.cameraEntity.getYaw())))
						.add(Helper.minecraftClient.cameraEntity.getPos().add(0, Helper.minecraftClient.cameraEntity
								.getEyeHeight(Helper.minecraftClient.cameraEntity.getPose()), 0));

				int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
						friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(),
						animalsColor.getColor().getRGB());

				WorldRender.drawLine(pos.x, pos.y, pos.z, eyeVector.x, eyeVector.y, eyeVector.z,
						width.getCurrentValueFloat(), color);
				WorldRender.drawLine(pos.x, pos.y, pos.z, pos.x, pos.y + e.getHeight(), pos.z,
						width.getCurrentValueFloat(), color);
			}
		}
	}

	private double getX(double rad) {
		return Math.sin(rad) * (radius.getCurrentValueDouble() * 40);
	}

	private double getY(double rad) {
		return Math.cos(rad) * Math.sin(Math.toRadians(MathHelper.clamp(90.0f, -90.0f, 90.0f)))
				* (radius.getCurrentValueDouble() * 40);
	}

}
