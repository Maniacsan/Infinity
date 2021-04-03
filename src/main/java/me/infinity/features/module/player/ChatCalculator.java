package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.event.ServerChatEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.calc.TermSolver;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically solves examples from the server", key = -2, name = "ChatCalc", visible = true)
public class ChatCalculator extends Module {

	// server
	private Settings serverSide = new Settings(this, "Server Side", true, () -> true);
	private Settings serverMode = new Settings(this, "Server", "Message",
			new ArrayList<>(Arrays.asList("Message", "Info")), () -> serverSide.isToggle());

	// client
	private Settings clientSide = new Settings(this, "Client Side", true, () -> true);
	private Settings clientMode = new Settings(this, "Client", "Message",
			new ArrayList<>(Arrays.asList("Message", "Info")), () -> clientSide.isToggle());

	private Settings globalChat = new Settings(this, "Global Chat", true,
			() -> clientMode.getCurrentMode().equalsIgnoreCase("Message")
					|| serverMode.getCurrentMode().equalsIgnoreCase("Message"));

	private Settings delay = new Settings(this, "Delay", 1D, 0D, 5D,
			() -> clientSide.isToggle() || serverSide.isToggle());

	private boolean sendClient;
	private boolean sendServer;

	private String clientResult = null;
	private String serverResult = null;

	@Override
	public void onDisable() {
		clientResult = null;
		serverResult = null;
		sendClient = false;
		sendServer = false;
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (sendClient) { /* client side */

			sendServer = false;

			if (clientResult != null) {

				(new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep((long) (delay.getCurrentValueDouble() * 1000));
							if (clientMode.getCurrentMode().equalsIgnoreCase("Message")) {
								Helper.getPlayer()
										.sendChatMessage(globalChat.isToggle() ? "!" + clientResult : clientResult);
								clientResult = null;
							} else if (clientMode.getCurrentMode().equalsIgnoreCase("Info")) {
								Helper.infoMessage(clientResult);
								clientResult = null;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}

			sendClient = false;
		} else if (sendServer) { /* server side */

			if (serverResult != null) {

				(new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep((long) (delay.getCurrentValueDouble() * 1000));
							if (serverMode.getCurrentMode().equalsIgnoreCase("Message")) {
								Helper.getPlayer()
										.sendChatMessage(globalChat.isToggle() ? "!" + serverResult : serverResult);
								serverResult = null;
							} else if (serverMode.getCurrentMode().equalsIgnoreCase("Info")) {
								Helper.infoMessage(serverResult);
								serverResult = null;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			sendServer = false;
		}
	}

	@EventTarget
	public void onServerMessages(ServerChatEvent event) {
		if (serverSide.isToggle()) {

			String message = event.getMessage().replace(" ", "");

			String split = message.split(":")[0];

			String chatMessage = message.contains("Решите пример:") ? message.replace("Решите пример:", "")
					: message.replace(split + ":", "");

			Optional<String> postfix = TermSolver.transformInfixToPostfix(chatMessage);

			if (postfix.isPresent()) {

				double result = TermSolver.solvePostfix(postfix.get());

				serverResult = String.valueOf((int) result);
				sendServer = true;
			}

		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (clientSide.isToggle()) {
				if (event.getPacket() instanceof ChatMessageC2SPacket) {
					ChatMessageC2SPacket cms = (ChatMessageC2SPacket) event.getPacket();

					String chatMessage = cms.getChatMessage().contains("Решите пример:")
							? cms.getChatMessage().replace("Решите пример:", "")
							: cms.getChatMessage().replace("!", "");

					Optional<String> postfix = TermSolver.transformInfixToPostfix(chatMessage);

					double result = TermSolver.solvePostfix(postfix.get());

					clientResult = String.valueOf((int) result);

					sendClient = true;
				}
			}
		}
	}
}
