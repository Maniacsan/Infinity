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
import me.infinity.utils.MoveUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

@ModuleInfo(name = "Velocity", key = -2, visible = true, desc = "Anti knockback", category = Module.Category.COMBAT)
public class Velocity extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList("Packet", "Matrix 6.0.6")), () -> true);
	private Settings vertical = new Settings(this, "Vertical", 0.0D, 0.0D, 100.0D,
			() -> Boolean.valueOf(mode.getCurrentMode().equalsIgnoreCase("Packet")));
	private Settings horizontal = new Settings(this, "Horizontal", 0.0D, 0.0D, 100.0D,
			() -> Boolean.valueOf(mode.getCurrentMode().equalsIgnoreCase("Packet")));

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
			if (mode.getCurrentMode().equalsIgnoreCase("Packet")
					|| mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
				if (sVel != null) {
					event.setX(event.getX() + sVel.getVelocityX() / 8000);
					event.setZ(event.getZ() + sVel.getVelocityZ() / 8000);
					sVel = null;
				}
			}	
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
				if (Helper.getPlayer().isOnGround()) 
					event.setY(-0.1);	
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
					if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
						((IEntityVelocityUpdateS2CPacket) vp)
								.setVelocityX(vp.getVelocityX() * (int) vertical.getCurrentValueDouble() / 100);
						((IEntityVelocityUpdateS2CPacket) vp)
								.setVelocityY(vp.getVelocityY() * (int) horizontal.getCurrentValueDouble() / 100);
						((IEntityVelocityUpdateS2CPacket) vp)
								.setVelocityZ(vp.getVelocityZ() * (int) vertical.getCurrentValueDouble() / 100);
					} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(vp.getVelocityX() * 0);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(vp.getVelocityZ() * 0);
						
						if (Helper.getPlayer().isOnGround()) {
							((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(vp.getVelocityY() * 0);
						}
						
					}
				}
			}
		}
	}

}
