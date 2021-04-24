package org.infinity.ui.util;

import org.infinity.clickmenu.util.Render2D;
import org.infinity.event.protect.ButtonPressEvent;

import com.darkmagician6.eventapi.EventManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CustomButtonWidget extends AbstractPressableButtonWidget {
	public static final CustomButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {
	};
	protected final CustomButtonWidget.PressAction onPress;
	protected final CustomButtonWidget.TooltipSupplier tooltipSupplier;

	public CustomButtonWidget(int x, int y, int width, int height, Text message,
			CustomButtonWidget.PressAction onPress) {
		this(x, y, width, height, message, onPress, EMPTY);
	}

	public CustomButtonWidget(int x, int y, int width, int height, Text message, CustomButtonWidget.PressAction onPress,
			CustomButtonWidget.TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, message);
		this.onPress = onPress;
		this.tooltipSupplier = tooltipSupplier;
	}

	public void onPress() {
		this.onPress.onPress(this);
	}

	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		Render2D.drawRectWH(matrices, x, y, width, height, this.active && this.isHovered() ? 0xFF85C0BA :
				this.active ? 0xFF95D3CD
								: !this.active && this.isHovered() ? 0xFF6F737B : 0xFF50545D);
		int j = this.active ? 16777215 : 10526880;
		drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2,
				this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);

		if (this.isHovered()) {
			this.renderToolTip(matrices, mouseX, mouseY);
		}

	}

	public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
		this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
	}

	@Environment(EnvType.CLIENT)
	public interface TooltipSupplier {
		void onTooltip(CustomButtonWidget button, MatrixStack matrices, int mouseX, int mouseY);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(CustomButtonWidget button);
	}
}
