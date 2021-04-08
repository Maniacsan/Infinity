package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Direction;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Do not slow down when using / eating", key = -2, name = "NoSlow", visible = true)
public class NoSlow extends Module {

	public Settings mode = new Settings(this, "Mode", "Vanilla",
			new ArrayList<>(Arrays.asList("Vanilla", "NCP", "Matrix 6.1.0")), () -> true);

	private Settings packet = new Settings(this, "Packet", true,
			() -> mode.getCurrentMode().equalsIgnoreCase("Vanilla"));

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			if (mode.getCurrentMode().equalsIgnoreCase("NCP")) {
				if (Helper.getPlayer().isUsingItem() && !Helper.getPlayer().hasVehicle()) {
					if (!Helper.getPlayer().isOnGround())
						return;
					double d = Math.abs(Helper.getPlayer().getVelocity().y);
					if (d < 0.1D) {
						double e = 0.4D + d * 0.72D;
						Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().multiply(e, 1.0D, e));
					}
				}
			} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
				if (Helper.getPlayer().isUsingItem() && !Helper.getPlayer().hasVehicle()) {
					double rSpeed = MathAssist.random(0.11, 0.1335445);

					if (!Helper.getPlayer().isOnGround())
						return;

					Helper.getPlayer().yaw = Helper.minecraftClient.getCameraEntity().yaw;

					MoveUtil.strafe(MoveUtil.getYaw(), rSpeed);

					if (Helper.getPlayer().age % 7 == 0) {
						double d = Math.abs(Helper.getPlayer().getVelocity().y);
						if (d < 0.1D) {
							double e = 0.4D + d * 1.5D;
							Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().multiply(e, 1.0D, e));
						}
						
						MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed() * 0.99);
						MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.145);
						
					}
				}
			}
		}
	}

	@EventTarget
	public void onSend(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {

			if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && packet.isToggle()) {
				if (event.getPacket() instanceof PlayerMoveC2SPacket) {
					if (Helper.getPlayer().isUsingItem() && !Helper.getPlayer().hasVehicle()) {
						Helper.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK,
								EntityUtil.getPlayerPosFloor(), Direction.DOWN));
					}
				}
			}
		}
	}
}
