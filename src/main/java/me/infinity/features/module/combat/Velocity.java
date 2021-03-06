package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IEntityVelocityUpdateS2CPacket;
import me.infinity.utils.Helper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

@ModuleInfo(name = "Velocity", key = -2, visible = true, desc = "Anti knockback", category = Module.Category.COMBAT)
public class Velocity extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet", new ArrayList<>(Arrays.asList("Packet", "Matrix")),
			() -> true);
	private Settings vertical = new Settings(this, "Vertical", 0.0D, 0.0D, 100.0D,
			() -> Boolean.valueOf(mode.getCurrentMode().equalsIgnoreCase("Packet")));
	private Settings horizontal = new Settings(this, "Horizontal", 0.0D, 0.0D, 100.0D,
			() -> Boolean.valueOf(mode.getCurrentMode().equalsIgnoreCase("Packet")));

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.POST)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix")) {
				if (Helper.getPlayer().hurtTime > 0) {
					Helper.getPlayer().setVelocity(0, 0, 0);
				}
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
				Packet<?> packet = event.getPacket();
				if (packet instanceof EntityVelocityUpdateS2CPacket) {
					EntityVelocityUpdateS2CPacket vp = (EntityVelocityUpdateS2CPacket) packet;
					if (vp.getId() == Helper.getPlayer().getEntityId()) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX((int) vertical.getCurrentValueDouble());
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY((int) horizontal.getCurrentValueDouble());
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ((int) vertical.getCurrentValueDouble());

					}
				}
			}
		}
	}

}
