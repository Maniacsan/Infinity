package me.infinity.features.module.visual;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import me.infinity.utils.render.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Shows broken armor", key = -2, name = "ArmorHUD", visible = true)
public class ArmorHUD extends Module {

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		int x = 1;
        for(int i = 3; i >= 0; i--) {
			RenderUtil.drawItem(Helper.getPlayer().inventory.armor.get(i), 1, x, true);
			x += 17;
		}
	}

}
