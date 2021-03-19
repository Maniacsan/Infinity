package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.event.ServerChatEvent;
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

	private Settings delay = new Settings(this, "Delay", 1D, 0D, 20D,
			() -> clientSide.isToggle() || serverSide.isToggle());

	private int time;

	private boolean sendClient;
	private boolean sendServer;

	private String clientResult = null;
	private String serverResult = null;

	@Override
	public void onDisable() {
		time = 0;
		clientResult = null;
		serverResult = null;
		sendClient = false;
		sendServer = false;
	}

	@Override
	public void onPlayerTick() {

		// Client side
		if (sendClient) {

			// chtobi ne proizoshel vzriv pc
			sendServer = false;

			if (time > 0) {
				time--;
				return;
			}

			if (clientResult != null) {

				if (clientMode.getCurrentMode().equalsIgnoreCase("Message")) {
					Helper.getPlayer().sendChatMessage(clientResult);
				} else if (clientMode.getCurrentMode().equalsIgnoreCase("Info")) {
					Helper.infoMessage(clientResult);
				}

			}
			sendClient = false;
		}
	}

	@EventTarget
	public void onServerMessages(ServerChatEvent event) {
		if (serverSide.isToggle()) {

			String message = event.getMessage();
			
			String split = message.split(":")[0];

			String chatMessage = message.contains("Решите пример:") ? message.replace("Решите пример:", "") : message.replace(split + ":", "");

			Optional<String> postfix = TermSolver.transformInfixToPostfix(chatMessage);

			double result = TermSolver.solvePostfix(postfix.get());

			serverResult = String.valueOf((int) result);

			time = (int) delay.getCurrentValueDouble();

			if (serverMode.getCurrentMode().equalsIgnoreCase("Message")) {
				Helper.getPlayer().networkHandler.sendPacket(new ChatMessageC2SPacket(String.valueOf((int) result)));
			} else if (serverMode.getCurrentMode().equalsIgnoreCase("Info")) {
				Helper.infoMessage(String.valueOf((int) result));
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
							: cms.getChatMessage();

					Optional<String> postfix = TermSolver.transformInfixToPostfix(chatMessage);

					double result = TermSolver.solvePostfix(postfix.get());

					clientResult = String.valueOf((int) result);

					time = (int) delay.getCurrentValueDouble();

					sendClient = true;
				}
			}
		}
	}
}
