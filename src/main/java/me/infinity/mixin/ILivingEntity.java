package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface ILivingEntity {

	@Accessor("lastAttackedTicks")
	public int getLastAttackedTick();

}
