package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.TimeHelper;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Make you faster", key = -2, name = "Speed", visible = true)
public class Speed extends Module {

	private Settings mode = new Settings(this, "Mode", "Strafe",
			new ArrayList<>(Arrays.asList("Strafe")), () -> true);

	private Settings strafeSpeed = new Settings(this, "Strafe Speed", 0.23, 0.05, 1.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Strafe"));

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Strafe")
				|| mode.getCurrentMode().equalsIgnoreCase("Sentiel Strafe")) {
			if (MoveUtil.isMoving() && Helper.getPlayer().isOnGround())
				Helper.getPlayer().jump();
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
				MoveUtil.strafe(strafeSpeed.getCurrentValueDouble(), MoveUtil.calcMoveYaw());
			} else if (mode.getCurrentMode().equalsIgnoreCase("Sentiel Ground")) {

				if (!Helper.getPlayer().isOnGround())
					return;

				if (MoveUtil.isMoving()) {
					if (timer.hasReached(175)) {
						MoveUtil.strafe(2.0D);
						timer.reset();
					} else {
						MoveUtil.strafe(0);
					}
				}

			} else if (mode.getCurrentMode().equalsIgnoreCase("Sentiel Strafe")) {
				if (MoveUtil.isMoving()) {
					if (Helper.getPlayer().isOnGround()) {
						MoveUtil.strafe(1.7);
					} else {
						MoveUtil.strafe(1.243D);	
					}
				}
			}
		}
	}

}
