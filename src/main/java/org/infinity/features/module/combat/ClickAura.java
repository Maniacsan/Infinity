package org.infinity.features.module.combat;

import org.infinity.event.ClickEvent;
import org.infinity.event.PacketEvent;
import org.infinity.event.RotationEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.PacketUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtils;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attacking target on click", key = -2, name = "ClickAura", visible = true)
public class ClickAura extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	private Settings throughWalls = new Settings(this, "Through Walls", false, () -> true);

	private Settings rotation = new Settings(this, "Rotation", true, () -> true);
	private Settings look = new Settings(this, "Look", true, () -> rotation.isToggle());

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);

	private Settings range = new Settings(this, "Range", 3.5D, 0D, 6D, () -> true);

	// target
	public static Entity target;

	@Override
	public void onDisable() {
		target = null;
	}

	@EventTarget
	public void onClick(ClickEvent event) {

		target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), fov.getCurrentValueDouble(),
				players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle(),
				throughWalls.isToggle());

		if (target == null)
			return;

		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		float[] rotate = RotationUtils.lookAtEntity(target);

		if (rotation.isToggle() && look.isToggle()) {
			Helper.getPlayer().yaw = rotate[0];
			Helper.getPlayer().pitch = rotate[1];
		}
		Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
		EntityUtil.swing(true);

	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		float[] rotate = RotationUtils.lookAtEntity(target);
		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		if (rotation.isToggle()) {
			PacketUtil.setRotation(event, rotate[0], rotate[1]);
		}
	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		float[] rotate = RotationUtils.lookAtEntity(target);
		if (target == null)
			return;

		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		if (rotation.isToggle()) {
			event.setYaw(rotate[0]);
			event.setPitch(rotate[1]);
		}
		event.cancel();
	}

}
