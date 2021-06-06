package org.infinity.mixin;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {

	@Inject(method = "render", at = @At("TAIL"), cancellable = true)
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		int modKey = InfMain.getModuleManager().getModuleByClass(GuiMod.class).getKey();
		String key = modKey == 96
				? Formatting.BLUE + "GRAVE " + Formatting.GRAY + "\"" + Formatting.BLUE + " ` " + Formatting.GRAY
						+ "\" "
				: Formatting.BLUE + String.valueOf(InputUtil.fromKeyCode(modKey, modKey)).replace("key.keyboard.", "")
						.toUpperCase();
		FontUtils.drawStringWithShadow(matrices, "Open gui on the " + key + Formatting.WHITE + " key", 2, 3,
				0xFFFFFFFF);

	}

}
