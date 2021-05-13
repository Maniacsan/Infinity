package org.infinity.features.module.visual;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@ModuleInfo(category = Category.VISUAL, desc = "Shows broken armor", key = -2, name = "ArmorHUD", visible = true)
public class ArmorHUD extends Module {

	private Setting damage = new Setting(this, "Damage Count", true);

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		if (Helper.minecraftClient.options.debugEnabled)
			return;

		int x = 1;
		for (int i = Helper.getPlayer().inventory.armor.size() - 1; i >= 0; i--) {
			ItemStack armor = Helper.getPlayer().inventory.armor.get(i);
			
			if (armor.isDamageable())
			Render2D.drawRectWH(matrices, 1, x + 1, damage.isToggle() && armor.isDamageable() ? 40 : 16, 16,
					0x90000000);
			RenderUtil.drawItem(Helper.getPlayer().inventory.armor.get(i), 1, x, true);

			if (damage.isToggle() && armor.isDamageable()) {
				int damage = armor.getMaxDamage() - armor.getDamage();
				FontUtils.drawStringWithShadow(matrices, String.valueOf(damage), 21, x + 5, 0xFFFFFFFF);
			}

			x += 17;
		}
	}

}
