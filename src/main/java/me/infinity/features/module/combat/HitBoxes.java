package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.MathAssist;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Edit entity hitbox", key = -2, name = "HitBoxes", visible = true)
public class HitBoxes extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	public Settings size = new Settings(this, "Size", 0.5F, 0.0F, 3.0F, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(MathAssist.round(size.getCurrentValueFloat(), 1)));
	}

	public boolean isTarget(Entity entity) {
		if (EntityUtil.isTarget(entity, players.isToggle(), invisibles.isToggle(), mobs.isToggle(),
				animals.isToggle())) {
			return true;
		}
		return false;
	}

	public float getSize(Entity entity) {
		if (!isEnabled())
			return 0;
		if (isTarget(entity))
			return size.getCurrentValueFloat();
		return 0;
	}

}
