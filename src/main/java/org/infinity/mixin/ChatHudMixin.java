package org.infinity.mixin;

import org.infinity.chat.InfChatHud;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin extends DrawableHelper {

	@Shadow
	protected abstract boolean isChatHidden();

	@Shadow
	protected abstract void processMessageQueue();

	@Unique
	public InfChatHud infChat = InfMain.getChatHud();

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
		infChat.render(matrices, tickDelta);
		ci.cancel();
	}

	@Inject(method = "clear", at = @At("HEAD"), cancellable = true)
	private void onClear(boolean clearHistory, CallbackInfo ci) {
		infChat.clear(clearHistory);
		ci.cancel();
	}

	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", cancellable = true)
	private void onAddMessage(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
		infChat.addMessage(message, messageId, timestamp, bl);
		ci.cancel();
	}

	@Inject(method = "reset", at = @At("HEAD"), cancellable = true)
	private void onReset(CallbackInfo ci) {
		infChat.reset();
		ci.cancel();
	}

	@Inject(method = "scroll", at = @At("HEAD"), cancellable = true)
	private void onScroll(double amount, CallbackInfo ci) {
		infChat.scroll(amount);
		ci.cancel();
	}

	@Inject(method = "getText", at = @At("HEAD"), cancellable = true)
	private void getText(double x, double y, CallbackInfoReturnable<Style> ci) {

		ci.setReturnValue(infChat.getText(x, y));
		ci.cancel();
	}

	@Inject(method = "removeMessage", at = @At("HEAD"), cancellable = true)
	private void onRemoveMessage(int messageId, CallbackInfo ci) {
		infChat.removeMessage(messageId);
		ci.cancel();
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	public void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(infChat.mouseClicked(mouseX, mouseY));
	}

}
