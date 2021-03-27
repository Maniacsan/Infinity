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
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.block.BlockUtil;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Lets you walk on water", key = -2, name = "Jesus", visible = true)
public class Jesus extends Module {

	private Settings mode = new Settings(this, "Mode", "Swing",
			new ArrayList<>(Arrays.asList("Swing", "Matrix 6.0.6", "NGrief")), () -> true);

	private boolean water;

	private double dir;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
		double offsetY = MathAssist.random(0.01, 0.3);

		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
			double jumpY = MathAssist.random(0.05, 0.1);
			if (BlockUtil.isFluid(new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getY() + offsetY,
					Helper.getPlayer().getZ()))) {
				if (Helper.minecraftClient.options.keyJump.isPressed()) {
					Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, jumpY,
							Helper.getPlayer().getVelocity().z);
				} else
					Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, 0,
							Helper.getPlayer().getVelocity().z);
			}

		} else if (mode.getCurrentMode().equalsIgnoreCase("Swing")) {
			if (Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava() || water) {
				if (Helper.getPlayer().age % 3 == 0) {
					if (Helper.minecraftClient.options.keyForward.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.7), 0.2, Math.cos(dir) * 0.7);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.4), 0.2, Math.cos(dir) * 0.4);
					}
					if (Helper.minecraftClient.options.keyLeft.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw - 90);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.6), 0.2, Math.cos(dir) * 0.6);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.3), 0.2, Math.cos(dir) * 0.3);
					}
					if (Helper.minecraftClient.options.keyRight.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw + 90);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.6), 0.2, Math.cos(dir) * 0.6);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.3), 0.2, Math.cos(dir) * 0.3);
					}
				}
			}
		} else if (mode.getCurrentMode().equalsIgnoreCase("NGrief")) {
			if (Helper.getPlayer().age % 5 == 0) {
				Helper.getPlayer().setPos(Helper.getPlayer().getPos().x, Helper.getPlayer().getPos().y + 3.0,
						Helper.getPlayer().getPos().z);
			}
			if (Helper.minecraftClient.options.keyLeft.isPressed()) {
				MoveUtil.hClip(0, -0.5f);
			}
			if (Helper.minecraftClient.options.keyRight.isPressed()) {
				MoveUtil.hClip(0, 0.5f);
			}

			if (Helper.minecraftClient.options.keyBack.isPressed()) {
				MoveUtil.hClip(-0.5f, 0);
			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Swing")) {

				if (Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava() || water) {
					if (Helper.getPlayer().age % 2 == 0) {
						if (Helper.minecraftClient.options.keyRight.isPressed()
								|| Helper.minecraftClient.options.keyLeft.isPressed()
								|| Helper.minecraftClient.options.keyForward.isPressed())
							Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, -0.2,
									Helper.getPlayer().getVelocity().z);
					}
				}
			}
		} else if (event.getType().equals(EventType.POST)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Swing")) {
				water = Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava();
				if (Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava()) {
					if (Helper.minecraftClient.options.keyRight.isPressed()
							|| Helper.minecraftClient.options.keyLeft.isPressed()
							|| Helper.minecraftClient.options.keyForward.isPressed())
						return;
					Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, 0.2,
							Helper.getPlayer().getVelocity().z);
				}
			}
		}
	}

}
