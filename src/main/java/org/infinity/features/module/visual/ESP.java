package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.RenderEntityEvent;
import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.features.module.combat.KillAura;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Category.VISUAL, desc = "Draw entity esp", key = -2, name = "ESP", visible = true)
public class ESP extends Module {

	private Setting mode = new Setting(this, "Mode", "Box", new ArrayList<>(Arrays.asList("Fill", "Box", "Vanilla")));

	private Setting width = new Setting(this, "Width", 1.1f, 0.5f, 3.0f)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Box"));

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", true).setVisible(() -> players.isToggle());

	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);

	// colors
	private Setting playerColor = new Setting(this, "Player Color", new Color(247, 251, 247))
			.setVisible(() -> players.isToggle());
	private Setting friendsColor = new Setting(this, "Friends Color", new Color(247, 251, 247))
			.setVisible(() -> players.isToggle() && friends.isToggle());
	private Setting mobsColor = new Setting(this, "Mobs Color", new Color(236, 173, 24))
			.setVisible(() -> mobs.isToggle());
	private Setting animalsColor = new Setting(this, "Animals Color", new Color(108, 234, 42))
			.setVisible(() -> animals.isToggle());

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
		if (event.getType().equals(EventType.POST)) {
			for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle())) {

				int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
						friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(),
						animalsColor.getColor().getRGB());

				if (e == KillAura.target)
					color = Color.RED.getRGB();

				if (mode.getCurrentMode().equalsIgnoreCase("Fill"))
					WorldRender.drawFill(e.getBoundingBox(), color);
				else if (mode.getCurrentMode().equalsIgnoreCase("Box"))
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
