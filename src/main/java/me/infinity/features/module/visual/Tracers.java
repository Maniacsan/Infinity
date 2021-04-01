package me.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.clickmenu.ClickMenu;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.render.WorldRender;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Draw lines / arrows to entities", key = -2, name = "Tracers", visible = true)
public class Tracers extends Module {

	private Settings mode = new Settings(this, "Mode", "Arrows", new ArrayList<>(Arrays.asList("Lines", "Arrows")),
			() -> true);

	// lines
	private Settings width = new Settings(this, "Width", 2.0f, 0.5f, 3.5f,
			() -> mode.getCurrentMode().equalsIgnoreCase("Lines"));

	// arrows
	private Settings radius = new Settings(this, "Radius", 1.6D, 0.5D, 8.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Arrows"));
	private Settings size = new Settings(this, "Size", 3.0D, 1.0D, 8.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Arrows"));

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", true, () -> players.isToggle());

	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	// colors
	private Settings playerColor = new Settings(this, "Player Color", new Color(120, 97, 238),
			() -> players.isToggle());
	private Settings friendsColor = new Settings(this, "Friends Color", new Color(247, 251, 247),
			() -> players.isToggle() && friends.isToggle());
	private Settings mobsColor = new Settings(this, "Mobs Color", new Color(236, 173, 24), () -> mobs.isToggle());
	private Settings animalsColor = new Settings(this, "Animals Color", new Color(108, 234, 42),
			() -> animals.isToggle());

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		if (mode.getCurrentMode().equalsIgnoreCase("Arrows")) {

			for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle())) {

				if (Helper.minecraftClient.currentScreen != null && !(Helper.minecraftClient.currentScreen instanceof ClickMenu))
					return;

				float[] toRot = RotationUtils.lookAtEntity(e, 360, 360);
				float yaw = RotationUtils.getYaw(e) + Helper.getPlayer().yaw;

				// normalize 
				yaw = (float) Math.toRadians(yaw);

				int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
						friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(),
						animalsColor.getColor().getRGB());

				Render2D.drawTriangle((width / 2) - (int) getX(yaw), height / 2 - (int) getY(yaw),
						(int) size.getCurrentValueDouble(), (int) (toRot[0] - Helper.getPlayer().yaw), color);
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
						.rotateX((float) (-Math.toRadians(Helper.minecraftClient.cameraEntity.pitch)))
						.rotateY((float) (-Math.toRadians(Helper.minecraftClient.cameraEntity.yaw)))
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
