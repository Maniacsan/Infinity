package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.infinity.clickmenu.util.FontUtils;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {

	@Inject(method = "render", at = @At("TAIL"), cancellable = true)
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		FontUtils.drawStringWithShadow(matrices, "Open gui on the " + Formatting.BLUE + "GRAVE " + Formatting.GRAY
				+ "\"" + Formatting.BLUE + " ` " + Formatting.GRAY + "\" " + Formatting.WHITE + "key", 2, 3, 0xFFFFFFFF);

	}

}
