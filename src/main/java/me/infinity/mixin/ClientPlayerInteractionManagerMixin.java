package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.AttackEvent;
import me.infinity.features.module.player.FastBreak;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow
	private int blockBreakingCooldown;

	private int getCooldown() {
		return (InfMain.getModuleManager().getModuleByClass(FastBreak.class).isEnabled()
				? (int) ((FastBreak) InfMain.getModuleManager().getModuleByClass(FastBreak.class)).speed
						.getCurrentValueDouble()
				: 5);
	}

	@Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 3), require = 0)
	public void updateBlockBreakingProgress(ClientPlayerInteractionManager clientPlayerInteractionManager, int i) {
		this.blockBreakingCooldown = getCooldown();
	}

	@Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 4), require = 0)
	public void updateBlockBreakingProgress2(ClientPlayerInteractionManager clientPlayerInteractionManager, int i) {
		this.blockBreakingCooldown = getCooldown();
	}

	@Redirect(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I"), require = 0)
	public void attackBlock(ClientPlayerInteractionManager clientPlayerInteractionManager, int i) {
		this.blockBreakingCooldown = getCooldown();
	}

	@Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
	public void preAttack(PlayerEntity entity, Entity target, CallbackInfo info) {
		AttackEvent attackEvent = new AttackEvent(EventType.PRE);
		EventManager.call(attackEvent);
		if (attackEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "attackEntity", at = @At("RETURN"), cancellable = true)
	public void postAttack(PlayerEntity entity, Entity target, CallbackInfo info) {
		AttackEvent attackEvent = new AttackEvent(EventType.POST);
		EventManager.call(attackEvent);
		if (attackEvent.isCancelled())
			info.cancel();
	}

}
