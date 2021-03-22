package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.ILivingEntity;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.PlayerSend;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Critical hit", key = GLFW.GLFW_KEY_B, name = "Criticals", visible = true)
public class Criticals extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList(new String[] { "Jump", "Packet" })), () -> true);
	
	private boolean attack;
	private double attackTick;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotion(MotionEvent event) {
		if (event.getType().equals(EventType.POST)) {
			if (this.mode.getCurrentMode().equals("Packet")) {
				double x = Helper.getPlayer().getX();
				double y = Helper.getPlayer().getY();
				double z = Helper.getPlayer().getZ();
				
				PlayerSend.setPosition(x, y + 1.1E-5, z, false);
				PlayerSend.setPosition(x, y, z, false);
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType() == EventType.SEND) {
			if (this.mode.getCurrentMode().equals("Packet")) {
				Packet<?> packet = event.getPacket();
				if (packet instanceof PlayerInteractEntityC2SPacket && ((PlayerInteractEntityC2SPacket) packet)
						.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {

				}
			}
		}
	}

}
