package me.infinity.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class Helper {


	// from net.minecraft
	public static MinecraftClient minecraftClient = MinecraftClient.getInstance();
	

	// Utils
	private static MoveUtil moveUtil = new MoveUtil();
	private static UpdateUtil updateUtil = new UpdateUtil();
	private static RenderUtil renderUtil = new RenderUtil();

	/*
	 * Static import for net.minecraft
	 */
	public static ClientPlayerEntity getPlayer() {
		return minecraftClient.player;
	}

	public static World getWorld() {
		return minecraftClient.world;
	}

	/*
	 * Static import for Utilites
	 */
	public static MoveUtil getMoveUtil() {
		return moveUtil;
	}

	public static UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public static RenderUtil getRenderUtil() {
		return renderUtil;
	}

	//

	public static void sendPacket(Packet<?> packet) {
		getPlayer().networkHandler.sendPacket(packet);
	}

}