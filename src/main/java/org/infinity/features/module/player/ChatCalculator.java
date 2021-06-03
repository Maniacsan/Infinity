package org.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.infinity.event.PacketEvent;
import org.infinity.event.ServerChatEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;
import org.infinity.utils.calc.TermSolver;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@ModuleInfo(category = Category.PLAYER, desc = "Automatically solves examples", key = -2, name = "ChatCalculator", visible = true)
public class ChatCalculator extends Module {

	private Setting mode = new Setting(this, "Mode", "Message", new ArrayList<>(Arrays.asList("Message", "Info")));

	private Setting serverSide = new Setting(this, "Server Messages", true);

	private Setting globalChat = new Setting(this, "Global Chat", true)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Message"));

	private Setting delay = new Setting(this, "Delay", 1D, 0D, 5D);

	private String serverMessage;

	private String clientResult = null;
	private String serverResult = null;

	private List<String> solvesResults = new ArrayList<>();

	private Timer timer = new Timer();

	@Override
	public void onDisable() {
		clientResult = null;
		serverResult = null;

		if (!solvesResults.isEmpty())
			solvesResults.clear();
	}

	@Override
	public void onPlayerTick() {
		if (solvesResults.size() > 3) {
			solvesResults.remove(solvesResults.size() - 1);
		}

		if (clientResult != null) {
			(new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep((long) (delay.getCurrentValueDouble() * 1000));
						if (mode.getCurrentMode().equalsIgnoreCase("Message")) {
							if (clientResult == null)
								return;

							Helper.getPlayer()
									.sendChatMessage(globalChat.isToggle() ? "!" + clientResult : clientResult);
							clientResult = null;
						} else if (mode.getCurrentMode().equalsIgnoreCase("Info")) {
							if (clientResult == null)
								return;

							Helper.infoMessage(clientResult);
							clientResult = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	@EventTarget
	public void onServerMessages(ServerChatEvent event) {
		if (serverSide.isToggle()) {

			String message = event.getMessage().replace(" ", "");

			String split = message.split(":")[0];

			if (split.contains(Helper.getPlayer().getEntityName()))
				return;

			serverMessage = message.contains("Решите пример:") ? message.replace("Решите пример:", "")
					: message.replace(split + ":", "");

			Optional<String> postfix = TermSolver.transformInfixToPostfix(serverMessage);

			try {

				String result = String.valueOf((int) TermSolver.solvePostfix(postfix.get()));

				if (solvesResults.contains(result))
					return;

				timer.reset();
				serverResult = result;

				if (timer.hasReached(delay.getCurrentValueDouble() * 1000)) {
					if (mode.getCurrentMode().equalsIgnoreCase("Message")) {
						Helper.getPlayer().sendChatMessage(globalChat.isToggle() ? "!" + serverResult : serverResult);
						solvesResults.add(serverResult);
						serverResult = null;
						serverMessage = null;

					} else if (mode.getCurrentMode().equalsIgnoreCase("Info")) {

						Helper.infoMessage(serverResult);
						serverResult = null;
						serverMessage = null;
					}
				}

			} catch (NumberFormatException ex) {
			}

		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof ChatMessageC2SPacket) {
				ChatMessageC2SPacket cms = (ChatMessageC2SPacket) event.getPacket();

				if (InfMain.getChatHud().currentChat == InfMain.getChatHud().infChat)
					return;

				String chatMessage = cms.getChatMessage().contains("Решите пример:")
						? cms.getChatMessage().replace("Решите пример:", "")
						: cms.getChatMessage().replace("!", "");

				Optional<String> postfix = TermSolver.transformInfixToPostfix(chatMessage);

				if (postfix.isPresent()) {

					double result = TermSolver.solvePostfix(postfix.get());

					clientResult = String.valueOf((int) result);
				}
			}
		}
	}
}
