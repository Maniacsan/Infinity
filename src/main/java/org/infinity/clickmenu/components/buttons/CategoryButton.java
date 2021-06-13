package org.infinity.clickmenu.components.buttons;

import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.main.InfMain;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CategoryButton {

	private ArrayList<ModuleButton> moduleButtons = new ArrayList<>();
	private List<Module> modules = new ArrayList<>();
	private List<Module> searchList = new ArrayList<>();
	private Panel panel;
	private String name;
	private boolean open;

	private boolean scrollHover;

	private int offset;
	private int _cbuttonsHeight;

	private double x;
	private double y;
	private double width;
	private double height;

	private double hoverAnim;

	public CategoryButton(String name, List<Module> moduleList, Panel panel) {
		this.name = name;
		this.panel = panel;
		this.modules = moduleList;

		if (moduleList != null) {
			moduleList.forEach(module -> {
				if (module.getCategory() != Category.ENABLED || name != panel.SEARCH)
					moduleButtons.add(new ModuleButton(module, moduleButtons, panel));
			});
		}
		enabledRefresh();
	}

	public void init() {
		moduleButtons.forEach(ModuleButton::init);
	}

	public void addChildren(List<Element> children) {
		moduleButtons.forEach(moduleButton -> moduleButton.addChildren(children));
	}

	public void enabledRefresh() {
		InfMain.getModuleManager().getEnableModules().clear();
		if (name == panel.ENABLED) {
			moduleButtons.clear();
			for (Module module : InfMain.getModuleManager().getEnableModules()) {
				ModuleButton enabledButton = new ModuleButton(module, moduleButtons, panel);
				moduleButtons.add(enabledButton);
			}
			moduleButtons.forEach(moduleButton -> moduleButton.addChildren(panel.clickMenu.getChildren()));
		}
	}

	public void searchRefresh(List<Module> searchList) {
		if (!moduleButtons.isEmpty())
			moduleButtons.clear();
		searchList.clear();
		getResult(searchList);

		if (searchList.isEmpty())
			return;

		for (Module result : searchList) {
			moduleButtons.add(new ModuleButton(result, moduleButtons, panel));
		}
		
		moduleButtons.forEach(moduleButton -> moduleButton.addChildren(panel.clickMenu.getChildren()));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		scrollHover = Render2D.isHovered(mouseX, mouseY, panel.x + 90, panel.y + 37, width + 60, panel.height - 40);

		if (!name.equalsIgnoreCase(panel.SEARCH)) {
			if (isOpen()) {
				Render2D.drawRectWH(matrices, x, y, width, height, 0xFF2E375A);
				Render2D.fillGradient(x, y, x + 2, Math.max(y + height, -1), 0xFF8CEDEB, 0xFF2D4780);
			}

			hoverAnim = Render2D.isHovered(mouseX, mouseY, x, y, width, height) ? Math.min(1.3, hoverAnim + 0.11)
					: Math.max(1, hoverAnim - 0.11);

			// icon
			GL11.glPushMatrix();

			GlStateManager.enableBlend();
			GL11.glTranslated(x + 12.5, y + 10.5, 0);
			GL11.glScaled(hoverAnim, hoverAnim, 1);
			GL11.glTranslated(-x - 12.5, -y - 10.5, 0);
			RenderUtil.drawTexture(matrices,
					new Identifier("infinity", "textures/icons/category/" + this.getName().toLowerCase() + ".png"),
					x + 7, y + 5, 11, 11);

			GlStateManager.disableBlend();
			GL11.glPopMatrix();

			IFont.legacy16.drawString(getName(), (int) x + 25, (int) y + 6, 0xFFDBDBDB, false);
		}

		double yMod = 2;

		panel.clickMenu.startScissor(panel.x + 90, panel.y + 37, width + 60, panel.height - 40);
		if (isOpen()) {

			if (scrollHover && _cbuttonsHeight > panel.height) {
				Render2D.drawRectWH(matrices, panel.x + 237, panel.y + 37, 2, panel.height - 40, 0x90000000);
				Render2D.drawRectWH(matrices, panel.x + 237, panel.y + 37 + offset, 2,
						panel.height - 40 - getHeightDifference(), 0xFF1F5A96);
			}

			for (ModuleButton moduleButton : moduleButtons) {

				_cbuttonsHeight = (int) (panel.y + 37 + yMod);
				moduleButton.setX(panel.x + 94);
				moduleButton.setY(yMod + panel.y + 34 - offset);
				moduleButton.setWidth(140);
				moduleButton.setHeight(25);

				yMod += 28;

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
			if (button != 0 || name.equalsIgnoreCase(panel.SEARCH))
				return;

			panel.searchField.setText("");
			panel.setSearch(false);

			moduleButtons.forEach(ModuleButton::resetAnimation);

			setOpen(!isOpen());

			if (name == panel.ENABLED) {
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

		int scrollOffset = 24;

		if (!isOpen() || !scrollHover || _cbuttonsHeight < panel.height - 40)
			return;

		if (amount < 0.0D) {
			this.offset += scrollOffset;

		} else if (amount > 0.0D) {
			this.offset -= scrollOffset;
		}

		int difference = getHeightDifference();
		if (offset > difference)
			offset = difference;
		else if (offset < 0)
			offset = 0;

	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (isOpen()) {
			moduleButtons.forEach(moduleButton -> moduleButton.mouseReleased(mouseX, mouseY, button));
		}
	}

	public void charTyped(char chr, int keyCode) {

	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (isOpen())
			moduleButtons.forEach(moduleButton -> moduleButton.keyPressed(keyCode, scanCode, modifiers));

		if (name.equalsIgnoreCase(panel.SEARCH) && panel.isOpenSearch()) {
			searchRefresh(searchList);
		}
	}

	public void onClose() {
		if (isOpen()) {
			moduleButtons.forEach(ModuleButton::onClose);
		}
	}

	public int getButtonsHeight() {
		int height = 0;
		for (ModuleButton button : moduleButtons) {
			if (isOpen())
				height += (button.getHeight() + 3);
		}
		return height;
	}

	public int getHeightDifference() {
		double diffHeight = panel.height - 40;
		return (int) (this.getButtonsHeight() - diffHeight);
	}

	private void getResult(List<Module> list) {
		if (!panel.searchField.getText().isEmpty()) {

			for (Module m : InfMain.getModuleManager().getList()) {
				if (m.getCategory().equals(Category.HIDDEN) || m.getCategory().equals(Category.ENABLED))
					continue;

				if (m.getName().toLowerCase().contains(panel.searchField.getText().toLowerCase().replace(" ", "")))
					list.add(m);
			}
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