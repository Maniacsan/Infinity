package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.RenderEntityEvent;
import org.infinity.event.RenderEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.features.module.combat.KillAura;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Draw entity esp", key = -2, name = "ESP", visible = true)
public class ESP extends Module {

	private Settings mode = new Settings(this, "Mode", "Fill", new ArrayList<>(Arrays.asList("Fill", "Box", "Vanilla")),
			() -> true);

	private Settings width = new Settings(this, "Width", 2.0f, 0.5f, 3.0f,
			() -> mode.getCurrentMode().equalsIgnoreCase("Box"));

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", true, () -> players.isToggle());

	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	// colors
	private Settings playerColor = new Settings(this, "Player Color", new Color(247, 251, 247),
			() -> players.isToggle());
	private Settings friendsColor = new Settings(this, "Friends Color", new Color(247, 251, 247),
			() -> players.isToggle() && friends.isToggle());
	private Settings mobsColor = new Settings(this, "Mobs Color", new Color(236, 173, 24), () -> mobs.isToggle());
	private Settings animalsColor = new Settings(this, "Animals Color", new Color(108, 234, 42),
			() -> animals.isToggle());

	private int lastWidth = -1;
	private int lastHeight = -1;
	private double lastShaderWidth;

	private boolean shaderUnloaded = true;

	@Override
	public void onDisable() {
		for (Entity e : Helper.getWorld().getEntities()) {
			if (e != Helper.getPlayer()) {
				if (e.isGlowing()) {
					e.setGlowing(false);
				}
			}
		}

	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle())) {

			int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
					friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(), animalsColor.getColor().getRGB());

			if (e == KillAura.target) {
				color = Color.RED.getRGB();
			}

			if (mode.getCurrentMode().equalsIgnoreCase("Fill")) {
				WorldRender.drawFill(e.getBoundingBox(), color);

			} else if (mode.getCurrentMode().equalsIgnoreCase("Box")) {
				WorldRender.drawBox(e.getBoundingBox(), width.getCurrentValueFloat(), color);

			}
		}
	}

	@EventTarget
	public void onEntityRender(RenderEntityEvent event) {
		int color = EntityUtil.getEntitiesColor(event.getEntity(), playerColor.getColor().getRGB(),
				friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(), animalsColor.getColor().getRGB());

		if (event.getEntity() == KillAura.target) {
			color = Color.RED.getRGB();
		}

		if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
			if (EntityUtil.isTarget(event.getEntity(), players.isToggle(), friends.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle())) {
				event.setVertex(getOutline(Helper.minecraftClient.getBufferBuilders(), color));
				event.getEntity().setGlowing(true);
			} else {
				event.getEntity().setGlowing(false);
			}
		} else {
			event.getEntity().setGlowing(false);
		}
	}

	private VertexConsumerProvider getOutline(BufferBuilderStorage buffers, int color) {
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		OutlineVertexConsumerProvider ovsp = buffers.getOutlineVertexConsumers();
		ovsp.setColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
		return ovsp;
	}
}