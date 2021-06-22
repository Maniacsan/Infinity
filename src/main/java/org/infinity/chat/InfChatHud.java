package org.infinity.chat;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class InfChatHud extends DrawableHelper {
	private final MinecraftClient client;
	private final List<String> messageHistory = Lists.newArrayList();
	private final List<ChatHudLine<Text>> messages = Lists.newArrayList();
	private final Deque<Text> messageQueue = Queues.newArrayDeque();
	private final List<ChatHudLine<OrderedText>> visibleMessages = Lists.newArrayList();
	private int scrolledLines;
	private int iscrolledLines;
	private boolean hasUnreadNewMessages;
	private long lastMessageAddedTime = 0L;

	public List<Chat> chats = new ArrayList<>();

	public Chat currentChat = null;

	public Chat mcChat = new Chat("Minecraft");

	public Chat infChat = new Chat("Infinity IRC");

	private int topY;
	private int tabWidth;

	public InfChatHud(MinecraftClient client) {
		this.client = client;
		chats.add(mcChat);
		chats.add(infChat);
		currentChat = mcChat;
	}

	public void render(MatrixStack matrices, int tickDelta) {
		if (!this.isChatHidden()) {
			this.processMessageQueue();
			int i = this.getVisibleLineCount();
			int j = currentChat == infChat ? this.currentChat.lines.size() : this.visibleMessages.size();
			if (j > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				float d = (float) this.getChatScale();
				int k = MathHelper.ceil((double) this.getWidth() / d);
	            matrices.push();
	            matrices.translate(4.0D, 8.0D, 0.0D);
	            matrices.scale(d, d, 1.0F);

				double e = this.client.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
				double f = this.client.options.textBackgroundOpacity;
				double g = 9.0D * (this.client.options.chatLineSpacing + 1.0D);
				double h = -8.0D * (this.client.options.chatLineSpacing + 1.0D)
						+ 4.0D * this.client.options.chatLineSpacing;
				int l = 0;

				int m;
				int x;
				int aa;
				int ab;
				if (currentChat == infChat) {
					for (m = 0; m + this.iscrolledLines < this.currentChat.lines.size() && m < i; ++m) {
						ChatHudLine<OrderedText> chatHudLine = (ChatHudLine) this.currentChat.lines
								.get(m + this.iscrolledLines);
						if (chatHudLine != null) {
							x = tickDelta - chatHudLine.getCreationTick();
							if (x < 200 || bl) {
								double o = bl ? 1.0D : getMessageOpacityMultiplier(x);
								aa = (int) (255.0D * o * e);
								ab = (int) (255.0D * o * f);
								++l;
								if (aa > 3) {
									double s = (double) (-m) * g;
									matrices.push();
									matrices.translate(0.0D, 0.0D, 50.0D);
									fill(matrices, -2, (int) (s - g), 0 + k + 4, (int) s, ab << 24);
									topY = (int) (s - g + 4);
									RenderSystem.enableBlend();
									matrices.translate(0.0D, 0.0D, 50.0D);
									this.client.textRenderer.drawWithShadow(matrices,
											(OrderedText) chatHudLine.getText(), 0.0F, (float) ((int) (s + h)),
											16777215 + (aa << 24));
									RenderSystem.disableBlend();
									matrices.pop();
								}
							}
						}
					}
				} else if (currentChat == mcChat) {
					for (m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
						ChatHudLine<OrderedText> chatHudLine = (ChatHudLine) this.visibleMessages
								.get(m + this.scrolledLines);
						if (chatHudLine != null) {
							x = tickDelta - chatHudLine.getCreationTick();
							if (x < 200 || bl) {
								double o = bl ? 1.0D : getMessageOpacityMultiplier(x);
								aa = (int) (255.0D * o * e);
								ab = (int) (255.0D * o * f);
								++l;
								if (aa > 3) {
									double s = (double) (-m) * g;
									matrices.push();
									matrices.translate(0.0D, 0.0D, 50.0D);
									fill(matrices, -2, (int) (s - g), 0 + k + 4, (int) s, ab << 24);
									topY = (int) (s - g + 4);
									RenderSystem.enableBlend();
									matrices.translate(0.0D, 0.0D, 50.0D);
									this.client.textRenderer.drawWithShadow(matrices,
											(OrderedText) chatHudLine.getText(), 0.0F, (float) ((int) (s + h)),
											16777215 + (aa << 24));
									RenderSystem.disableBlend();
									matrices.pop();
								}
							}
						}
					}
				}

				int w;
				if (!this.messageQueue.isEmpty()) {
					m = (int) (128.0D * e);
					w = (int) (255.0D * f);
					matrices.push();
					matrices.translate(0.0D, 0.0D, 50.0D);
					fill(matrices, -2, 0, k + 4, 9, w << 24);
					RenderSystem.enableBlend();
					matrices.translate(0.0D, 0.0D, 50.0D);
					this.client.textRenderer.drawWithShadow(matrices,
							(Text) (new TranslatableText("chat.queue", new Object[] { this.messageQueue.size() })),
							0.0F, 1.0F, 16777215 + (m << 24));
					matrices.pop();
					RenderSystem.disableBlend();
				}

				if (bl) {
					int v = 9;
					w = j * v + j;
					x = l * v + l;
					int y = currentChat == mcChat ? this.scrolledLines * x / j : this.iscrolledLines * x / j;
					int z = x * x / w;
					if (w != x) {
						aa = y > 0 ? 170 : 96;
						ab = this.hasUnreadNewMessages ? 13382451 : 3355562;
						fill(matrices, 0, -y, 2, -y - z, ab + (aa << 24));
						fill(matrices, 2, -y, 1, -y - z, 13421772 + (aa << 24));
					}
				}

				if (bl) {
					topY += 3;
					int i1 = this.chats.size();
					int width = (int) (Math.ceil(getWidth() / d) + 1);
					int tabCount = Math.min(3, i1);
					tabWidth = width / tabCount;

					fill(matrices, 0, topY - 29, tabWidth * 2 + 7, topY - 8, 0xFF363636);
					fill(matrices, 2, topY - 26, tabWidth, topY - 11, currentChat == mcChat ? 0xFF555454 : 0xFF1D1C1C);
					fill(matrices, tabWidth + 3, topY - 26, tabWidth + tabWidth, topY - 11,
							currentChat == infChat ? 0xFF555454 : 0xFF1D1C1C);
					FontUtils.drawHVCenteredString(matrices, "Minecraft", tabWidth / 2 + 2, topY - 18,
							currentChat == mcChat ? -1 : 0xFF8E8B8B);
					FontUtils.drawHVCenteredString(matrices, "Infinity", tabWidth + tabWidth / 2, topY - 18,
							currentChat == infChat ? -1 : 0xFF8E8B8B);

					fill(matrices, 2, topY - 8, tabWidth * 2 + 7, topY - 7, 0xFF74E9EA);

					topY -= 16;
				}
	            matrices.pop();
			}
		}
	}

	private boolean isChatHidden() {
		return this.client.options.chatVisibility == ChatVisibility.HIDDEN;
	}

	private static double getMessageOpacityMultiplier(int age) {
		double d = (double) age / 200.0D;
		d = 1.0D - d;
		d *= 10.0D;
		d = MathHelper.clamp(d, 0.0D, 1.0D);
		d *= d;
		return d;
	}

	public void clear(boolean clearHistory) {
		this.messageQueue.clear();
		this.currentChat.lines.clear();
		this.visibleMessages.clear();
		this.messages.clear();
		if (clearHistory) {
			this.messageHistory.clear();
		}

	}

	public void addMessage(Text message) {
		this.addMessage(message, 0);
	}

	public void addMessage(Text message, int messageId) {
		this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
	}

	public void addInfMessage(Text text) {
		if (currentChat == infChat) {
			addInfMessage(text, 0, this.client.inGameHud.getTicks(), false);
		}
	}

	private void addInfMessage(Text message, int messageId, int timestamp, boolean refresh) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
		List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
		boolean bl = this.isChatFocused();

		OrderedText orderedText;
		for (Iterator<OrderedText> var8 = list.iterator(); var8.hasNext(); this.currentChat.lines.add(0,
				new ChatHudLine<OrderedText>(timestamp, orderedText, messageId))) {
			orderedText = (OrderedText) var8.next();
			if (bl && this.iscrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1.0D);
			}
		}

		while (this.currentChat.lines.size() > 100) {
			this.currentChat.lines.remove(this.currentChat.lines.size() - 1);
		}

		if (!refresh) {
			this.messages.add(0, new ChatHudLine<Text>(timestamp, message, messageId));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void addMessage(Text message, int messageId, int timestamp, boolean refresh) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
		List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
		boolean bl = this.isChatFocused();

		OrderedText orderedText;
		for (Iterator<OrderedText> var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0,
				new ChatHudLine<OrderedText>(timestamp, orderedText, messageId))) {
			orderedText = (OrderedText) var8.next();
			if (bl && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1.0D);
			}
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!refresh) {
			this.messages.add(0, new ChatHudLine<Text>(timestamp, message, messageId));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}

	}

	public void reset() {
		this.currentChat.lines.clear();
		this.visibleMessages.clear();
		this.resetScroll();

		for (int i = this.messages.size() - 1; i >= 0; --i) {
			ChatHudLine<Text> chatHudLine = (ChatHudLine<Text>) this.messages.get(i);
			this.addMessage((Text) chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
		}

	}

	public List<String> getMessageHistory() {
		return this.messageHistory;
	}

	public void addToMessageHistory(String message) {
		if (this.messageHistory.isEmpty()
				|| !((String) this.messageHistory.get(this.messageHistory.size() - 1)).equals(message)) {
			this.messageHistory.add(message);
		}

	}

	public void resetScroll() {
		if (currentChat == mcChat)
			this.scrolledLines = 0;
		else
			this.iscrolledLines = 0;
		this.hasUnreadNewMessages = false;
	}

	public void scroll(double amount) {
		if (currentChat == infChat) {
			this.iscrolledLines = (int) ((double) this.iscrolledLines + amount);
			int i = this.currentChat.lines.size();
			if (this.iscrolledLines > i - this.getVisibleLineCount()) {
				this.iscrolledLines = i - this.getVisibleLineCount();
			}

			if (this.iscrolledLines <= 0) {
				this.iscrolledLines = 0;
				this.hasUnreadNewMessages = false;
			}
		} else if (currentChat == mcChat) {
			this.scrolledLines = (int) ((double) this.scrolledLines + amount);
			int i = this.visibleMessages.size();
			if (this.scrolledLines > i - this.getVisibleLineCount()) {
				this.scrolledLines = i - this.getVisibleLineCount();
			}

			if (this.scrolledLines <= 0) {
				this.scrolledLines = 0;
				this.hasUnreadNewMessages = false;
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		if (this.isChatFocused()) {
			int i = this.chats.size();
			int width = (int) (Math.ceil(getWidth() / getChatScale()) + 1);
			int tabCount = Math.min(3, i);
			float tabWidth = width / tabCount;

			if (Render2D.isFillHovered(mouseX, mouseY, tabWidth - tabWidth, Render2D.getScaledHeight() + topY - 2 - 50,
					tabWidth, Render2D.getScaledHeight() + topY + 13 - 50)) {
				this.currentChat = mcChat;

			} else if (Render2D.isFillHovered(mouseX, mouseY, tabWidth, Render2D.getScaledHeight() + topY - 2 - 50,
					tabWidth + tabWidth, Render2D.getScaledHeight() + topY + 13 - 50)) {
				this.currentChat = infChat;

			}
			topY -= 16;
		}
		return false;
	}

	@Nullable
	public Style getText(double x, double y) {
		if (currentChat == infChat) {
			if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
				double d = x - 2.0D;
				double e = (double) this.client.getWindow().getScaledHeight() - y - 40.0D;
				d = (double) MathHelper.floor(d / this.getChatScale());
				e = (double) MathHelper.floor(e / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0D)));
				if (d >= 0.0D && e >= 0.0D) {
					int i = Math.min(this.getVisibleLineCount(), this.currentChat.lines.size());
					if (d <= (double) MathHelper.floor((double) this.getWidth() / this.getChatScale())) {
						this.client.textRenderer.getClass();
						if (e < (double) (9 * i + i)) {
							this.client.textRenderer.getClass();
							int j = (int) (e / 9.0D + (double) this.scrolledLines);
							if (j >= 0 && j < this.currentChat.lines.size()) {
								ChatHudLine<OrderedText> chatHudLine = (ChatHudLine) this.currentChat.lines.get(j);
								return this.client.textRenderer.getTextHandler()
										.getStyleAt((OrderedText) chatHudLine.getText(), (int) d);
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else if (currentChat == mcChat) {
				if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
					double d = x - 2.0D;
					double e = (double) this.client.getWindow().getScaledHeight() - y - 40.0D;
					d = (double) MathHelper.floor(d / this.getChatScale());
					e = (double) MathHelper
							.floor(e / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0D)));
					if (d >= 0.0D && e >= 0.0D) {
						int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
						if (d <= (double) MathHelper.floor((double) this.getWidth() / this.getChatScale())) {
							this.client.textRenderer.getClass();
							if (e < (double) (9 * i + i)) {
								this.client.textRenderer.getClass();
								int j = (int) (e / 9.0D + (double) this.scrolledLines);
								if (j >= 0 && j < this.visibleMessages.size()) {
									ChatHudLine<OrderedText> chatHudLine = (ChatHudLine) this.visibleMessages.get(j);
									return this.client.textRenderer.getTextHandler()
											.getStyleAt((OrderedText) chatHudLine.getText(), (int) d);
								}
							}
						}

						return null;
					} else {
						return null;
					}
				}
			}
		}
		return null;
	}

	private boolean isChatFocused() {
		return this.client.currentScreen instanceof ChatScreen;
	}

	public void removeMessage(int messageId) {
		if (currentChat == infChat) {
			this.currentChat.lines.removeIf((message) -> {
				return message.getId() == messageId;
			});
		} else if (currentChat == mcChat) {
			this.visibleMessages.removeIf((message) -> {
				return message.getId() == messageId;
			});
		}
		this.messages.removeIf((message) -> {
			return message.getId() == messageId;
		});
	}

	public int getWidth() {
		return getWidth(this.client.options.chatWidth);
	}

	public int getHeight() {
		return getHeight(
				(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused)
						/ (this.client.options.chatLineSpacing + 1.0D));
	}

	public double getChatScale() {
		return this.client.options.chatScale;
	}

	public static int getWidth(double widthOption) {
		return MathHelper.floor(widthOption * 280.0D + 40.0D);
	}

	public static int getHeight(double heightOption) {
		return MathHelper.floor(heightOption * 160.0D + 20.0D);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}

	private long getChatDelayMillis() {
		return (long) (this.client.options.chatDelay * 1000.0D);
	}

	private void processMessageQueue() {
		if (!this.messageQueue.isEmpty()) {
			long l = System.currentTimeMillis();
			if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
				this.addMessage((Text) this.messageQueue.remove());
				this.lastMessageAddedTime = l;
			}

		}
	}

	public void queueMessage(Text message) {
		if (this.client.options.chatDelay <= 0.0D) {
			this.addMessage(message);
		} else {
			long l = System.currentTimeMillis();
			if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
				this.addMessage(message);
				this.lastMessageAddedTime = l;
			} else {
				this.messageQueue.add(message);
			}
		}

	}
}
