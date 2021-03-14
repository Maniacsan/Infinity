package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.mojang.authlib.GameProfile;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.features.module.movement.SafeWalk;
import me.infinity.features.module.player.NoSlow;
import me.infinity.features.module.player.Scaffold;
import me.infinity.utils.UpdateUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Shadow
	protected void autoJump(float float_1, float float_2) {
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

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	public void move(MovementType movementType_1, Vec3d vec3d_1, CallbackInfo info) {
		PlayerMoveEvent moveEvent = new PlayerMoveEvent(movementType_1, vec3d_1);
		EventManager.call(moveEvent);
		if (moveEvent.isCancelled()) {
			info.cancel();
		} else if (!movementType_1.equals(moveEvent.type) || !vec3d_1.equals(moveEvent.vec3d)) {
			double double_1 = this.getX();
			double double_2 = this.getZ();
			super.move(moveEvent.type, moveEvent.vec3d);
			this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
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
