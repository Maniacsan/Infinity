package org.infinity.features.module.visual;

import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(category = Category.VISUAL, desc = "Shows information about the enemy ", key = -2, name = "TargetInfo", visible = true)
public class TargetInfo extends Module {

	public Setting scale = new Setting(this, "Scale", 1.0D, 0.4, 2.0D);

	private Setting friends = new Setting(this, "Friends", false);
	private Setting invisibles = new Setting(this, "Invisibles", true);

	private Setting range = new Setting(this, "Range", 6D, 1D, 12.0D);

	private Entity target;

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int w, int h) {
		target = EntityUtil.setTarget(range.getCurrentValueDouble(), 360, true, friends.isToggle(),
				invisibles.isToggle(), false, false, true);

		if (target == null || target == Helper.getPlayer())
			return;

		int wc = w / 2 - (162 / 2);
		int hc = h / 2 + 60;

		if (target instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) target;
			double inc = ((player.getHealth() + player.getAbsorptionAmount()) / player.getMaxHealth()) * 161;
			double end = Math.min(inc, 161);

			float scale = (float) this.scale.getCurrentValueDouble();

			matrices.push();

			matrices.translate(wc + (162 / 2), hc + (60 / 2), 0);
			matrices.scale(scale, scale, scale);
			matrices.translate(-wc - (162 / 2), -hc - (60 / 2), 0);

			Render2D.drawBorderedRect(matrices, wc, hc, 160, 58, 2, 0xFF060515, 0x9908090F);
			InventoryScreen.drawEntity(wc + 18, hc + 54, 23, 60, -target.getPitch(), (LivingEntity) target);
			int off = 0;

			for (int i = 3; i >= 0; i--) {
				RenderUtil.drawItem(player.getInventory().getArmorStack(i), wc + 34, hc - 1 + off, true);
				off += 14;
			}

			RenderUtil.drawItem(player.getEquippedStack(EquipmentSlot.MAINHAND),
					player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() ? wc + 52 : wc + 70, hc + 38, true);
			RenderUtil.drawItem(player.getEquippedStack(EquipmentSlot.OFFHAND), wc + 52, hc + 38, true);

			IFont.legacy15.drawString(matrices, player.getName().getString(), wc + 52, hc + 2, 0xFFFFFFFF);
			IFont.legacy15.drawString(matrices,
					"Health: " + MathAssist.round(player.getHealth() + player.getAbsorptionAmount(), 1), wc + 52,
					hc + 13, 0xFFFFFFFF);
			Render2D.horizontalGradient(matrices, wc, hc + 58, (int) (wc - 1 + end), hc + 59, 0xFFF41919, 0xFF1DF420);

			matrices.pop();
		}
	}
}
