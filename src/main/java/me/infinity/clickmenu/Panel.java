package me.infinity.clickmenu;

import java.util.ArrayList;

import me.infinity.InfMain;
import me.infinity.clickmenu.features.CategoryButton;
import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Module.Category;
import me.infinity.features.module.visual.GuiMod;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class Panel {

	private static final Identifier AVATAR = new Identifier("infinity", "avatar.png");
	private ArrayList<CategoryButton> catButton = new ArrayList<>();
	private ClickMenu clickMenu;
	private double x;
	private double y;
	private double width;
	private double height;

	private double prevX;
	private double prevY;

	private boolean hovered;
	private boolean closeHovered;

	private boolean dragging;

	public Panel(ClickMenu clickMenu, double x, double y, double width, double height) {
		this.clickMenu = clickMenu;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		for (Category category : Category.values()) {
			catButton.add(new CategoryButton(category.name(), this));
		}
		catButton.add(new CategoryButton("CONFIGS", this));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, this.x, this.y - 11, this.width, 11);
		this.closeHovered = Render2D.isHovered(mouseX, mouseY, this.x + width - 20, this.y - 11, 20, 11);
		if (this.dragging) {
			this.x = this.prevX + mouseX;
			this.y = this.prevY + mouseY;
		}

		// drag borders
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();
		Window window = Helper.minecraftClient.getWindow();
		double scaleWidth = window.getScaledWidth();
		double scaleHeight = window.getScaledHeight();
		scaleWidth /= scale;
		scaleHeight /= scale;
		if (x < 0) {
			x = 0;
		}
		if (x > scaleWidth) {
			x = scaleWidth - x;
		}
		if (y - 11 < 0) {
			y = 11;
		}
		if (y > scaleHeight) {
			y = scaleHeight - y;
		}

		Render2D.drawRectWH(matrices, this.x, this.y, this.width, this.height, ColorUtils.BG);
		Render2D.drawRectWH(matrices, x, y - 11, width, 11, 0xFFDFDFDF);
		Render2D.drawRectWH(matrices, x, y + 2, 66, height - 4, ColorUtils.lineColor);
		Render2D.drawRectWH(matrices, x + 1, y + 3, 64, height - 6, ColorUtils.backNight);

		String dirStr = "C:\\license\\infinity.jar";
		FontUtils.drawString(matrices, dirStr, (int) x + 1, (int) y - 8, 0xFF8E8E8E);
		// close button
		Render2D.drawRectWH(matrices, x + width - 20, y - 11, 19, 10, closeHovered ? 0xFFF31919 : 0xFFBAB7B7);
		FontUtils.drawString(matrices, "x", (int) ((int) x + width - 13), (int) y - 10,
				closeHovered ? 0xFFECEAEA : 0xFF181818);

		Helper.minecraftClient.getTextureManager().bindTexture(AVATAR);
		DrawableHelper.drawTexture(matrices, (int) x + 16, (int) y + 4, 0, 0, 34, 36, 34, 36);
		double yOff = 3;
		for (CategoryButton catButton : catButton) {
			if (catButton.displayModulePanel) {
				if (catButton.getName() == "CONFIGS") {
					Render2D.drawRectWH(matrices, x + 68, y + 2, 79, height - 4, ColorUtils.lineColor);
					Render2D.drawRectWH(matrices, x + 69, y + 3, 77, height - 6, ColorUtils.backNight);
					Render2D.drawRectWH(matrices, x + 150, y + 20, 160, height - 22, ColorUtils.lineColor);
					Render2D.drawRectWH(matrices, x + 151, y + 21, 158, height - 24, 0xFF1B1B1B);
					Render2D.drawRectWH(matrices, x + 312, y + 2, 66, height - 4, ColorUtils.lineColor);
					Render2D.drawRectWH(matrices, x + 313, y + 3, 64, height - 6, ColorUtils.backNight);
				} else {
					Render2D.drawRectWH(matrices, x + 68, y + 2, 151, height - 4, ColorUtils.lineColor);
					Render2D.drawRectWH(matrices, x + 69, y + 3, 149, height - 6, 0xFF1D1C1C);
				}
			}
			catButton.render(matrices, mouseX, mouseY, delta, x + 4, yOff + y + 40, 60, 20, y, x, y, width, height);
			yOff += 23;
		}

	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		catButton.forEach(catButton -> catButton.mouseClicked(mouseX, mouseY, button));
		if (this.hovered) {
			if (button == 0) {
				this.dragging = true;
				this.prevX = this.x - mouseX;
				this.prevY = this.y - mouseY;
			}
		}
		if (closeHovered && button == 0) {
			clickMenu.onClose();
		}
	}

	public void mouseScrolled(double d, double e, double amount) {
		catButton.forEach(catButton -> catButton.mouseScrolled(d, e, amount));
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.dragging = false;
		}
		catButton.forEach(catButton -> catButton.mouseReleased(mouseX, mouseY, button));
	}

	public void charTyped(char chr, int keyCode) {
		catButton.forEach(catButton -> catButton.charTyped(chr, keyCode));
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		catButton.forEach(catButton -> catButton.keyPressed(keyCode, scanCode, modifiers));
	}

	public ArrayList<CategoryButton> getCatButton() {
		return catButton;
	}

}
