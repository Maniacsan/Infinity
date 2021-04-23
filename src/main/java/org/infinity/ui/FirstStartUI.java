package org.infinity.ui;

import org.infinity.InfMain;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.features.command.Command;
import org.infinity.utils.Helper;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class FirstStartUI extends Screen {

	private ButtonWidget continueButton;
	private int timer;

	public FirstStartUI() {
		super(new LiteralText(""));
		timer = 80;
	}

	@Override
	public void init() {

		addButton(continueButton = new ButtonWidget(width - 100, height - 30, 70, 20, new TranslatableText("Continue"), (continueButton) -> {
			Helper.getPlayer().closeScreen();
			InfMain.firstStart = false;
		}));

		super.init();
	}

	@Override
	public void tick() {

		if (timer > 0) {
			timer--;
		}
		
		continueButton.active = timer <= 0;

		super.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		FontUtils.drawHCenteredString(matrices, "Hi, you entered the " + Formatting.BLUE + "Infinity", width / 2, 35,
				-1);
		FontUtils.drawHCenteredString(matrices,
				"I will only leave here a short information that will be useful to you!", width / 2, 60, -1);
		FontUtils.drawHCenteredString(matrices,
				Formatting.GRAY + "- You can enter the alt manager in the" + Formatting.WHITE + " Multiplayer Screen"
						+ Formatting.GRAY + ", just poke " + Formatting.YELLOW + "Alex" + Formatting.GRAY
						+ " on the head" + Formatting.WHITE + " :D",
				width / 2, 80, -1);
		FontUtils.drawHCenteredString(matrices,
				Formatting.GRAY + "- Gooey in the game opens on" + Formatting.BLUE + " GRAVE" + Formatting.WHITE + " ("
						+ Formatting.BLUE + " ` " + Formatting.WHITE + ")" + Formatting.GRAY + " it is below the"
						+ Formatting.WHITE + " ESC button",
				width / 2, 95, -1);
		FontUtils.drawHCenteredString(matrices,
				Formatting.GRAY + "- Commands are written in chat, you just need to add this" + Formatting.WHITE
						+ " prefix" + Formatting.GRAY + " to the beginning" + Formatting.WHITE + " ( "
						+ Formatting.YELLOW + Command.prefix + Formatting.WHITE + " )",
				width / 2, 110, -1);

		FontUtils.drawHCenteredString(matrices,
				Formatting.GRAY + "For example write to chat " + Formatting.WHITE + Command.prefix + "help", width / 2,
				125, -1);

		FontUtils.drawHCenteredString(matrices, Formatting.GRAY + "Perhaps it was useful for you," + Formatting.WHITE
				+ " Happy journey through" + Formatting.BLUE + " Infinity!", width / 2, 150, -1);

		super.render(matrices, mouseX, mouseY, delta);
	}

}
