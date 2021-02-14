package me.infinity.ui.account;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.ui.account.main.Account;
import me.infinity.ui.account.main.AccountManager;
import me.infinity.ui.account.main.AddThread;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class GuiEdit extends Screen {

	private GuiAccountSwitcher prev;
	private ButtonWidget buttonSave;
	private ButtonWidget buttonBack;
	private TextFieldWidget accountName;
	private TextFieldWidget password;
	private Account account;

	public GuiEdit(GuiAccountSwitcher prev, Account account) {
		super(new LiteralText(""));
		this.prev = prev;
		this.account = account;
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.accountName = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 66, 200, 20,
				new TranslatableText("Edit Login"));
		this.accountName.setSelected(true);
		this.accountName.setMaxLength(30);
		this.accountName.setText(account.getUsername());
		this.children.add(accountName);
		this.password = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 106, 200, 20,
				new TranslatableText("Edit Password"));
		this.password.setSelected(true);
		this.password.setMaxLength(128);
		this.password.setText(account.getPassword());
		this.children.add(password);

		this.buttonSave = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96,
				200, 20, new TranslatableText("Save"), (buttonWidget) -> {
					if (!accountName.getText().isEmpty()) {
						AccountManager.registry.remove(account);
						AddThread addThread = new AddThread(this.accountName.getText(), this.password.getText());
						addThread.start();
					}
					Helper.minecraftClient.openScreen(prev);
					prev.refresh();
				}));

		this.buttonBack = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 100,
				this.height / 4 + 96 + 28, 200, 20, new TranslatableText("Cancel"), (buttonWidget) -> {
					Helper.minecraftClient.openScreen(prev);
					prev.refresh();
				}));

	}

	@Override
	public void tick() {
		boolean notEmpty = !accountName.getText().isEmpty();
		buttonSave.active = notEmpty;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawStringWithShadow(matrices, this.textRenderer, "Edit Login", this.width / 2 - 100, 53, 10526880);
		drawStringWithShadow(matrices, this.textRenderer, "Edit Password", this.width / 2 - 100, 94, 10526880);
		this.accountName.render(matrices, mouseX, mouseY, delta);
		this.password.render(matrices, mouseX, mouseY, delta);
		FontUtils.drawHVCenteredString(matrices, "Edit Account", this.width / 2, 27, -1);
		super.render(matrices, mouseX, mouseY, delta);
	}

}
