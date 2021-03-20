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
		// name
		Helper.minecraftClient.getTextureManager().bindTexture(NAME);
		DrawableHelper.drawTexture(matrices, this.width / 2 - 36, this.height / 2 - 121, 0, 0, 90, 23, 90, 23);	
		
		// logo
		Helper.minecraftClient.getTextureManager().bindTexture(LOGO);
		DrawableHelper.drawTexture(matrices, this.width / 2 - 75, this.height / 2 - 130, 0, 0, 38, 40, 38, 40);
	}

}
