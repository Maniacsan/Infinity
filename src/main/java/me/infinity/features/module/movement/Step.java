package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Let you walk up Blocks very fast", key = -2, name = "Step", visible = true)
public class Step extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix 6.1.0",
			new ArrayList<>(Arrays.asList("Vanilla", "Intave", "Matrix 6.1.0", "NCP")), () -> true);

	private Settings height = new Settings(this, "Height", 1.5, 0.5, 10.0,
			() -> mode.getCurrentMode().equalsIgnoreCase("Vanilla"));

	private double previousX, previousY, previousZ;
	private double offsetX, offsetY, offsetZ;
	private byte cancelStage;
	private boolean hasStep;

	// ncp
	public static boolean cancelSomePackets;
	private int stepTicks;

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
		InfMain.resetTimer();
		Helper.getPlayer().stepHeight = 0.6f;
	}

	@EventTarget
	public void onTick(TickEvent event) {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Intave")) {

			EntityUtil.setStepHeight(1.02f);

			if (hasStep) {
				MoveUtil.getHorizontalVelocity(MoveUtil.getSpeed(), (float) MoveUtil.calcMoveYaw());
				InfMain.TIMER = 0.9f + Helper.getPlayer().age % 4 / 21f;
			}

			if (Helper.getPlayer().isOnGround()) {
				InfMain.resetTimer();
				hasStep = false;
			}

			if (Helper.getPlayer().horizontalCollision && Helper.getPlayer().isOnGround()) {
				float y = (float) Math.max(0.5, 0.4);
				hasStep = true;
				InfMain.TIMER = y;
				MoveUtil.setYVelocity(0.45);
				Helper.getPlayer().tick();
				Helper.getPlayer().tick();
			}
		}
	}

	@Override
	public void onPlayerTick() {
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
			if (Helper.minecraftClient.options.keyJump.isPressed())
				EntityUtil.setStepHeight(0.6f);
			else
				EntityUtil.setStepHeight(1.4f);
		} else if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
			EntityUtil.setStepHeight((float) height.getCurrentValueDouble());
		}

		if (mode.getCurrentMode().equalsIgnoreCase("NCP")) {
			if (Helper.getPlayer().horizontalCollision) {
				cancelSomePackets = true;
				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY() + (.41999998688698 * (1 + Helper.getPlayer().stepHeight)),
						Helper.getPlayer().getZ(), Helper.getPlayer().isOnGround()));
				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY() + (.7531999805212 * (1 + Helper.getPlayer().stepHeight)),
						Helper.getPlayer().getZ(), Helper.getPlayer().isOnGround()));
			} else {
				InfMain.resetTimer();
			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
				if (Helper.minecraftClient.options.keyJump.isPressed())
					return;

				offsetX = 0;
				offsetY = 0;
				offsetZ = 0;

				if (cancelStage == -1) {
					cancelStage = 0;
					return;
				}

				double yDist = Helper.getPlayer().getY() - previousY;
				double hDistSq = (Helper.getPlayer().getX() - previousX) * (Helper.getPlayer().getX() - previousX)
						+ (Helper.getPlayer().getZ() - previousZ) * (Helper.getPlayer().getZ() - previousZ);

				if (yDist > 0.5 && yDist < 1.05 && hDistSq < 1 && cancelStage == 0) {
					Helper.sendPacket(
							new PlayerMoveC2SPacket.PositionOnly(previousX, previousY + 0.42, previousZ, false));
					offsetX = previousX - Helper.getPlayer().getX();
					offsetY = 0.755 - yDist;
					offsetZ = previousZ - Helper.getPlayer().getZ();

					Helper.getPlayer().stepHeight = 1.05F;
					cancelStage = 1;
				}

				switch (cancelStage) {
				case 1:
					cancelStage = 2;
					break;
				case 2:
					event.cancel();
					cancelStage = -1;
					break;
				}

				previousX = Helper.getPlayer().getX();
				previousY = Helper.getPlayer().getY();
				previousZ = Helper.getPlayer().getZ();

				if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
					MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().getX() + offsetX,
							Helper.getPlayer().getVelocity().getZ() + offsetZ);
					Helper.getPlayer().setBoundingBox(Helper.getPlayer().getBoundingBox().offset(0, 0, 0));
				}

			} else {
				if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
					MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().getX() - offsetX,
							Helper.getPlayer().getVelocity().getZ() - offsetZ);
					Helper.getPlayer().setBoundingBox(Helper.getPlayer().getBoundingBox().offset(0, -offsetY, 0));
				}
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {

			if (mode.getCurrentMode().equalsIgnoreCase("NCP")) {
				if (Helper.getPlayer() != null && Helper.getPlayer().isOnGround()
						|| (Helper.getPlayer().verticalCollision))
					Helper.getPlayer().stepHeight = 1F;

				if (cancelSomePackets) {
					InfMain.resetTimer();
					this.stepTicks++;
				}
				if (this.stepTicks >= 3) {
					InfMain.TIMER = 0.35F;
					cancelSomePackets = false;
					this.stepTicks = 0;
				}
			}
		}
	}

}
