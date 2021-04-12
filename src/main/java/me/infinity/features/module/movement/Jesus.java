package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IKeyBinding;
import me.infinity.utils.Helper;
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.block.BlockUtil;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Lets you walk on water", key = -2, name = "Jesus", visible = true)
public class Jesus extends Module {

	private Settings mode = new Settings(this, "Mode", "Swing", new ArrayList<>(Arrays.asList("Swing", "Matrix 6.0.6")),
			() -> true);

	private Settings speed = new Settings(this, "Speed", 0.32, 0.2, 0.5,
			() -> mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6"));

	private boolean water;

	private double dir;

	@Override
	public void onDisable() {
		KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyJump).getBoundKey(), false);
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
		double offsetY = MathAssist.random(0.01, 0.3);
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
			if (BlockUtil.isFluid(new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getY() + offsetY,
					Helper.getPlayer().getZ()))) {

				Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, 0,
						Helper.getPlayer().getVelocity().z);
			}

			if (Helper.getPlayer().isTouchingWater()) {
				Helper.getPlayer().setSprinting(false);
				MoveUtil.strafe(MoveUtil.calcMoveYaw(), speed.getCurrentValueDouble());
				KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyJump).getBoundKey(), true);
			} else
				KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyJump).getBoundKey(), false);

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
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

		} else if (mode.getCurrentMode().equalsIgnoreCase("Swing")) {

			if (Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava() || water) {
				if (Helper.getPlayer().age % 2 == 0) {
					if (Helper.minecraftClient.options.keyRight.isPressed()
							|| Helper.minecraftClient.options.keyLeft.isPressed()
							|| Helper.minecraftClient.options.keyForward.isPressed())
						Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().x, -0.2,
								Helper.getPlayer().getVelocity().z);
				}
			}
		} else if (event.getType().equals(EventType.POST)) {
			water = Helper.getPlayer().isTouchingWater() || Helper.getPlayer().isInLava();
			if (mode.getCurrentMode().equalsIgnoreCase("Swing")) {
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
