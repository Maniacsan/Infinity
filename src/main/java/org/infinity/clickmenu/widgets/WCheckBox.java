package org.infinity.clickmenu.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WCheckBox extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier("textures/gui/checkbox.png");
	private boolean checked;
	private final boolean field_24253;
	private int color;

	public WCheckBox(int x, int y, int width, int height, Text text, boolean checked) {
		this(x, y, width, height, text, checked, true);
	}

	public WCheckBox(int i, int j, int k, int l, Text text, boolean bl, boolean bl2) {
		super(i, j, k, l, text);
		this.checked = bl;
		this.field_24253 = bl2;
	}

	public void onPress() {
		this.checked = !this.checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		drawTexture(matrices, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.checked ? 20.0F : 0.0F, 20,
				this.height, 64, 64);
		this.renderBg(matrices, minecraftClient, mouseX, mouseY);
		if (this.field_24253) {
			drawTextWithShadow(matrices, textRenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2,
					14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
		}

	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
}