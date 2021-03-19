package me.infinity.ui.account;

import java.util.ArrayList;
import java.util.List;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.ui.account.main.Account;
import me.infinity.ui.account.main.AccountManager;
import me.infinity.ui.account.main.AccountThread;
import me.infinity.ui.util.ListWidget;
import me.infinity.utils.Helper;
import me.infinity.utils.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class GuiAccountSwitcher extends Screen {

	private Screen prev;
	public ListGui listGui;
	private ButtonWidget buttonDelete;
	private ButtonWidget buttonLogin;
	private ButtonWidget buttonEdit;
	private AccountThread accountThread;

	public GuiAccountSwitcher(Screen prev) {
		super(new LiteralText(""));
		this.prev = prev;
		refresh();
	}

	@Override
	public void init() {
		listGui = new ListGui(client, this, getListAccount());
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20,
				new TranslatableText("Add"), (buttonWidget) -> {
					Helper.minecraftClient.openScreen(new GuiAddAccount(this));
					refresh();
				}));
		buttonDelete = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20,
				new TranslatableText("Delete"), (buttonWidget) -> {
					Account selectAccount = listGui.getSelectedAlt();
					if (accountThread != null)
						accountThread = null;
					AccountManager.registry.remove(selectAccount);
					InfMain.getAccountManager().save();
					refresh();
				}));
		// this.width / 2 + 4 + 50, this.height - 52, 100, 20,
		buttonLogin = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20,
				new TranslatableText("Login"), (buttonWidget) -> {
					Account acc = listGui.getSelectedAlt();
					if (acc == null) {
						// System.out.print("GDE MOY ACC!");
						return;
					}
					(accountThread = new AccountThread(acc)).start();
					refresh();
				}));

		buttonEdit = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20,
				new TranslatableText("Edit"), (buttonWidget) -> {
					Account selectAccount = listGui.getSelectedAlt();
					if (selectAccount != null)
						Helper.minecraftClient.openScreen(new GuiEdit(this, selectAccount));
					refresh();
				}));
		this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20,
				new TranslatableText("Direct"), (buttonWidget) -> {
					Helper.minecraftClient.openScreen(new GuiDirect(this));
					refresh();
				}));
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20,
				new TranslatableText("Refresh"), (buttonWidget) -> {
					refresh();
				}));
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20,
				new TranslatableText("Back"), (buttonWidget) -> {
					Helper.minecraftClient.openScreen(prev);
				}));
	}

	public void refresh() {
		listGui = new ListGui(client, this, getListAccount());
	}

	@Override
	public void tick() {
		boolean altSelected = listGui.selected >= 0 && listGui.selected < getListAccount().size();

		buttonLogin.active = altSelected;
		buttonEdit.active = altSelected;
		buttonDelete.active = altSelected;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		listGui.render(matrixStack, mouseX, mouseY, partialTicks);
		FontUtils.drawHVCenteredString(matrixStack, "Account Manager", this.width / 2, 27, -1);
		FontUtils.drawStringWithShadow(matrixStack,
				"Logged in as : " + Formatting.GRAY + Helper.minecraftClient.getSession().getUsername(), 2, 5,
				0xFFFFFFFF);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	private List<Account> getListAccount() {
		List<Account> altList = new ArrayList<>();
		for (final Account alt : AccountManager.registry) {
			altList.add(alt);
		}
		return altList;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		listGui.mouseClicked(mouseX, mouseY, mouseButton);

		if (mouseY >= 36 && mouseY <= height - 57)
			if (mouseX >= width / 2 + 140 || mouseX <= width / 2 - 126)
				listGui.selected = -1;

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		listGui.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		listGui.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		listGui.mouseScrolled(d, e, amount);
		return super.mouseScrolled(d, e, amount);
	}

	public static final class ListGui extends ListWidget {
		private final List<Account> list;
		private int selected = -1;

		public ListGui(MinecraftClient minecraft, GuiAccountSwitcher prevScreen, List<Account> list) {
			super(minecraft, prevScreen.width, prevScreen.height, 36, prevScreen.height - 56, 30);

			this.list = list;
		}

		@Override
		protected boolean isSelectedItem(int id) {
			return selected == id;
		}

		protected int getSelectedSlot() {
			return selected;
		}

		protected Account getSelectedAlt() {
			if (selected < 0 || selected >= list.size())
				return null;

			return list.get(selected);
		}

		@Override
		protected int getItemCount() {
			return list.size();
		}

		@Override
		protected boolean selectItem(int index, int button, double mouseX, double mouseY) {
			if (index >= 0 && index < list.size())
				selected = index;

			return true;
		}

		@Override
		protected void renderBackground() {

		}

		@Override
		protected void renderItem(MatrixStack matrixStack, int id, int x, int y, int var4, int var5, int var6,
				float partialTicks) {
			Account alt = list.get(id);

			if (client.getSession().getUsername().equals(alt.getUsername())
					|| client.getSession().getUsername().equals(alt.getMask())) {
				Render2D.drawRectWH(matrixStack, x, y - 1, 217, 26, 0x70000000);
				Render2D.drawRectWH(matrixStack, x - 2, y - 2, 220, 1, 0xFF56DA38);
				Render2D.drawRectWH(matrixStack, x - 2, y + 27, 220, 1, 0xFF56DA38);
				Render2D.drawRectWH(matrixStack, x - 2, y - 2, 1, 30, 0xFF56DA38);
				Render2D.drawRectWH(matrixStack, x + 217, y - 2, 1, 30, 0xFF56DA38);
			}

			String name = "";
			if (alt.getMask().equalsIgnoreCase("")) {
				name = alt.getUsername();
			} else {
				name = alt.getMask();
			}

			RenderUtil.drawFace(matrixStack, alt.getUsername(), x + 1, y + 1, 25, 25, isSelectedItem(id));

			String status = alt.isMojang() ? Formatting.GREEN + "Mojang"
					: alt.isNickname() ? Formatting.GRAY + "Nickname" : Formatting.RED + "Failed";

			client.textRenderer.draw(matrixStack, name, x + 31, y + 3, -1);
			client.textRenderer.draw(matrixStack, status, x + 31, y + 15, 10526880);
		}
	}

}
