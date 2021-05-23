package org.infinity.clickmenu.components.buttons;

import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.main.InfMain;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CategoryButton {

	private ArrayList<ModuleButton> moduleButtons = new ArrayList<>();
	private List<Module> modules = new ArrayList<>();
	private Panel panel;
	private String name;
	private boolean open;

	private double x;
	private double y;
	private double width;
	private double height;

	public CategoryButton(String name, List<Module> moduleList, Panel panel) {
		this.name = name;
		this.panel = panel;
		this.modules = moduleList;

		if (moduleList != null) {
			moduleList.forEach(module -> {
				if (module.getCategory() != Category.ENABLED)
					moduleButtons.add(new ModuleButton(module, moduleButtons, panel));
			});
		}

		enabledRefresh();
	}

	public void enabledRefresh() {
		InfMain.getModuleManager().getEnableModules().clear();
		if (name == Category.ENABLED.name) {
			moduleButtons.clear();
			InfMain.getModuleManager().getEnableModules().forEach(module -> {
				ModuleButton enabledButton = new ModuleButton(module, moduleButtons, panel);
				moduleButtons.add(enabledButton);
			});
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (isOpen()) {
			Render2D.drawRectWH(matrices, x, y, width, height, 0xFF2E375A);
			Render2D.fillGradient(x, y, x + 2, y + height, 0xFF8CEDEB, 0xFF2D4780);
		}

		// icon
		String name = "";

		switch (this.getName()) {
		case "Combat":
			name = "combat";
			break;
		case "Movement":
			name = "movement";
			break;
		case "World":
			name = "world";
			break;
		case "Player":
			name = "player";
			break;
		case "Visual":
			name = "visual";
			break;
		case "Enabled":
			name = "enabled";
			break;
		case "Config":
			name = "settings";
			break;
		}
		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/category/" + name + ".png"), x + 6,
				y + 4, 13, 13);

		FontUtils.drawString(matrices, getName(), (int) x + 25, (int) y + 7, 0xFFDBDBDB);

		double yMod = 2;

		if (panel.isSearch())
			return;

		Render2D.startMenuScissor(panel.x + 65, panel.y + 5, width + 113, panel.height - 8);
		if (isOpen()) {
			for (ModuleButton moduleButton : moduleButtons) {

				moduleButton.setX(panel.x + 96);
				moduleButton.setY(yMod + panel.y + 34);
				moduleButton.setWidth(140);
				moduleButton.setHeight(25);

				yMod += 26;

				moduleButton.render(matrices, mouseX, mouseY, delta);
			}
		}
		Render2D.stopScissor();
	}

	public void tick() {
		if (isOpen())
			moduleButtons.forEach(ModuleButton::tick);
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x, y, width, height)) {
			if (button != 0)
				return;

			panel.searchField.setText("");
			panel.setSearch(false);

			setOpen(!isOpen());

			if (name == Category.ENABLED.name) {
				enabledRefresh();
			}

			panel.getCategoryButtons().forEach(categoryButton -> {
				if (!categoryButton.name.equalsIgnoreCase(name))
					categoryButton.open = false;
			});
		}

		if (isOpen()) {
			if (getName().equalsIgnoreCase(panel.configPanel.getName()))
				panel.configPanel.mouseClicked(mouseX, mouseY, button);
			else
				moduleButtons.forEach(moduleButton -> moduleButton.mouseClicked(mouseX, mouseY, button));
		}
	}

	public void mouseScrolled(double d, double e, double amount) {
		if (isOpen()) {
			moduleButtons.forEach(moduleButton -> moduleButton.mouseScrolled(d, e, amount));
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (isOpen()) {
			moduleButtons.forEach(moduleButton -> moduleButton.mouseReleased(mouseX, mouseY, button));
		}
	}

	public void charTyped(char chr, int keyCode) {

	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
	}

	public void onClose() {
		if (isOpen()) {
			moduleButtons.forEach(ModuleButton::onClose);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public List<Module> getModules() {
		return modules;
	}

	public ArrayList<ModuleButton> getModuleButtons() {
		return moduleButtons;
	}

}