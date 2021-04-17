package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
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
			new ArrayList<>(Arrays.asList("Strafe", "Sentiel Ground", "onGround")), () -> true);

	private Settings strafeSpeed = new Settings(this, "Strafe Speed", 0.24, 0.2, 0.5,
			() -> mode.getCurrentMode().equalsIgnoreCase("Strafe"));

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
			if (MoveUtil.isMoving() && Helper.getPlayer().isOnGround()) {
				if (Helper.getPlayer().isSprinting())
					Helper.getPlayer().setSprinting(false);
				Helper.getPlayer().jump();
			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
				MoveUtil.strafe(MoveUtil.calcMoveYaw(), strafeSpeed.getCurrentValueDouble());

			} else if (mode.getCurrentMode().equalsIgnoreCase("Sentiel Ground")) {

				if (!Helper.getPlayer().isOnGround())
					return;

				if (MoveUtil.isMoving()) {
					if (timer.hasReached(380)) {
						InfMain.TIMER = 0.45f;
						MoveUtil.hClip(3);
						timer.reset();
					} else {
						InfMain.resetTimer();
					}

				} else
					InfMain.resetTimer();

			} else if (mode.getCurrentMode().equalsIgnoreCase("onGround")) {
				if (MoveUtil.isMoving()) {

					if (Helper.getPlayer().forwardSpeed != 0) {
						Helper.getPlayer().setSprinting(true);
					}

					if (!Helper.getPlayer().isOnGround()) {
						return;
					}
					
					MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed());

					if (Helper.getPlayer().age % 3 == 0) {

						MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed() * 1.7);

						MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed() * 1.3);
					}
					
					MoveUtil.setYVelocity(-1e-2);
				}
			}
		}
	}
}
