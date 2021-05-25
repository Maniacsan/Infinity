package org.infinity.clickmenu.components;

import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.components.buttons.ModuleButton;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.main.InfMain;

import net.minecraft.client.util.math.MatrixStack;

public class Search {

	private ArrayList<ModuleButton> moduleButtons = new ArrayList<>();
	private Panel panel;
	
	private int offset;
	private boolean scrollHover;
	private int _cbuttonsHeight;

	private boolean open;

	public Search(Panel panel) {
		this.panel = panel;
	}

	private void refresh(List<Module> searchList) {
		if (!moduleButtons.isEmpty())
			moduleButtons.clear();
		searchList.clear();
		getResult(searchList);

		if (searchList.isEmpty())
			return;
		
		searchList.forEach(result -> {
			moduleButtons.add(new ModuleButton(result, moduleButtons, panel));
		});
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (isOpen()) {
			
			List<Module> searchList = new ArrayList<>();
			refresh(searchList);

			double yMod = 2;
			Render2D.startMenuScissor(panel.x + 90, panel.y + 37, 150, panel.height - 40);
			for (ModuleButton moduleButton : moduleButtons) {

				moduleButton.setX(panel.x + 96);
				moduleButton.setY(yMod + panel.y + 34);
				moduleButton.setWidth(140);
				moduleButton.setHeight(25);

				yMod += 26;

				moduleButton.render(matrices, mouseX, mouseY, delta);

			}
			Render2D.stopScissor();
		}
	}

	public void tick() {
		if (isOpen()) {
			moduleButtons.forEach(ModuleButton::tick);
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isOpen()) {
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

	public ArrayList<ModuleButton> getModuleButtons() {
		return moduleButtons;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
