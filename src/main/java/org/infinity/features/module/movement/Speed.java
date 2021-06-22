package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.MoveUtil;
import org.infinity.utils.Timer;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

@ModuleInfo(category = Category.MOVEMENT, desc = "Make you faster", key = -2, name = "Speed", visible = true)
public class Speed extends Module {

	private Setting mode = new Setting(this, "Mode", "Strafe",
			new ArrayList<>(Arrays.asList("Strafe", "Sentiel Ground", "onGround")));

	private Setting strafeSpeed = new Setting(this, "Strafe Speed", 0.24, 0.2, 0.5)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Strafe"));

	private Timer timer = new Timer();

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
				if (!Helper.getPlayer().isOnGround())
					return;

				Helper.getPlayer().velocityDirty = false;

				Helper.getPlayer().getAbilities().allowFlying = false;

				MoveUtil.setHVelocity(0, 0);
				MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.4);

			}
		}
	}
}
