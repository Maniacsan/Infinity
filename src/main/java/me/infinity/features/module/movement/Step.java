package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.entity.EntityUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Let you walk up Blocks very fast", key = -2, name = "Step", visible = true)
public class Step extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix 6.0.6",
			new ArrayList<>(Arrays.asList("Vanilla", "Intave", "Matrix 6.0.6")), () -> true);

	private boolean hasStep;

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
		InfMain.resetTimer();
		Helper.getPlayer().stepHeight = 0.5f;
	}

	@EventTarget
	public void onTick(TickEvent event) {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Intave")) {

			EntityUtil.setStepHeight(0.52f);

			if (hasStep) {
				MoveUtil.getHorizontalVelocity(MoveUtil.getSpeed(), (float) MoveUtil.calcMoveYaw());
				InfMain.TIMER = 0.9f + Helper.getPlayer().age % 4 / 21f;
				MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() - 0.01);
				MoveUtil.getHorizontalVelocity(0.002, (float) MoveUtil.calcMoveYaw());
			}

			if (Helper.getPlayer().isOnGround()) {
				InfMain.resetTimer();
				hasStep = false;
			}

			if (Helper.getPlayer().horizontalCollision && Helper.getPlayer().isOnGround()) {
				float y = (float) Math.max(1.1, 1.2);
				hasStep = true;
				InfMain.TIMER = y;
				MoveUtil.setYVelocity(0.45);
				MoveUtil.getHorizontalVelocity(5.5, (float) MoveUtil.calcMoveYaw());
				Helper.getPlayer().tickMovement();

			}

		}
	}

	@Override
	public void onPlayerTick() {
		double rheight = Helper.getPlayer().getBoundingBox().minY - Helper.getPlayer().getY();
		boolean canStep = (rheight >= 0.625D);
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
			Helper.getPlayer().stepHeight = 1.5f;
			if (Helper.getPlayer().horizontalCollision && Helper.getPlayer().isOnGround() && canStep) {
				InfMain.TIMER = 0.2f - ((rheight >= 1.0D) ? (Math.abs(1.0F - (float) rheight) * 0.2f * 0.55F) : 0.0F);
				if (InfMain.TIMER <= 0.05F)
					InfMain.TIMER = 0.05F;
				InfMain.resetTimer();
			}
		} else if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
			Helper.getPlayer().stepHeight = 1.5f;
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

		}
	}

}
