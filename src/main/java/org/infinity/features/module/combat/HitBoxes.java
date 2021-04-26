package org.infinity.features.module.combat;

import org.infinity.event.RotationEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.StringUtil;
import org.infinity.utils.entity.EntityUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Edit entity hitbox", key = -2, name = "HitBoxes", visible = true)
public class HitBoxes extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	public Settings size = new Settings(this, "Size", 0.35D, 0.0D, 5.0D, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(StringUtil.DF(size.getCurrentValueDouble(), 2));
	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		// spoofing to entity rotation

		if (Helper.minecraftClient.targetedEntity == null && !EntityUtil.isTarget(Helper.minecraftClient.targetedEntity,
				players.isToggle(), false, invisibles.isToggle(), mobs.isToggle(), animals.isToggle()))
			return;

		event.setYaw(Helper.getPlayer().yaw);
		event.setPitch(Helper.getPlayer().pitch);
		event.cancel();

	}

	public boolean isTarget(Entity entity) {
		if (EntityUtil.isTarget(entity, players.isToggle(), false, invisibles.isToggle(), mobs.isToggle(),
				animals.isToggle())) {
			return true;
		}
		return false;
	}

	public float getSize(Entity entity) {
		if (!isEnabled())
			return 0;
		if (isTarget(entity))
			return (float) size.getCurrentValueDouble();
		return 0;
	}

}