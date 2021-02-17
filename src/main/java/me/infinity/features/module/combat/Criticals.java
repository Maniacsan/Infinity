package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Critical hit", key = GLFW.GLFW_KEY_B, name = "Criticals", visible = true)
public class Criticals extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList(new String[] { "Jump", "Packet" })));

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType() == EventType.SEND) {
			if (this.mode.getCurrentMode().equals("Packet")) {
				if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
					PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
					if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
						criticalHit(event);
					}
				}
			}
		}
	}

	private void criticalHit(PacketEvent event) {
		Packet<?> packet = event.getPacket();
		if (packet instanceof PlayerMoveC2SPacket) {
			event.cancel();
			event.setPacket(new PlayerMoveC2SPacket.Both(Helper.getPlayer().getX(),
					Helper.getPlayer().getY() + 0.05000000074505806D, Helper.getPlayer().getZ(), Helper.getPlayer().yaw,
					Helper.getPlayer().pitch, false));
			event.setPacket(new PlayerMoveC2SPacket.Both(Helper.getPlayer().getX(), Helper.getPlayer().getY(),
					Helper.getPlayer().getZ(), Helper.getPlayer().yaw, Helper.getPlayer().pitch, false));
			event.setPacket(new PlayerMoveC2SPacket.Both(Helper.getPlayer().getX(),
					Helper.getPlayer().getY() + 0.012511000037193298D, Helper.getPlayer().getZ(),
					Helper.getPlayer().yaw, Helper.getPlayer().pitch, false));
			event.setPacket(new PlayerMoveC2SPacket.Both(Helper.getPlayer().getX(), Helper.getPlayer().getY(),
					Helper.getPlayer().getZ(), Helper.getPlayer().yaw, Helper.getPlayer().pitch, false));

		}
	}

}
