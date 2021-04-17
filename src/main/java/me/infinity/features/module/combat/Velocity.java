package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.CaretEvent;

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
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

@ModuleInfo(name = "Velocity", key = -2, visible = true, desc = "Anti knockback", category = Module.Category.COMBAT)
public class Velocity extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList("Packet", "Reverse", "Matrix 6.1.0")), () -> true);
	private Settings vertical = new Settings(this, "Vertical", 0.0D, 0.0D, 100.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
					|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));
	private Settings horizontal = new Settings(this, "Horizontal", 0.0D, 0.0D, 100.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
					|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));

	private EntityVelocityUpdateS2CPacket sVel;

	@Override
	public void onDisable() {
		sVel = null;
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
				if (Helper.getPlayer().hurtTime > 0 && sVel != null) {
					event.setX(event.getX() + sVel.getVelocityX() / 8000);
					event.setY(event.getY() - sVel.getVelocityY() / 8000);
					event.setZ(event.getZ() + sVel.getVelocityZ() / 8000);
					event.cancel();
					sVel = null;
				}
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			Packet<?> packet = event.getPacket();
			if (packet instanceof EntityVelocityUpdateS2CPacket) {
				EntityVelocityUpdateS2CPacket vp = (EntityVelocityUpdateS2CPacket) packet;
				if (vp.getId() == Helper.getPlayer().getEntityId()) {
					sVel = vp;
					int x = vp.getVelocityX() * (int) horizontal.getCurrentValueDouble() / 100;
					int y = vp.getVelocityY() * (int) vertical.getCurrentValueDouble() / 100;
					int z = vp.getVelocityZ() * (int) horizontal.getCurrentValueDouble() / 100;
					if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(x);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(y);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(z);

					} else if (mode.getCurrentMode().equalsIgnoreCase("Reverse")) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(-x);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(-y);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(-z);

					} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {

						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(0);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(0);

					}
				}
			} else if (packet instanceof ExplosionS2CPacket) {
				event.cancel();
			}
		}
	}

}
