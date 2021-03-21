package me.infinity.features.module.visual;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.event.EntityTagEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Makes name tags convenient", key = -2, name = "NameTags", visible = true)
public class NameTags extends Module {

	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", true, () -> players.isToggle());

	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	private Settings scale = new Settings(this, "Scale", 0.35D, 0.2D, 0.6D, () -> true);

	@EventTarget
	public void onTagRender(EntityTagEvent event) {
		renderTag(event.getMatrices());
		event.cancel();
	}

	// EntityRenderer.class, method = renderLabelIfPresent
	public void renderTag(MatrixStack matrices) {
		for (Entity entity : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle())) {
			float calcScale = (float) Math.max(
					Math.min(Helper.getPlayer().distanceTo(entity) / (100 / scale.getCurrentValueDouble()), 2.4 / 50),
					1 / 80d);
			String tag = entity.getDisplayName().getString() + " " + getHealthColor((LivingEntity) entity)
					+ ((LivingEntity) entity).getHealth();

			float f = entity.getHeight() + 0.5F;

			int i = "spray123".equals(entity.getDisplayName().getString()) ? -10 : 0;

			matrices.push();
			matrices.translate(0.0D, (double) f, 0.0D);
			matrices.multiply(Helper.minecraftClient.getEntityRenderDispatcher().getRotation());
			matrices.scale(-calcScale, -calcScale, calcScale);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableDepthTest();

			float h = (float) (-FontUtils.getStringWidth(tag) / 2);
			Render2D.drawRect(matrices, -h + 3, i - 2, h - 3, 10, 0x90000000);

			FontUtils.drawString(matrices, tag, h, i, 0xFFD1D1D1);

			matrices.pop();
		}
	}

	private Formatting getHealthColor(LivingEntity e) {
		if (e.getHealth() + e.getAbsorptionAmount() >= e.getMaxHealth())
			return Formatting.GREEN;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.7)
			return Formatting.WHITE;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.4)
			return Formatting.GOLD;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.1)
			return Formatting.RED;
		return Formatting.WHITE;
	}
}
