package org.infinity.utils;

import org.infinity.InfMain;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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

	public static void ircMessage(String text) {
		InfMain.getChatHud().addInfMessage(new LiteralText(text));
	}

	public static void infoMessage(String message) {
		minecraftClient.inGameHud.getChatHud()
				.addMessage(new LiteralText(Formatting.BLUE + "Infinity" + Formatting.WHITE + ": " + message));
	}

	public static void openScreen(Screen screen) {
		minecraftClient.openScreen(screen);
	}

	public static void openSite(String url) {

		Text component = new LiteralText(url);

		ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		component.getStyle().withClickEvent(event);

	}

}