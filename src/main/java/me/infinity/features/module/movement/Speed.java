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
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Make you faster", key = -2, name = "Speed", visible = true)
public class Speed extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix",
			new ArrayList<>(Arrays.asList("Matrix 6.0.6", "Sentiel Ground", "Strafe")), () -> true);

	private Settings strafeSpeed = new Settings(this, "Strafe Speed", 0.23, 0.05, 1.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Strafe"));

	private int timeTicks;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {
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
				
				float speed = (float) MathAssist.random(9.8, 11.1);

				if (timeTicks <= 4 && timeTicks != 0) {
					InfMain.TIMER = 0.2f;
					MoveUtil.getHorizontalVelocity(speed, (float) Helper.getPlayer().yaw);
					MoveUtil.setYVelocity(0);
				}
				
				if (timeTicks > 4) {
					InfMain.TIMER = 0.12f;
					
					MoveUtil.setYVelocity(-2);
					
					MoveUtil.getHorizontalVelocity(5.8f, (float) Helper.getPlayer().yaw);
					
					if (Helper.getPlayer().age % 2 == 0)
					MoveUtil.getHorizontalVelocity(4.2f, (float) MoveUtil.calcMoveYaw());
					
				}
				
				if (timeTicks == 0) {
					timeTicks++;
				}
				
				if (Helper.getPlayer().age % 3 == 0) {
					InfMain.resetTimer();
					timeTicks = 0;
		
				}
				
			}

		}
	}

}
