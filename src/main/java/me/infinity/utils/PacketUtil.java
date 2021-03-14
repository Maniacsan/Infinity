package me.infinity.utils;

import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.mixin.IPlayerMoveC2SPacket;
import me.infinity.mixin.IPlayerPositionLookS2CPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
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
					((IPlayerMoveC2SPacket) cp).setYaw(yaw);
					((IPlayerMoveC2SPacket) cp).setPitch(pitch);
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
			}
		}
	}

	public static void cancelServerPosition(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
				PlayerPositionLookS2CPacket serverLook = (PlayerPositionLookS2CPacket) event.getPacket();
				((IPlayerPositionLookS2CPacket) serverLook).setX(Helper.getPlayer().getX());
				((IPlayerPositionLookS2CPacket) serverLook).setY(Helper.getPlayer().getY());
				((IPlayerPositionLookS2CPacket) serverLook).setZ(Helper.getPlayer().getZ());
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

}
