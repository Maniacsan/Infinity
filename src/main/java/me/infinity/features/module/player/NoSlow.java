package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Direction;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Do not slow down when using / eating", key = -2, name = "NoSlow", visible = true)
public class NoSlow extends Module {

	public Settings mode = new Settings(this, "Mode", "Vanilla", new ArrayList<>(Arrays.asList("Vanilla")), () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onSend(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {

			if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
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
