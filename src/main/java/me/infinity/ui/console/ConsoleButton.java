package me.infinity.ui.console;

import me.infinity.clickmenu.util.FontUtils;
import net.minecraft.client.util.math.MatrixStack;
	
public class ConsoleButton {

	public void render(MatrixStack matrices, String text, double x, double y) {
		FontUtils.drawStringWithShadow(matrices, text, x, y, -1);
	}
}
