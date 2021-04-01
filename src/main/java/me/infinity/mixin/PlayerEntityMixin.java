package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.infinity.InfMain;
import me.infinity.features.module.player.FastBreak;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow
	public PlayerInventory inventory;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = { "getBlockBreakingSpeed" }, at = { @At("RETURN") }, cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
		FastBreak fastBreak = ((FastBreak) InfMain.getModuleManager().getModuleByClass(FastBreak.class));
		if (fastBreak.isEnabled()) {
			cir.setReturnValue((float) (cir.getReturnValue() * fastBreak.speed.getCurrentValueDouble()));
		}
	}
}
