package me.infinity.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

public class EntityUtil {

	// Set target -> Entity entity -> to update method -> entity =
	// EntityUtil.setTarget(values);
	public static Entity setTarget(double range, boolean players, boolean invisibles, boolean mobs, boolean animals) {
		Entity entity = null;
		float maxDist = (float) range;
		for (Entity e : getTargets(players, invisibles, mobs, animals)) {
			if (e != null) {
				float currentDist = Helper.getPlayer().distanceTo(e);
				if (currentDist <= maxDist) {
					maxDist = currentDist;
					entity = e;
				}
			}
		}
		return entity;
	}

	public static List<Entity> getTargets(boolean players, boolean invisibles, boolean mobs, boolean animals) {
		return StreamSupport.stream(Helper.minecraftClient.world.getEntities().spliterator(), false)
				.filter(entity -> isTarget(entity, players, invisibles, mobs, animals)).collect(Collectors.toList());
	}

	public static boolean isTarget(Entity entity, boolean players, boolean invisibles, boolean mobs, boolean animals) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer())
			return false;

		if (invisibles && entity.isInvisible())
			return true;
		if (players && entity instanceof PlayerEntity)
			return true;
		if (mobs && isMonster(entity))
			return true;
		if (animals && isAnimal(entity))
			return true;

		return false;
	}

	public static boolean isAnimal(Entity e) {
		return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof WaterCreatureEntity
				|| e instanceof GolemEntity;
	}

	public static boolean isMonster(Entity e) {
		return e instanceof Monster;
	}

	public static void swing(boolean animation) {
		if (animation) {
			Helper.getPlayer().swingHand(Hand.MAIN_HAND);
		} else {
			Helper.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
		}
	}
}
