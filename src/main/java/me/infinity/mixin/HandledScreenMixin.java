package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {

	private static final Identifier LOGO = new Identifier("infinity", "logo.png");
	private static final Identifier NAME = new Identifier("infinity", "infinity.png");

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

		int x = Helper.getPlayer().isCreative() && Helper.getPlayer().getActiveStatusEffects() != null
				? this.width / 2 + 5
				: this.width / 2 - 36;
		int y = Helper.getPlayer().isCreative() ? this.height / 2 - 135 : this.height / 2 - 121;
		// name
		Helper.minecraftClient.getTextureManager().bindTexture(NAME);
		DrawableHelper.drawTexture(matrices, x, y, 0, 0, 90, 23, 90, 23);
		
		int x1 = Helper.getPlayer().isCreative() && Helper.getPlayer().getActiveStatusEffects() != null
				? this.width / 2 + 29
				: this.width / 2 - 75;
		int y1 = Helper.getPlayer().isCreative() ? this.height / 2 - 144 : this.height / 2 - 130;

		// logo
		Helper.minecraftClient.getTextureManager().bindTexture(LOGO);
		DrawableHelper.drawTexture(matrices, x1, y1, 0, 0, 38, 40, 38, 40);
	}

}
