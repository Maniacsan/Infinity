package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtils;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.entity.Entity;
import net.minecraft.item.BowItem;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Aimbot for bow to target", key = -2, name = "BowAim", visible = true)
public class BowAim extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet", new ArrayList<>(Arrays.asList("Packet", "Client")),
			() -> true);
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	private Settings throughWalls = new Settings(this, "Through Walls", false, () -> true);

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);
	private Settings range = new Settings(this, "Range", 40, 1, 80, () -> true);
	private Settings speed = new Settings(this, "Speed", 90F, 1F, 180F, () -> true);

	public static Entity target;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (Helper.getPlayer().getMainHandStack().getItem() instanceof BowItem && Helper.getPlayer().isUsingItem()
					&& Helper.getPlayer().getItemUseTime() >= 3) {
				target = EntityUtil.setTarget(range.getCurrentValueInt(), fov.getCurrentValueDouble(),
						players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(),
						animals.isToggle(), throughWalls.isToggle());
				if (target == null)
					return;
				float[] look = RotationUtils.bowAimRotation(target);
				
				look[0] = RotationUtils.limitAngleChange(event.getYaw(), look[0], speed.getCurrentValueFloat());
				look[1] = RotationUtils.limitAngleChange(event.getPitch(), look[1], speed.getCurrentValueFloat());

				event.setRotation(look[0], look[1], mode.getCurrentMode().equalsIgnoreCase("Client"));

			}
		}
	}
}