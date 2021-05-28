package org.infinity.clickmenu.components;

import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.ClickMenu;
import org.infinity.clickmenu.components.buttons.CategoryButton;
import org.infinity.clickmenu.components.config.ConfigPanel;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WSearchField;
import org.infinity.features.Category;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class Panel {

	public ArrayList<CategoryButton> categoryButtons = new ArrayList<>();
	public ConfigPanel configPanel = new ConfigPanel("Config");
	private Search searchPanel;

	public WSearchField searchField;
	private ClickMenu clickMenu;

	public double x;
	public double y;
	public double width;
	public double height;

	private double prevX;
	private double prevY;

	private boolean hovered;
	private boolean closeHovered;

	private boolean search;

	// Created here so that when it closes it remembers its state
	private boolean openSearch;

	private boolean dragging;

	public Panel(ClickMenu clickMenu, double x, double y, double width, double height) {
		this.clickMenu = clickMenu;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		categoryButtons.add(
				new CategoryButton("Combat", InfMain.getModuleManager().getModulesByCategory(Category.COMBAT), this));
		categoryButtons.add(new CategoryButton("Movement",
				InfMain.getModuleManager().getModulesByCategory(Category.MOVEMENT), this));
		categoryButtons.add(
				new CategoryButton("World", InfMain.getModuleManager().getModulesByCategory(Category.WORLD), this));
		categoryButtons.add(
				new CategoryButton("Player", InfMain.getModuleManager().getModulesByCategory(Category.PLAYER), this));
		categoryButtons.add(
				new CategoryButton("Visual", InfMain.getModuleManager().getModulesByCategory(Category.VISUAL), this));
		categoryButtons.add(
				new CategoryButton("Enabled", InfMain.getModuleManager().getModulesByCategory(Category.ENABLED), this));

		categoryButtons.add(new CategoryButton(configPanel.getName(), null, this));

		searchPanel = new Search(this);
	}

	public void init() {
		Helper.minecraftClient.keyboard.setRepeatEvents(true);
		searchField = new WSearchField(Helper.minecraftClient.textRenderer, this, (int) this.width / 2 - 80,
				(int) this.height / 2 - 58, 160, 22, new TranslatableText("Search"), false);

		searchField.setMaxLength(40);
		searchField.setColor(0xFF1D1F30);

		configPanel.init();

		categoryButtons.forEach(CategoryButton::init);
	}

	public void addChildren(List<Element> children) {
		children.add(searchField);
		children.add(configPanel.textField);
		
		categoryButtons.forEach(categoryButton -> categoryButton.addChildren(children));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, this.x + 92, this.y, this.width - 92, 29);
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
		if (y < 0) {
			y = 0;
		}
		if (y > scaleHeight) {
			y = scaleHeight - y;
		}

		// panel
		Render2D.drawRectWH(matrices, x, y + 1, width, height - 1, 0xFF181818);
		Render2D.fillGradient(this.x + 92, this.y + 1, x + this.width - 2, y + this.height - 2, 0xFF20202F, 0xFF243670);

		// category panel
		Render2D.drawRectWH(matrices, x + 2, y + 2, 90, height - 4, 0xFF161621);

		// profile info
		Render2D.drawRectWH(matrices, this.x + 2, this.y + 66.5, 90, 0.5, 0xFF4A4F65);
		FontUtils.drawHCenteredString(matrices, InfMain.getUser().getName(), x + 94 / 2, y + 50, -1);

		// header
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 3, width - 92, 28 - 1, 0xFF161621);
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 2, width - 92, 1, 0xFF1F1F1F);
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 2, 0.5, 28, 0xFF4A4F65);

		Render2D.fillGradient(this.x + 92, this.y + 28, this.x + width - 1, this.y + 33, 0xFF8EA8E0, 0xFF2E3349);

		searchField.setX((int) (x + width - 115));
		searchField.setY((int) y + 8);
		searchField.setWidth((int) 110);
		searchField.setHeight((int) 16);
		searchField.render(matrices, mouseX, mouseY, delta);

		setSearch(!searchField.getText().isEmpty());

		searchPanel.setOpen(isSearch());

		searchPanel.render(matrices, mouseX, mouseY, delta);

		double yOffset = 2;
		for (CategoryButton categoryButton : categoryButtons) {
			if (isSearch()) {
				categoryButton.setOpen(false);
			}

			categoryButton.setX(x + 3);
			categoryButton.setY(yOffset + y + 66);
			categoryButton.setWidth(90);
			categoryButton.setHeight(20);

			yOffset += 21;

			categoryButton.render(matrices, mouseX, mouseY, delta);

			if (categoryButton.isOpen() && !isSearch()
					&& categoryButton.getName().equalsIgnoreCase(configPanel.getName())) {
				configPanel.setX(x + 92);
				configPanel.setY(y + 34);
				configPanel.setWidth(width - 92);
				configPanel.setHeight(height);
				configPanel.render(matrices, mouseX, mouseY, delta);
			}
		}
	}

	public void tick() {
		categoryButtons.forEach(CategoryButton::tick);
		configPanel.tick();
		searchPanel.tick();
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (closeHovered && button == 0) {
			this.dragging = false;
			clickMenu.onClose();
		}

		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseClicked(mouseX, mouseY, button);
		});

		searchPanel.mouseClicked(mouseX, mouseY, button);

		if (this.hovered) {
			if (button == 0) {
				this.dragging = true;
				this.prevX = this.x - mouseX;
				this.prevY = this.y - mouseY;
			}
		}

	}

	public void mouseScrolled(double d, double e, double amount) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseScrolled(d, e, amount);
		});

		configPanel.mouseScrolled(d, e, amount);
		searchPanel.mouseScrolled(d, e, amount);
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.dragging = false;
		}
		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseReleased(mouseX, mouseY, button);
		});
		searchPanel.mouseScrolled(mouseX, mouseY, button);

	}

	public void charTyped(char chr, int keyCode) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.charTyped(chr, keyCode);
		});
		searchPanel.charTyped(chr, keyCode);
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.keyPressed(keyCode, scanCode, modifiers);
		});
		searchPanel.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onClose() {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.onClose();
		});
		searchPanel.onClose();
		configPanel.onClose();
	}

	public ArrayList<CategoryButton> getCategoryButtons() {
		return categoryButtons;
	}

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public boolean isOpenSearch() {
		return openSearch;
	}

	public void setOpenSearch(boolean openSearch) {
		this.openSearch = openSearch;
	}
}
