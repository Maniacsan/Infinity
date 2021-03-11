package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.mojang.authlib.GameProfile;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.features.module.movement.SafeWalk;
import me.infinity.features.module.player.NoSlow;
import me.infinity.features.module.player.Scaffold;
import me.infinity.utils.UpdateUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
	private boolean tickUseItem(ClientPlayerEntity player) {
		if (InfMain.getModuleManager().getModuleByClass(NoSlow.class).isEnabled()
				&& ((NoSlow) InfMain.getModuleManager().getModuleByClass(NoSlow.class)).mode.getCurrentMode()
						.equalsIgnoreCase("Vanilla")) {
			return false;
		}

		return player.isUsingItem();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		if (UpdateUtil.canUpdate()) {
			InfMain.getHookManager().onPlayerTick();
		}
	}

	@Inject(at = @At("HEAD"), method = "sendMovementPackets")
	private void sendMovementPackets(CallbackInfo info) {
		MotionEvent motionEvent = new MotionEvent(EventType.PRE);
		EventManager.call(motionEvent);

		if (motionEvent.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(at = @At("RETURN"), method = "sendMovementPackets")
	private void sendMovementPacketsPost(CallbackInfo info) {
		MotionEvent motionEvent = new MotionEvent(EventType.POST);
		EventManager.call(motionEvent);

		if (motionEvent.isCancelled()) {
			info.cancel();
		}
	}

	@Override
	protected boolean clipAtLedge() {
		return super.clipAtLedge() || InfMain.getModuleManager().getModuleByClass(SafeWalk.class).isEnabled()
				|| (InfMain.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()
						&& ((Scaffold) InfMain.getModuleManager().getModuleByClass(Scaffold.class)).safeWalk
								.isToggle());
	}

}
