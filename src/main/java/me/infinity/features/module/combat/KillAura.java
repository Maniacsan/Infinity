package me.infinity.features.module.combat;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attack entities on range", key = GLFW.GLFW_KEY_R, name = "KillAura", visible = true)
public class KillAura extends Module {

	private Settings players = new Settings(this, "Players", true);
	private Settings invisibles = new Settings(this, "Invisibles", true);
	private Settings mobs = new Settings(this, "Mobs", true);
	private Settings animals = new Settings(this, "Animals", true);
	private Settings noSwing = new Settings(this, "No Swing", false);
	private Settings coolDown = new Settings(this, "CoolDown", true);
	private Settings range = new Settings(this, "Range", 4.0D, 0.1D, 6.0D);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D);

	public static Entity target;
	
	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {
		target = null;
		super.onDisable();
	}

	@EventTarget
	public void onPlayerTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), players.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle());
			if (target == null)
				return;

			if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
					: timer.hasReached(aps.getCurrentValueDouble())) {
				Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
				EntityUtil.swing(!noSwing.isToggle());
				timer.reset();
			}
		} else if (event.getType().equals(EventType.POST)) {
			float[] look = RotationUtils.lookAtEntity(target, getKey(), getKey());
			Helper.getPlayer().yaw = look[0];
			Helper.getPlayer().pitch = look[1];
		}
	}

}
