package me.infinity.features.module.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attacking target on click", key = -2, name = "ClickAura", visible = true)
public class ClickAura extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	private Settings rotation = new Settings(this, "Rotation", true, () -> true);
	private Settings look = new Settings(this, "Look", true, () -> rotation.isToggle());

	private Settings range = new Settings(this, "Range", 3.5D, 0D, 6D, () -> true);
	
	// spoof
	private float prevYaw;
	private float prevPitch;
	
	// target
	public static Entity target;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			prevYaw = Helper.getPlayer().yaw;
			prevPitch = Helper.getPlayer().pitch;
			
			target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), players.isToggle(),
					invisibles.isToggle(), mobs.isToggle(), animals.isToggle());

			if (target == null)
				return;

			if (!Helper.minecraftClient.options.keyAttack.isPressed())
				return;

			float[] rotate = RotationUtils.lookAtEntity(target, 180, 180);
			
			EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), rotate[0], rotate[1]);

			if (rotation.isToggle()) {
				Helper.getPlayer().yaw = rotate[0];
				Helper.getPlayer().pitch = rotate[1];
			}

				Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
				EntityUtil.swing(true);

		} else if (event.getType().equals(EventType.POST)) {
			if (!Helper.minecraftClient.options.keyAttack.isPressed())
				return;

			if (rotation.isToggle() && !look.isToggle()) {
				Helper.getPlayer().yaw = prevYaw;
				Helper.getPlayer().pitch = prevPitch;
			}
		}
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

}
