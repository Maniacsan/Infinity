package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.EntityTagEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.render.RenderUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

	private Settings scale = new Settings(this, "Scale", 0.35D, 0.2D, 0.6D, () -> true);

	@EventTarget
	public void onTagRender(EntityTagEvent event) {
		renderTag(event.getMatrices());
		event.cancel();
	}

	public void renderTag(MatrixStack matrices) {
		for (Entity entity : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle())) {
			float cScale = (float) Math.max(
					Math.min(Helper.getPlayer().distanceTo(entity) / (100 / scale.getCurrentValueDouble()), 2.4 / 50),
					1 / 80d);
			List<String> lines = new ArrayList<>();

			Vec3d rPos = EntityUtil.getRenderPos(entity);
			lines.add(entity.getName().getString() + " " + getHealthColor((LivingEntity) entity)
					+ ((LivingEntity) entity).getHealth());

			double c = 0;
			double lscale = cScale * 0.4;
			double up = ((0.3 + lines.size() * 0.25) * cScale) + lscale / 2;

			if (entity instanceof PlayerEntity) {
				if (this.armor.isToggle()) {
					drawItem(rPos.x, rPos.y + up, rPos.z, -2.5, 0, lscale,
							((PlayerEntity) entity).getEquippedStack(EquipmentSlot.MAINHAND));
					drawItem(rPos.x, rPos.y + up, rPos.z, 2.5, 0, lscale,
							((PlayerEntity) entity).getEquippedStack(EquipmentSlot.OFFHAND));

					for (ItemStack i : entity.getArmorItems()) {
						drawItem(rPos.x, rPos.y + up, rPos.z, c + 1.5, 0, lscale, i);
						c--;
					}
				}
			}

			if (!lines.isEmpty()) {
				float offset = 0.25f + lines.size() * 0.25f;

				for (String s : lines) {
					RenderUtil.drawText(s, rPos.x, rPos.y + (offset * cScale), rPos.z, cScale);
					offset -= 0.25f;
				}
			}
		}
	}

	private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		MatrixStack matrix = RenderUtil.drawGuiItem(x, y, z, offX, offY, scale, item);

		matrix.scale(-0.05F, -0.05F, 1f);

		GL11.glDepthFunc(GL11.GL_ALWAYS);
		if (!item.isEmpty()) {
			int w = Helper.minecraftClient.textRenderer.getWidth("x" + item.getCount()) / 2;
			Helper.minecraftClient.textRenderer.drawWithShadow(matrix, "x" + item.getCount(), 7 - w, 3, 0xffffff);
		}

		matrix.scale(0.85F, 0.85F, 1F);

		int c = 0;
		for (Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
			String text = I18n.translate(m.getKey().getName(2).getString());

			if (text.isEmpty())
				continue;

			String subText = text.substring(0, Math.min(text.length(), 2)) + m.getValue();

			int w1 = Helper.minecraftClient.textRenderer.getWidth(subText) / 2;
			Helper.minecraftClient.textRenderer.drawWithShadow(matrix, subText, -2 - w1, c * 10 - 19,
					m.getKey() == Enchantments.VANISHING_CURSE || m.getKey() == Enchantments.BINDING_CURSE ? 0xff5050
							: 0xffb0e0);
			c--;
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
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
