package me.infinity.features.module.combat;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.ClickEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.RotationEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.RotationUtils;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attacking target on click", key = -2, name = "ClickAura", visible = true)
public class ClickAura extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

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
				friends.isToggle(), players.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle());

		if (target == null)
			return;

		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		float[] rotate = RotationUtils.lookAtEntity(target, 180, 180);

		if (rotation.isToggle() && look.isToggle()) {
			Helper.getPlayer().yaw = rotate[0];
			Helper.getPlayer().pitch = rotate[1];
		}
		Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
		EntityUtil.swing(true);

	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		float[] rotate = RotationUtils.lookAtEntity(target, 180, 180);
		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		if (rotation.isToggle()) {
			PacketUtil.setRotation(event, rotate[0], rotate[1]);
		}
	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		float[] rotate = RotationUtils.lookAtEntity(target, 180, 180);
		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		if (rotation.isToggle()) {
			event.setYaw(rotate[0]);
			event.setPitch(rotate[1]);
		}
		event.cancel();
	}

}
