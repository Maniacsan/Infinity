package me.infinity.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MoveEvent;
import me.infinity.event.RotationEvent;
import me.infinity.features.module.combat.HitBoxes;
import me.infinity.features.module.combat.Velocity;
import me.infinity.features.module.movement.AntiWaterPush;
import me.infinity.utils.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public abstract void setSwimming(boolean swimming);

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

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", opcode = Opcodes.INVOKEVIRTUAL, ordinal = 0), method = {
			"updateMovementInFluid(Lnet/minecraft/tag/Tag;D)Z" })
	private void setVelocityFromFluid(Entity entity, Vec3d velocity) {
		if (InfMain.getModuleManager().getModuleByClass(AntiWaterPush.class).isEnabled())
			return;

		entity.setVelocity(velocity);
	}

	@Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
	private void onPushAwayFrom(Entity entity, CallbackInfo ci) {
		Velocity.pushAway((Entity) (Object) this, entity, ci);
	}

	@Overwrite
	public final Vec3d getRotationVector(float pitch, float yaw) {
		RotationEvent rotationEvent = new RotationEvent(yaw, pitch);
		EventManager.call(rotationEvent);

		if (rotationEvent.isCancelled()) {
			pitch = rotationEvent.getPitch();
			yaw = rotationEvent.getYaw();
		}

		float f = pitch * 0.017453292F;
		float g = -yaw * 0.017453292F;
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d((double) (i * j), (double) (-k), (double) (h * j));
	}

}
