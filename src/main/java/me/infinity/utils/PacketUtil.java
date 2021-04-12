package me.infinity.utils;

import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.mixin.IPlayerMoveC2SPacket;
import me.infinity.mixin.IPlayerPositionLookS2CPacket;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class PacketUtil {

	/**
	 * Packet rotation
	 * 
	 * @param event
	 * @param yaw
	 * @param pitch
	 */
	public static void setRotation(PacketEvent event, float yaw, float pitch) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				if (!Float.isNaN(yaw) || !Float.isNaN(pitch)) {
					RotationUtils.fixSensitivity((float) Helper.minecraftClient.options.mouseSensitivity, yaw, pitch);
					((IPlayerMoveC2SPacket) cp).setYaw(yaw);
					((IPlayerMoveC2SPacket) cp).setPitch(pitch);
					((IPlayerMoveC2SPacket) cp).setLook(true);
				}
			}
		}
	}

	/**
	 * Dl9 drugih buget viden rotacii tvoei cameri ,a ne packetov
	 * 
	 * @param event
	 */
	public static void cancelServerRotation(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
				PlayerPositionLookS2CPacket serverLook = (PlayerPositionLookS2CPacket) event.getPacket();
				((IPlayerPositionLookS2CPacket) serverLook).setYaw(Helper.getPlayer().yaw);
				((IPlayerPositionLookS2CPacket) serverLook).setPitch(Helper.getPlayer().pitch);
			}
		}
	}

	public static void setPosition(PacketEvent event, double x, double y, double z) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				((IPlayerMoveC2SPacket) cp).setX(x);
				((IPlayerMoveC2SPacket) cp).setY(y);
				((IPlayerMoveC2SPacket) cp).setZ(z);
				((IPlayerMoveC2SPacket) cp).setPos(true);
			}
		}
	}

	public static void setPosition(PacketEvent event, double x, double y, double z, boolean ground) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				((IPlayerMoveC2SPacket) cp).setX(x);
				((IPlayerMoveC2SPacket) cp).setY(y);
				((IPlayerMoveC2SPacket) cp).setZ(z);
				((IPlayerMoveC2SPacket) cp).setOnGround(ground);
				((IPlayerMoveC2SPacket) cp).setPos(true);
			}
		}
	}

	public static void setOnGround(PacketEvent event, boolean ground) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				((IPlayerMoveC2SPacket) cp).setOnGround(ground);
			}
		}
	}

	public static void setY(PacketEvent event, double y) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				((IPlayerMoveC2SPacket) cp).setY(y);
			}
		}
	}

	public static void fixSensitive(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket.LookOnly) {
				PlayerMoveC2SPacket.LookOnly cp = (PlayerMoveC2SPacket.LookOnly) event.getPacket();
				double sens = Helper.minecraftClient.options.mouseSensitivity / 0.005;
				double m = 0.005 * sens;
				double gcd = m * m * m * 1.2;
				((IPlayerMoveC2SPacket) cp).setYaw((float) (cp.getYaw(0) - cp.getYaw(0) % gcd));
				((IPlayerMoveC2SPacket) cp).setPitch((float) (cp.getPitch(0) - cp.getPitch(0) % gcd));
			}
		}
	}

	public static void cancelMotionPackets(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				event.cancel();
			}
		}
	}

	public static void cancelCommandPackets(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof ClientCommandC2SPacket) {
				event.cancel();
			}
		}
	}

	public static void cancelServerKeepAlive(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof KeepAliveS2CPacket) {
				event.cancel();
			}
		}
	}

	public static void cancelKeepAlive(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof KeepAliveC2SPacket) {
				event.cancel();
			}
		}
	}

}
