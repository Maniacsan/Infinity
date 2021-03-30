package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import me.infinity.event.EntityTagEvent;
import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.render.WorldRender;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Makes name tags convenient", key = -2, name = "NameTags", visible = true)
public class NameTags extends Module {

	private Settings armor = new Settings(this, "Armor", true, () -> true);

	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", true, () -> players.isToggle());

	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	private Settings scale = new Settings(this, "Scale", 2D, 0.2D, 5D, () -> true);

	@EventTarget
	public void onTagRender(EntityTagEvent event) {
		if (EntityUtil.isTarget(event.getEntity(), players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle()))
			event.cancel();
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity entity : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle())) {
			List<String> lines = new ArrayList<>();
			double scale = 0;

			Vec3d rPos = getRenderPos(entity);

			if (entity instanceof LivingEntity) {
				if (entity == Helper.minecraftClient.player || entity.hasPassenger(Helper.minecraftClient.player)
						|| Helper.minecraftClient.player.hasPassenger(entity))
					return;

				LivingEntity e = (LivingEntity) entity;

				String health = getHealthText(e);

				scale = Math.max(this.scale.getCurrentValueDouble()
						* (Helper.minecraftClient.cameraEntity.distanceTo(entity) / 20), 1);

				lines.add(entity.getDisplayName().getString() + " " + health);

				/* Drawing Items */
				double c = 0;
				double lscale = scale * 0.4;
				double up = ((0.3 + lines.size() * 0.25) * scale) + lscale / 2;

				if (armor.isToggle()) {
					drawItem(rPos.x, rPos.y + up, rPos.z, 2.5, 0, lscale, e.getEquippedStack(EquipmentSlot.MAINHAND));
					drawItem(rPos.x, rPos.y + up, rPos.z, -2.5, 0, lscale, e.getEquippedStack(EquipmentSlot.OFFHAND));

					for (ItemStack i : e.getArmorItems()) {
						drawItem(rPos.x, rPos.y + up, rPos.z, c - 1.5, 0, lscale, i);
						c++;
					}
				}
			}

			if (!lines.isEmpty()) {
				float offset = 0.25f + lines.size() * 0.25f;

				for (String s : lines) {
					WorldRender.drawText(s, rPos.x, rPos.y + (offset * scale), rPos.z, scale);

					offset -= 0.25f;
				}
			}
		}
	}

	private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		MatrixStack matrix = WorldRender.draw3DItem(x, y, z, offX, offY, scale, item);

		matrix.scale(-0.05F, -0.05F, 0.05f);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);

		matrix.scale(0.75F, 0.75F, 1F);

		int c = 0;
		for (Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
			String text = I18n.translate(m.getKey().getName(2).getString());

			if (text.isEmpty())
				continue;

			String subText = text.substring(0, 2) + m.getValue();

			int w1 = Helper.minecraftClient.textRenderer.getWidth(subText) / 2;
			// hz vashe kak 0 - w1 rabotaet ,no bez nego krivo
			Helper.minecraftClient.textRenderer.drawWithShadow(matrix, subText, 0 - w1, c * 12 - 24, 0xFFFFFFFF);
			c--;
		}

		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableDepthTest();

		RenderSystem.disableBlend();
	}

	private Vec3d getRenderPos(Entity e) {
		return Helper.minecraftClient.currentScreen != null && Helper.minecraftClient.currentScreen.isPauseScreen()
				? e.getPos().add(0, e.getHeight(), 0)
				: new Vec3d(e.lastRenderX + (e.getX() - e.lastRenderX) * Helper.minecraftClient.getTickDelta(),
						(e.lastRenderY + (e.getY() - e.lastRenderY) * Helper.minecraftClient.getTickDelta())
								+ e.getHeight(),
						e.lastRenderZ + (e.getZ() - e.lastRenderZ) * Helper.minecraftClient.getTickDelta());
	}

	private String getHealthText(LivingEntity e) {
		return getHealthColor(e) + String.valueOf((int) (e.getHealth() + e.getAbsorptionAmount()));

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
