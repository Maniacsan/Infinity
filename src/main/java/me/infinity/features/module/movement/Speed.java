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
			new ArrayList<>(Arrays.asList("Strafe", "Sentiel Ground", "Matrix 6.1.0")), () -> true);

	private Settings strafeSpeed = new Settings(this, "Strafe Speed", 0.23, 0.05, 1.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Strafe"));

	private TimeHelper timer = new TimeHelper();
	private int jumpTicks;
	private byte currentTick;

	@Override
	public void onDisable() {
		jumpTicks = 0;
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
			if (MoveUtil.isMoving() && Helper.getPlayer().isOnGround())
				Helper.getPlayer().jump();
		}
		
		if (currentTick <= -1) {
			currentTick = 0;
		}

		if (!Helper.getPlayer().isOnGround()) {
			jumpTicks++;
		} else if (Helper.getPlayer().isOnGround())
			jumpTicks = 0;
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
				MoveUtil.strafe(MoveUtil.getYaw(), strafeSpeed.getCurrentValueDouble());

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

			} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
				if (MoveUtil.isMoving()) {

					if (jumpTicks > 0) {
						MoveUtil.setYVelocity(-0.3);
					} else if (jumpTicks <= 0) {
						MoveUtil.setYVelocity(0.2);
						MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.7);
						event.setOnGround(true);
					}

					if (Helper.getPlayer().isOnGround() && currentTick == 0) {
						MoveUtil.getHorizontalVelocity(15.5, (float) MoveUtil.calcMoveYaw());
						currentTick = 1;
					}

					switch (currentTick) {
					case 1:
						MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().getX() * 0.7,
								Helper.getPlayer().getVelocity().getZ() * 0.7);
						currentTick = 1;
						break;
					case 2:
						MoveUtil.strafe(MoveUtil.calcMoveYaw(), 4);
						event.cancel();
						currentTick = -2;
					}

				}
			}
		}
	}
}
