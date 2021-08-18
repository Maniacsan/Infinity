package org.infinity.ui.menu.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.infinity.features.Category;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.ClickMenu;
import org.infinity.ui.menu.components.buttons.CategoryButton;
import org.infinity.ui.menu.components.config.ConfigPanel;
import org.infinity.ui.menu.widgets.WSearchField;
import org.infinity.utils.Helper;
import org.infinity.utils.render.ColorUtils;
import org.infinity.utils.render.Render2D;
import org.infinity.utils.render.RenderUtil;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class Panel {

	public ArrayList<CategoryButton> categoryButtons = new ArrayList<>();
	public ConfigPanel configPanel = new ConfigPanel("Config");
	public String SEARCH = "Search";
	public String ENABLED = "Enabled";

	public WSearchField searchField;
	public ClickMenu clickMenu;

	public double x;
	public double y;
	public double width;
	public double height;

	private double prevX;
	private double prevY;

	private boolean hovered;
	private boolean closeHovered;

	private boolean logoAnimate;
	private double _lanim;
	private double _lhover;

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
				new CategoryButton("Combat", InfMain.getModuleManager().getByCategory(Category.COMBAT), this));
		categoryButtons.add(new CategoryButton("Movement",
				InfMain.getModuleManager().getByCategory(Category.MOVEMENT), this));
		categoryButtons.add(
				new CategoryButton("World", InfMain.getModuleManager().getByCategory(Category.WORLD), this));
		categoryButtons.add(
				new CategoryButton("Player", InfMain.getModuleManager().getByCategory(Category.PLAYER), this));
		categoryButtons.add(
				new CategoryButton("Visual", InfMain.getModuleManager().getByCategory(Category.VISUAL), this));
		categoryButtons
				.add(new CategoryButton("Misc", InfMain.getModuleManager().getByCategory(Category.MISC), this));
		categoryButtons.add(
				new CategoryButton(ENABLED, InfMain.getModuleManager().getByCategory(Category.ENABLED), this));
		categoryButtons.add(new CategoryButton(configPanel.getName(), null, this));
		categoryButtons.add(new CategoryButton(SEARCH, null, this));

	}

	public void init() {
		Helper.MC.keyboard.setRepeatEvents(true);
		searchField = new WSearchField(this);

		searchField.setMaxLength(60);
		searchField.setColor(0xFF1D1F30);

		configPanel.init();

		categoryButtons.forEach(CategoryButton::init);
	}

	public void addChildren(List<Element> children) {
		categoryButtons.forEach(categoryButton -> categoryButton.addChildren(children));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, this.x + 92, this.y, (searchField.getX() - 92) - 50, 29);
		this.closeHovered = Render2D.isHovered(mouseX, mouseY, this.x + width - 20, this.y - 11, 20, 11);
		if (this.dragging) {
			this.x = this.prevX + mouseX;
			this.y = this.prevY + mouseY;
		}

		// drag borders
		Window window = Helper.MC.getWindow();
		double scaleWidth = window.getScaledWidth();
		double scaleHeight = window.getScaledHeight();
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
		Render2D.verticalGradient(matrices, this.x + 92, this.y + 1, x + this.width - 2, y + this.height - 2,
				0xFF20202F, 0xFF243670);

		// category panel
		Render2D.drawRectWH(matrices, x + 2, y + 2, 90, height - 4, 0xFF161621);
		IFont.legacy13.drawString(matrices, "v" + InfMain.VERSION, x + 2, y + height - 12, 0xFF464746);

		// header
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 3, width - 92, 28 - 1, 0xFF161621);
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 2, width - 92, 1, 0xFF1F1F1F);
		Render2D.drawRectWH(matrices, this.x + 92, this.y + 2, 0.5, 28, 0xFF4A4F65);
		Render2D.verticalGradient(matrices, this.x + 92, this.y + 28, this.x + width - 1, this.y + 33, 0xFF8EA8E0,
				0xFF2E3349);

		// profile info
		String profileName = InfMain.getUser().getName();
		if (profileName.length() > 18)
			profileName = profileName.substring(0, 18) + "...";

		IFont.legacy16.drawString(matrices, profileName, x + 119, y + 5, -1);
		
		String role = InfMain.getUser().getRole().name();
		if (role.equalsIgnoreCase("YouTube"))
			role = Formatting.RED + "You" + Formatting.BLACK + "Tube";
		IFont.legacy14.drawString(matrices,
				"License: " + ColorUtils.getUserRoleColor() + role, x + 119, y + 17, -1);
		RenderUtil.drawImage(matrices, false, x + 95, y + 5, 20, 20,
				InfMain.getDirection() + File.separator + "profile" + File.separator + "photo.png");

		if (logoAnimate) {
			_lanim += 2;
			if (_lanim > 360)
				_lanim = 0;
		} else
			_lanim = 0;

		_lhover = Render2D.isHovered(mouseX, mouseY, x + 1, y + 1, 90, 62) ? Math.min(1.05, _lhover + 0.03)
				: Math.max(1, _lhover - 0.03);

		// logo
		Render2D.verticalGradient(matrices, x + 1, y + 1, x + 1 + 90, y + 1 + 62, 0xFF0B0D1B, 0xFF161621);

		double lx = (x + 26) + (39 / 2);
		double ly = (y + 6) + (39 / 2);

		matrices.push();

		GlStateManager._enableBlend();
		matrices.translate(lx, ly, 0);
		matrices.scale((float) _lhover, (float) _lhover, 1f);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) _lanim));
		matrices.translate(-lx, -ly, 0);

		RenderUtil.drawTexture(matrices,
				new Identifier("infinity", "textures/game/circle_logo.png"),
				x + 26, y + 6, 39, 39);

		GlStateManager._disableBlend();
		matrices.pop();

		IFont.legacy20.drawString(matrices, InfMain.NAME.toUpperCase(), x + 22, y + 48, -1);
		Render2D.drawRectWH(matrices, x + 6, y + 64.5, 80, 0.5, 0xFF4A4F65);

		searchField.setX((int) (x + width - 115));
		searchField.setY((int) y + 8);
		searchField.setWidth((int) 110);
		searchField.setHeight((int) 16);
		searchField.render(matrices, mouseX, mouseY, delta);

		setSearch(!searchField.getText().isEmpty());
		if (!isOpenSearch())
			searchField.setFocused(false);

		double yOffset = 2;
		for (CategoryButton categoryButton : categoryButtons) {
			if (isSearch()) {
				if (categoryButton.getName().equalsIgnoreCase(SEARCH))
					categoryButton.setOpen(true);
				else
					categoryButton.setOpen(false);
			}

			categoryButton.setX(x + 3);
			categoryButton.setY(yOffset + y + 65);
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
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (closeHovered && button == 0) {
			this.dragging = false;
			clickMenu.onClose();
		}

		if (Render2D.isHovered(mouseX, mouseY, x + 1, y + 1, 90, 62) && button == 0)
			logoAnimate = !logoAnimate;

		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseClicked(mouseX, mouseY, button);
		});

		if (this.hovered && button == 0) {
			this.dragging = true;
			this.prevX = this.x - mouseX;
			this.prevY = this.y - mouseY;
		}
		searchField.mouseClicked(mouseX, mouseY, button);
	}

	public void mouseScrolled(double d, double e, double amount) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseScrolled(d, e, amount);
		});

		configPanel.mouseScrolled(d, e, amount);
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.dragging = false;
		}
		categoryButtons.forEach(categoryButton -> {
			categoryButton.mouseReleased(mouseX, mouseY, button);
		});

	}

	public void charTyped(char chr, int keyCode) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.charTyped(chr, keyCode);
		});
		searchField.charTyped(chr, keyCode);
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.keyPressed(keyCode, scanCode, modifiers);
		});
		searchField.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onClose() {
		categoryButtons.forEach(categoryButton -> {
			categoryButton.onClose();
		});
		configPanel.onClose();
		dragging = false;

		searchField.onClose();
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
