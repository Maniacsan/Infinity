package me.infinity.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class Helper {

	// from net.minecraft
	public static MinecraftClient minecraftClient = MinecraftClient.getInstance();

	/*
	 * Static import for net.minecraft
	 */
	public static ClientPlayerEntity getPlayer() {
		return minecraftClient.player;
	}

	public static ClientWorld getWorld() {
		return minecraftClient.world;
	}
	

	//

	public static void sendPacket(Packet<?> packet) {
		getPlayer().networkHandler.sendPacket(packet);
	}

	public static void infoMessage(String message) {
		minecraftClient.inGameHud.getChatHud()
				.addMessage(new LiteralText(Formatting.BLUE + "Infinity" + Formatting.WHITE + ": " + message));
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static String replaceNull(String str) {
		return str == null ? "" : str + " ";
	}
	
	public static String DF(Number value, int maxvalue) {
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(maxvalue);
		return df.format(value);
	}

}