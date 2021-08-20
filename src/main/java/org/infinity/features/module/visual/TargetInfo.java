package org.infinity.features.module.visual;

import java.awt.Color;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.utils.MathAssist;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.Render2D;
import org.infinity.utils.render.RenderUtil;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Shows information about the enemy ", key = -2, name = "TargetInfo", visible = true)
public class TargetInfo extends Module {

	private Setting friends = new Setting(this, "Friends", false);
	private Setting invisibles = new Setting(this, "Invisibles", true);

	private Setting range = new Setting(this, "Range", 6D, 1D, 12.0D);
	
	private float fade;

	private Entity target;

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int w, int h) {
		target = EntityUtil.getTarget(range.getCurrentValueDouble(), 360, true, friends.isToggle(),
				invisibles.isToggle(), false, false, true);

		if (target == null)
			fade = Math.max(0, fade - 0.4F);
		else
			fade = Math.min(1F, fade + 0.4F);

		int wc = w / 2 - (162 / 2);
		int hc = h / 2 + 60;
		
		if (fade <= 0)
			return;

		if (target instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) target;
			
			RenderSystem.setShaderColor(1f, 1f, 1f, fade);
			double inc = ((player.getHealth() + player.getAbsorptionAmount()) / player.getMaxHealth()) * 75;
			double end = Math.min(inc, 75);

			Render2D.horizontalGradient(matrices, wc, hc, (int) (wc - 1 + 170), hc + 51, new Color(0, 0, 0, 200).getRGB(),
					new Color(0, 0, 0, 0).getRGB());
			InventoryScreen.drawEntity(wc + 18, hc + 47, 23, 60, -target.getPitch(), (LivingEntity) target);
			int off = 0;

			for (int i = 3; i >= 0; i--) {
				RenderUtil.drawItem(player.getInventory().getArmorStack(i), wc + 37 + off, hc + 1, true);
				if (!player.getInventory().getArmorStack(i).isEmpty())
				off += 16;
			}

			RenderUtil.drawItem(player.getEquippedStack(EquipmentSlot.MAINHAND),
					player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() ? wc + 37 : wc + 51, hc + 19, true);
			RenderUtil.drawItem(player.getEquippedStack(EquipmentSlot.OFFHAND), wc + 37, hc + 19, true);

			String name = player.getCustomName() != null ? player.getCustomName().getString()
					: player.getDisplayName().getString();

			IFont.legacy14.drawString(matrices, Formatting.RESET + name, wc + 2, hc - 9, -1);
			
			IFont.legacy14.drawString(matrices,
					String.valueOf(MathAssist.round(player.getHealth() + player.getAbsorptionAmount(), 1)),
					wc + 49 + 75, hc + 39, 0xFFFFFFFF);

			Render2D.drawHRoundedRect(matrices, wc + 43, hc + 40, 75, 6, 0xFF0A0A0A);
			Render2D.drawHRoundedRect(matrices, wc + 43, hc + 40, end, 6, 0xFF59DFD8);

			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
	}
}
