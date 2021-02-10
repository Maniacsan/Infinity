package me.infinity.features.combat;

import org.lwjgl.glfw.GLFW;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attack entities on range", key = GLFW.GLFW_KEY_R, name = "KillAura", visible = true)
public class KillAura extends Module {

	private Settings players = new Settings(this, "Players", true);
	private Settings invisibles = new Settings(this, "Invisibles", true);
	private Settings mobs = new Settings(this, "Mobs", true);
	private Settings animals = new Settings(this, "Animals", true);
	private Settings range = new Settings(this, "Range", 4.0D, 0.1D, 6.0D);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D);

	//
	TimeHelper time = new TimeHelper();

	public static LivingEntity target;

	@Override
	public void onDisable() {
		target = null;
		super.onDisable();
	}

	@Override
	public void onPlayerTick() {
		target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), players.isToggle(), invisibles.isToggle(),
				true, true);
		if (target == null)
			return;

		RotationUtils.lookAtEntity(target, Helper.getPlayer().yaw, Helper.getPlayer().pitch, 360f, 360f);

		if (Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1) {
			Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
			Helper.getPlayer().swingHand(Hand.MAIN_HAND);
		}
	}
	
	public LivingEntity setTarget(double range, boolean players, boolean invisibles, boolean mobs,
			boolean animals) {
		LivingEntity entity = null;
		float maxDist = 6;
		for (Entity e : Helper.minecraftClient.world.getEntities()) {
			if (e != null) {
				if (checkEntity(entity, players, invisibles, mobs, animals)) {
					float currentDist = Helper.getPlayer().distanceTo(e);
					if (currentDist <= maxDist) {
						maxDist = currentDist;
						entity = (LivingEntity) e;
					}
				}
			}
		}
		return entity;
	}

	public boolean checkEntity(LivingEntity entity, boolean players, boolean invisibles, boolean mobs,
			boolean animals) {
		boolean ret = false;

		if (entity == Helper.getPlayer()) {
			ret = false;
		}

		if (this.players.isToggle() && entity instanceof PlayerEntity) {
			ret = true;
		}

		if (this.mobs.isToggle() && entity instanceof MobEntity) {
			ret = true;
		}

		if (this.animals.isToggle() && entity instanceof AnimalEntity && !(entity instanceof MobEntity)) {
			ret = true;
		}

		return ret;
	}

}
