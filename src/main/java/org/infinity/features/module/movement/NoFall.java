package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.ClipEvent;
import org.infinity.event.MotionEvent;
import org.infinity.event.PacketEvent;
import org.infinity.event.PushOutBlockEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(category = Category.MOVEMENT, desc = "Do not receive damage from falling", key = -2, name = "NoFall", visible = true)
public class NoFall extends Module {

	private Setting mode = new Setting(this, "Mode", "Matrix 6.1.1",
			new ArrayList<>(Arrays.asList("Vanilla", "Packet")));

	private boolean gbool;
	private int yoff;
	private int y2;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
		if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
			if (Helper.getPlayer().fallDistance > 2.5 && !Helper.getPlayer().isFallFlying())
				Helper.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
		} else if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
			if (Helper.getPlayer().fallDistance > 2.5f && Helper.getWorld().getBlockState(
					Helper.getPlayer().getBlockPos().add(0, -1.5 + (Helper.getPlayer().getVelocity().y * 0.1), 0))
					.getBlock() != Blocks.AIR) {
				Helper.getPlayer().networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
				Helper.getPlayer().networkHandler
						.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Helper.getPlayer().getX(),
								Helper.getPlayer().getY() - 420.69, Helper.getPlayer().getZ(), true));
				Helper.getPlayer().fallDistance = 0;
			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.1")) {
			if (Helper.getPlayer().fallDistance > 3.0f) {

			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {

	}

	@EventTarget
	public void onPushBlock(PushOutBlockEvent event) {

	}

	@EventTarget
	public void onClip(ClipEvent event) {

	}

}