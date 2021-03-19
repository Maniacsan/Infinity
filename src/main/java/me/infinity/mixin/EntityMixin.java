package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MoveEvent;
import me.infinity.event.RotationEvent;
import me.infinity.features.module.combat.HitBoxes;
import me.infinity.utils.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "move", at = @At("HEAD"))
	private void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
		if ((Object) this != Helper.minecraftClient.player)
			return;
		MoveEvent moveEvent = new MoveEvent(EventType.PRE, type, movement);
		EventManager.call(moveEvent);
	}

	@Inject(method = "move", at = @At("RETURN"))
	private void onMovePost(MovementType type, Vec3d movement, CallbackInfo info) {
		if ((Object) this != Helper.minecraftClient.player)
			return;
		MoveEvent moveEvent = new MoveEvent(EventType.POST, type, movement);
		EventManager.call(moveEvent);
	}

	@Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
	public void getTargetingMargin(CallbackInfoReturnable<Float> info) {
		float box = ((HitBoxes) InfMain.getModuleManager().getModuleByClass(HitBoxes.class))
				.getSize((Entity) (Object) this);
		if (box != 0) {
			info.setReturnValue(box);
		}
	}

	@Inject(method = "getRotationVector", at = @At("HEAD"), cancellable = true)
	private void onGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> info) {
		RotationEvent rotationEvent = new RotationEvent(yaw, pitch);
		EventManager.call(rotationEvent);

		if (rotationEvent.isCancelled()) {
			pitch = rotationEvent.getPitch();
			yaw = rotationEvent.getYaw();
		}
	}

}
