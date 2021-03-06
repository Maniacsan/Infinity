package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MoveEvent;
import me.infinity.utils.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class EntityMixin {
	
	@Shadow
	private Vec3d pos;

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

}
