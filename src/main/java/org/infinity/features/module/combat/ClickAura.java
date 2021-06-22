package org.infinity.features.module.combat;

import org.infinity.event.ClickEvent;
import org.infinity.event.PacketEvent;
import org.infinity.event.RotationEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.PacketUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Category.COMBAT, desc = "Attacking target on click", key = -2, name = "ClickAura", visible = true)
public class ClickAura extends Module {

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);

	private Setting throughWalls = new Setting(this, "Through Walls", false);

	private Setting rotation = new Setting(this, "Rotation", true);
	private Setting look = new Setting(this, "Look View", true).setVisible(() -> rotation.isToggle());

	private Setting fov = new Setting(this, "FOV", 120D, 0D, 360D);

	private Setting range = new Setting(this, "Range", 3.5D, 0D, 6D);

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

		float[] rotate = RotationUtil.lookAtEntity(target);

		if (rotation.isToggle() && look.isToggle()) {
			Helper.getPlayer().setYaw(rotate[0]);
			Helper.getPlayer().setPitch(rotate[1]);
		}
		Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
		EntityUtil.swing(true);

	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		float[] rotate = RotationUtil.lookAtEntity(target);
		if (!Helper.minecraftClient.options.keyAttack.isPressed())
			return;

		if (rotation.isToggle()) {
			PacketUtil.setRotation(event, rotate[0], rotate[1]);
		}
	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		float[] rotate = RotationUtil.lookAtEntity(target);
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
