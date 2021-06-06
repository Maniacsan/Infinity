package org.infinity.clickmenu.widgets;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.infinity.utils.Helper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WTextField extends AbstractButtonWidget implements Drawable, Element {
	private final TextRenderer textRenderer;
	private String text;
	private int maxLength;
	private int focusedTicks;
	private boolean obfText;
	private boolean focused;
	private boolean focusUnlocked;
	private boolean editable;
	private boolean selecting;
	private int firstCharacterIndex;
	private int selectionStart;
	private int selectionEnd;
	private int editableColor;
	private int uneditableColor;
	private String suggestion;
	private Consumer<String> changedListener;
	private Predicate<String> textPredicate;
	private BiFunction<String, Integer, OrderedText> renderTextProvider;

	private int color;
	private int lineColor;

	public WTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text, boolean obfText) {
		this(textRenderer, x, y, width, height, (TextFieldWidget) null, text, obfText);
	}

	public WTextField(TextRenderer textRenderer, int x, int y, int width, int height,
			@Nullable TextFieldWidget copyFrom, Text text, boolean obfText) {
		super(x, y, width, height, text);
		this.text = "";
		this.maxLength = 32;
		this.focused = true;
		this.focusUnlocked = true;
		this.editable = true;
		this.editableColor = 14737632;
		this.uneditableColor = 7368816;
		this.textPredicate = Objects::nonNull;
		this.obfText = obfText;
		this.renderTextProvider = (string, integer) -> {
			return OrderedText.styledString(string, Style.EMPTY);
		};
		this.textRenderer = textRenderer;
		if (copyFrom != null) {
			this.setText(copyFrom.getText());
		}
		this.color = 0xFFA5A6A6;
		this.setLineColor(0xFF0D0D1C);
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	public void setRenderTextProvider(BiFunction<String, Integer, OrderedText> renderTextProvider) {
		this.renderTextProvider = renderTextProvider;
	}

	public void tick() {
		++this.focusedTicks;
	}

	protected MutableText getNarrationMessage() {
		Text text = this.getMessage();
		return new TranslatableText("gui.narrate.editBox", new Object[] { text, this.text });
	}

	public void setText(String text) {
		if (this.textPredicate.test(text)) {
			if (text.length() > this.maxLength) {
				this.text = text.substring(0, this.maxLength);
			} else {
				this.text = text;
			}

			this.setCursorToEnd();
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(text);
		}
	}

	public String getText() {
		return this.text;
	}

	public String getSelectedText() {
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		return this.text.substring(i, j);
	}

	public void setTextPredicate(Predicate<String> textPredicate) {
		this.textPredicate = textPredicate;
	}

	public void write(String string) {
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		int k = this.maxLength - this.text.length() - (i - j);
		String string2 = SharedConstants.stripInvalidChars(string);
		int l = string2.length();
		if (k < l) {
			string2 = string2.substring(0, k);
			l = k;
		}

		String string3 = (new StringBuilder(this.text)).replace(i, j, string2).toString();
		if (this.textPredicate.test(string3)) {
			this.text = string3;
			this.setSelectionStart(i + l);
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(this.text);
		}
	}

	private void onChanged(String newText) {
		if (this.changedListener != null) {
			this.changedListener.accept(newText);
		}

		this.nextNarration = Util.getMeasuringTimeMs() + 500L;
	}

	private void erase(int offset) {
		if (Screen.hasControlDown()) {
			this.eraseWords(offset);
		} else {
			this.eraseCharacters(offset);
		}

	}

	public void eraseWords(int wordOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
			}
		}
	}

	public void eraseCharacters(int characterOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				int i = this.method_27537(characterOffset);
				int j = Math.min(i, this.selectionStart);
				int k = Math.max(i, this.selectionStart);
				if (j != k) {
					String string = (new StringBuilder(this.text)).delete(j, k).toString();
					if (this.textPredicate.test(string)) {
						this.text = string;
						this.setCursor(j);
					}
				}
			}
		}
	}

	public int getWordSkipPosition(int wordOffset) {
		return this.getWordSkipPosition(wordOffset, this.getCursor());
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition) {
		return this.getWordSkipPosition(wordOffset, cursorPosition, true);
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
		int i = cursorPosition;
		boolean bl = wordOffset < 0;
		int j = Math.abs(wordOffset);

		for (int k = 0; k < j; ++k) {
			if (!bl) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);
				if (i == -1) {
					i = l;
				} else {
					while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
						++i;
					}
				}
			} else {
				while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
					--i;
				}

				while (i > 0 && this.text.charAt(i - 1) != ' ') {
					--i;
				}
			}
		}

		return i;
	}

	public void moveCursor(int offset) {
		this.setCursor(this.method_27537(offset));
	}

	private int method_27537(int i) {
		return Util.moveCursor(this.text, this.selectionStart, i);
	}

	public void setCursor(int cursor) {
		this.setSelectionStart(cursor);
		if (!this.selecting) {
			this.setSelectionEnd(this.selectionStart);
		}

		this.onChanged(this.text);
	}

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void setCursorToStart() {
		this.setCursor(0);
	}

	public void setCursorToEnd() {
		this.setCursor(this.text.length());
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.isActive()) {
			return false;
		} else {
			this.selecting = Screen.hasShiftDown();
			if (Screen.isSelectAll(keyCode)) {
				this.setCursorToEnd();
				this.setSelectionEnd(0);
				return true;
			} else if (Screen.isCopy(keyCode)) {
				Helper.minecraftClient.keyboard.setClipboard(this.getSelectedText());
				return true;
			} else if (Screen.isPaste(keyCode)) {
				if (this.editable) {
					this.write(Helper.minecraftClient.keyboard.getClipboard());
				}

				return true;
			} else if (Screen.isCut(keyCode)) {
				Helper.minecraftClient.keyboard.setClipboard(this.getSelectedText());
				if (this.editable) {
					this.write("");
				}
				return true;
			} else {
				switch (keyCode) {
				case GLFW.GLFW_KEY_ENTER:
					return true;
				case 259:
					if (this.editable) {
						this.selecting = false;
						this.erase(-1);
						this.selecting = Screen.hasShiftDown();
					}

					return true;
				case 260:
				case 264:
				case 265:
				case 266:
				case 267:
				default:
					return false;
				case 261:
					if (this.editable) {
						this.selecting = false;
						this.erase(1);
						this.selecting = Screen.hasShiftDown();
					}

					return true;
				case 262:
					if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(1));
					} else {
						this.moveCursor(1);
					}

					return true;
				case 263:
					if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(-1));
					} else {
						this.moveCursor(-1);
					}

					return true;
				case 268:
					this.setCursorToStart();
					return true;
				case 269:
					this.setCursorToEnd();
					return true;
				}
			}
		}
	}

	public boolean isActive() {
		return this.isVisible() && this.isFocused() && this.isEditable();
	}

	public boolean charTyped(char chr, int keyCode) {
		if (!this.isActive()) {
			return false;
		} else if (SharedConstants.isValidChar(chr)) {
			if (this.editable) {
				this.write(Character.toString(chr));
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.isVisible()) {
			return false;
		} else {
			boolean bl = mouseX >= (double) this.x && mouseX < (double) (this.x + this.width)
					&& mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
			if (this.focusUnlocked) {
				this.setSelected(bl);
			}

			if (this.isFocused() && bl && button == 0) {
				int i = MathHelper.floor(mouseX) - this.x;
				if (this.focused) {
					i -= 4;
				}

				String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex),
						this.getInnerWidth());
				this.setCursor(this.textRenderer.trimToWidth(string, i).length() + this.firstCharacterIndex);
				return true;
			} else {
				return false;
			}
		}
	}

	public void setSelected(boolean selected) {
		super.setFocused(selected);
	}

	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			int j;
			if (this.hasBorder()) {
				j = this.isFocused() ? -1 : 0xFF8F8E8E;
				fill(matrices, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, lineColor);
				fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, color);
			}

			j = this.editable ? this.editableColor : this.uneditableColor;
			int k = this.selectionStart - this.firstCharacterIndex;
			int l = this.selectionEnd - this.firstCharacterIndex;
			String obfText = this.text.replaceAll("(?s).", "*");
			String customText = this.obfText ? obfText : this.text;
			String string = this.textRenderer.trimToWidth(customText.substring(this.firstCharacterIndex),
					this.getInnerWidth());
			boolean bl = k >= 0 && k <= string.length();
			boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
			int m = this.focused ? this.x + 4 : this.x;
			int n = this.focused ? this.y + (this.height - 8) / 2 : this.y;
			int o = m;
			if (l > string.length()) {
				l = string.length();
			}

			int yAdd = this.obfText ? 2 : 0;
			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, k) : string;
				o = this.textRenderer.draw(matrices,
						(OrderedText) this.renderTextProvider.apply(string2, this.firstCharacterIndex), (float) m,
						(float) n + yAdd, j);
			}

			boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
			int p = o;
			if (!bl) {
				p = k > 0 ? m + this.width : m;
			} else if (bl3) {
				p = o - 1;
				--o;
			}

			if (!string.isEmpty() && bl && k < string.length()) {
				this.textRenderer.draw(matrices,
						(OrderedText) this.renderTextProvider.apply(string.substring(k), this.selectionStart),
						(float) o, (float) n, j);
			}

			if (!bl3 && this.suggestion != null) {
				this.textRenderer.draw(matrices, this.suggestion, (float) (p - 1), (float) n, j);
			}

			int var10002;
			int var10003;
			int var10004;
			if (bl2) {
				if (bl3) {
					var10002 = n - 1;
					var10003 = p + 1;
					var10004 = n + 1;
					this.textRenderer.getClass();
					DrawableHelper.fill(matrices, p, var10002, var10003, var10004 + 9, -3092272);
				} else {
					this.textRenderer.draw(matrices, "_", (float) p, (float) n, j);
				}
			}

			if (l != k) {
				int q = m + textRenderer.getWidth(string.substring(0, l));
				var10002 = n - 1;
				var10003 = q - 1;
				var10004 = n + 1;
				this.textRenderer.getClass();
				this.drawSelectionHighlight(p, var10002, var10003, var10004 + 9);
			}

		}
	}

	private void drawSelectionHighlight(int x1, int y1, int x2, int y2) {
		int j;
		if (x1 < x2) {
			j = x1;
			x1 = x2;
			x2 = j;
		}

		if (y1 < y2) {
			j = y1;
			y1 = y2;
			y2 = j;
		}

		if (x2 > this.x + this.width) {
			x2 = this.x + this.width;
		}

		if (x1 > this.x + this.width) {
			x1 = this.x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double) x1, (double) y2, 0.0D).next();
		bufferBuilder.vertex((double) x2, (double) y2, 0.0D).next();
		bufferBuilder.vertex((double) x2, (double) y1, 0.0D).next();
		bufferBuilder.vertex((double) x1, (double) y1, 0.0D).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if (this.text.length() > maxLength) {
			this.text = this.text.substring(0, maxLength);
			this.onChanged(this.text);
		}

	}

	private int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.selectionStart;
	}

	public boolean hasBorder() {
		return this.focused;
	}

	public void setHasBorder(boolean hasBorder) {
		this.focused = hasBorder;
	}

	public void setEditableColor(int color) {
		this.editableColor = color;
	}

	public void setUneditableColor(int color) {
		this.uneditableColor = color;
	}

	public boolean changeFocus(boolean lookForwards) {
		return this.visible && this.editable ? super.changeFocus(lookForwards) : false;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.visible && mouseX >= (double) this.x && mouseX < (double) (this.x + this.width)
				&& mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
	}

	protected void onFocusedChanged(boolean bl) {
		if (bl) {
			this.focusedTicks = 0;
		}

	}

	private boolean isEditable() {
		return this.editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getInnerWidth() {
		return this.hasBorder() ? this.width - 8 : this.width;
	}

	public void setSelectionEnd(int i) {
		int j = this.text.length();
		this.selectionEnd = MathHelper.clamp(i, 0, j);
		if (this.textRenderer != null) {
			if (this.firstCharacterIndex > j) {
				this.firstCharacterIndex = j;
			}

			int k = this.getInnerWidth();
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), k);
			int l = string.length() + this.firstCharacterIndex;
			if (this.selectionEnd == this.firstCharacterIndex) {
				this.firstCharacterIndex -= this.textRenderer.trimToWidth(this.text, k, true).length();
			}

			if (this.selectionEnd > l) {
				this.firstCharacterIndex += this.selectionEnd - l;
			} else if (this.selectionEnd <= this.firstCharacterIndex) {
				this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
			}

			this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, j);
		}
	}

	public void setFocusUnlocked(boolean focusUnlocked) {
		this.focusUnlocked = focusUnlocked;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion;
	}

	public int getCharacterX(int index) {
		return index > this.text.length() ? this.x : this.x + this.textRenderer.getWidth(this.text.substring(0, index));
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

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}
}