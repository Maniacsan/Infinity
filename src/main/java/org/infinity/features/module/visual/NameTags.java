package org.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.infinity.event.EntityTagEvent;
import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.WorldRender;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Category.VISUAL, desc = "Makes name tags convenient", key = -2, name = "NameTags", visible = true)
public class NameTags extends Module {

	private Setting armor = new Setting(this, "Armor", true);

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", true).setVisible(() -> players.isToggle());

	private Setting invisibles = new Setting(this, "Invisibles", false);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);

	private Setting items = new Setting(this, "Items", true);

	private Setting scale = new Setting(this, "Scale", 2D, 0.2D, 5D);

	@EventTarget
	public void onTagRender(EntityTagEvent event) {
		if (EntityUtil.isTarget(event.getEntity(), players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle()))
			event.cancel();
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity entity : Helper.getWorld().getEntities()) {
			List<String> lines = new ArrayList<>();
			double scale = 0;

			Vec3d rPos = EntityUtil.getRenderPos(entity);

			scale = Math.max(
					this.scale.getCurrentValueDouble() * (Helper.minecraftClient.cameraEntity.distanceTo(entity) / 20),
					1);

			if (items.isToggle() && entity instanceof ItemEntity) {
				ItemEntity e = (ItemEntity) entity;

				lines.add(e.getDisplayName().getString());

			} else if (entity instanceof LivingEntity) {
				if (EntityUtil.isTarget(entity, players.isToggle(), friends.isToggle(), invisibles.isToggle(),
						mobs.isToggle(), animals.isToggle())) {

					LivingEntity e = (LivingEntity) entity;

					String health = getHealthText(e);

					String name = entity.getDisplayName().getString();

					name = name + " " + health;

					lines.add(name);

					/* Drawing Items */
					double c = 0;
					double lscale = scale * 0.4;
					double up = ((0.3 + lines.size() * 0.25) * scale) + lscale / 2;

					if (armor.isToggle()) {
						drawItem(rPos.x, rPos.y + up, rPos.z, 2.5, 0, lscale,
								e.getEquippedStack(EquipmentSlot.MAINHAND));
						drawItem(rPos.x, rPos.y + up, rPos.z, -2.5, 0, lscale,
								e.getEquippedStack(EquipmentSlot.OFFHAND));

						for (ItemStack i : e.getArmorItems()) {
							drawItem(rPos.x, rPos.y + up, rPos.z, c - 1.5, 0, lscale, i);
							c++;
						}
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

	private String getHealthText(LivingEntity e) {
		return getHealthColor(e) + String.valueOf((int) (e.getHealth() + e.getAbsorptionAmount()));

	}

	private Formatting getHealthColor(LivingEntity e) {
		if (e.getHealth() + e.getAbsorptionAmount() >= e.getMaxHealth())
			return Formatting.GREEN;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.7)
			return Formatting.YELLOW;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.4)
			return Formatting.GOLD;
		if (e.getHealth() + e.getAbsorptionAmount() <= e.getMaxHealth() * 0.2)
			return Formatting.RED;
		return Formatting.WHITE;
	}
}
