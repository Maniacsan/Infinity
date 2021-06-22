package org.infinity.utils;

import org.infinity.main.InfMain;
import org.infinity.utils.user.UserRole;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class Helper {

	public static MinecraftClient minecraftClient = MinecraftClient.getInstance();

	public static ClientPlayerEntity getPlayer() {
		return minecraftClient.player;
	}

	public static ClientWorld getWorld() {
		return minecraftClient.world;
	}

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
		Util.getOperatingSystem().open(url);
	}

	public static boolean isUser() {
		return InfMain.getUser().getRole().equals(UserRole.User);
	}

	public static boolean isPremium() {
		return InfMain.getUser().getRole().equals(UserRole.Premium);
	}

	public static boolean isModerator() {
		return InfMain.getUser().getRole().equals(UserRole.Moderator);
	}

	public static boolean isAdmin() {
		return InfMain.getUser().getRole().equals(UserRole.Admin);
	}

	public static boolean isCut(int code) {
		return code == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int code) {
		return code == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int code) {
		return code == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int code) {
		return code == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean hasControlDown() {
		if (MinecraftClient.IS_SYSTEM_MAC) {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343)
					|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347);
		} else {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341)
					|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
		}
	}

	public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
	}

	public static boolean hasAltDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346);
	}

}