package me.infinity.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityUtil {

	// Set target
	public static LivingEntity setTarget(double range, boolean players, boolean invisibles, boolean mobs,
			boolean animals) {
		LivingEntity entity = null;
		float maxDist = (float) range;
		for (Entity e : Helper.minecraftClient.world.getEntities()) {
			if (e != null) {
					float currentDist = Helper.getPlayer().distanceTo(e);
					if (currentDist <= maxDist) {
						maxDist = currentDist;
						entity = (LivingEntity) e;
					}
			}
		}
		return entity;
	}

	public static boolean checkEntity(LivingEntity entity, boolean players, boolean invisibles, boolean mobs,
			boolean animals) {
		if (entity == Helper.getPlayer()) {
			return false;
		}

		if (players && entity instanceof PlayerEntity) {
			return true;
		}

		if (mobs && isMonster(entity)) {
			return true;
		}

		if (animals && isAnimal(entity)) {
			return true;
		}

		return false;
	}
	
	public static boolean isAnimal(Entity e) {
		return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof WaterCreatureEntity || e instanceof GolemEntity;
	}
	
	public static boolean isMonster(Entity e) {
		return e instanceof Monster;
	}
}
