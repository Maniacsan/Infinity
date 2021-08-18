package org.infinity.ui;

import org.infinity.utils.ConnectUtil;
import org.infinity.utils.render.FontUtils;
import org.infinity.utils.render.Render2D;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class UpdateUI extends Screen {

	private int location;
	private int minX, maxX;

	public UpdateUI() {
		super(new LiteralText(""));
	}
	
	@Override
	public void init() {
		ConnectUtil.downloadClient();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Render2D.drawRectWH(matrices, 0, 0, width, height, 0xFF212D59);

		Render2D.drawRectWH(matrices, width / 2 - 166, height / 2 - 112, 332, 184, 0xFF202020);
		Render2D.drawRectWH(matrices, width / 2 - 165, height / 2 - 110, 330, 180, 0xFFD4DADA);

		Render2D.drawRectWH(matrices, width / 2 - 167 + 30, height / 2 - 2, 332 - 58, 24, 0xFF77E5D6);
		Render2D.drawRectWH(matrices, width / 2 - 165 + 30, height / 2, 330 - 60, 20, 0xFF1E1E1E);

		minX = width / 2 - 165 + 25;
		maxX = width / 2 + 105;

		if (location <= maxX - minX)
			location += 2;
		else if (location >= maxX - minX)
			location = 0;

		Render2D.drawRectWH(matrices, minX + location, height / 2, 25, 20, 0xFF77E5D6);

		FontUtils.drawHCenteredString(matrices, "Client Updating ", width / 2, height / 2 - 96, -1);

		FontUtils.drawHCenteredString(matrices, "Please wait while we update your client ", width / 2, height / 2 - 70,
				0xFF787878);
		FontUtils.drawHCenteredString(matrices, "It won't take long", width / 2, height / 2 - 58, 0xFF787878);
		FontUtils.drawHCenteredString(matrices, "After the update, the game will be released by itself", width / 2,
				height / 2 - 38, 0xFF787878);
		FontUtils.drawHCenteredString(matrices, "and you can enter with the update ", width / 2, height / 2 - 26,
				0xFF787878);

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void onClose() {

	}
}
