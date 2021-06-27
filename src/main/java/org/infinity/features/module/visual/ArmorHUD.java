package org.infinity.features.module.visual;

import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@ModuleInfo(category = Category.VISUAL, desc = "Shows broken armor", key = -2, name = "ArmorHUD", visible = true)
public class ArmorHUD extends Module {

	public Setting scale = new Setting(this, "Scale", 1.0D, 0.4, 2.0D);
	private Setting damage = new Setting(this, "Damage Count", true);

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		if (Helper.minecraftClient.options.debugEnabled)
			return;

		float scale = (float) this.scale.getCurrentValueDouble();

		matrices.push();
		matrices.scale(scale, scale, 1);

		int y = 1;
		for (int i = Helper.getPlayer().getInventory().armor.size() - 1; i >= 0; i--) {
			ItemStack armor = Helper.getPlayer().getInventory().armor.get(i);

			if (armor.isDamageable())
				Render2D.horizontalGradient(matrices, 1, y, damage.isToggle() && armor.isDamageable() ? 44 : 17, y + 18,
						0x90000000, 0x00000000);
			RenderUtil.drawItem(Helper.getPlayer().getInventory().armor.get(i), 1, y, true);

			if (damage.isToggle() && armor.isDamageable()) {
				int damage = armor.getMaxDamage() - armor.getDamage();
				IFont.legacy17.drawString(matrices, String.valueOf(damage), 20, y + 4, 0xFFFFFFFF);
			}

			y += 17;
		}

		matrices.pop();
	}

}
