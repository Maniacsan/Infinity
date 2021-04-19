package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.PacketEvent;
import me.infinity.event.ServerChatEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.calc.TermSolver;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically solves examples", key = -2, name = "ChatCalculator", visible = true)
public class ChatCalculator extends Module {

	private Settings mode = new Settings(this, "Mode", "Message", new ArrayList<>(Arrays.asList("Message", "Info")),
			() -> true);

	private Settings serverSide = new Settings(this, "Server Messages", true, () -> true);

	private Settings globalChat = new Settings(this, "Global Chat", true,
			() -> mode.getCurrentMode().equalsIgnoreCase("Message"));

	private Settings delay = new Settings(this, "Delay", 1D, 0D, 5D, () -> true);

	private String serverMessage;

	private String clientResult = null;
	private String serverResult = null;

	private List<String> solvesResults = new ArrayList<>();

	private TimeHelper timer = new TimeHelper();

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
