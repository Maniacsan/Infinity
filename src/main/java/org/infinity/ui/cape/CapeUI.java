package org.infinity.ui.cape;

import java.awt.Color;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.infinity.features.component.cape.CapeModel;
import org.infinity.features.component.cape.Capes.Cape;
import org.infinity.features.component.cape.Capes.Role;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.util.CustomButtonWidget;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;
import org.infinity.utils.render.ColorUtils;
import org.infinity.utils.render.Render2D;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class CapeUI extends Screen {

	private List<CapeBox> capeList = new ArrayList<>();
	private double pointX, pointY;

	private Timer scrollTimer = new Timer();

	private double scrollSpeed;
	private double prevScrollProgress;
	private double scrollProgress;
	private double _cbuttonHeight;

	private double xscale;

	private int tabs;

	public CapeUI() {
		super(new LiteralText(""));
	}

	@Override
	public void init() {
		capeList.clear();

		for (Cape cape : InfMain.getCape().getCapes()) {
			try {
				capeList.add(new CapeBox(cape.getName(), cape.getUrl(), cape.getRole()));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}

		this.addDrawableChild(new CustomButtonWidget(width / 2 + 70, height / 2 + 120, 80, 20,
				new TranslatableText("Done"), (buttonWidget) -> {
					done();
				}));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		pointX = 220;
		pointY = 17;

		if (Helper.getWorld() == null)
			Render2D.verticalGradient(matrices, 0, 0, width, height, 0xFF0B0D1B, 0xFF243E76);

		// It's just an angeled rect
		Render2D.drawCircle(matrices, width / 2 - pointX, pointY, 10, 0xFF142F50);
		Render2D.drawCircle(matrices, width / 2 + pointX, pointY, 10, 0xFF142F50);
		Render2D.drawCircle(matrices, width / 2 - pointX, pointY + (height - (pointY * 2)), 10, 0xFF142F50);
		Render2D.drawCircle(matrices, width / 2 + pointX, pointY + (height - (pointY * 2)), 10, 0xFF142F50);

		Render2D.drawRectWH(matrices, width / 2 - pointX - 5, pointY, 5, height - (pointY * 2), 0xFF142F50);
		Render2D.drawRectWH(matrices, width / 2 + pointX, pointY, 5, height - (pointY * 2), 0xFF142F50);

		Render2D.drawRectWH(matrices, width / 2 - pointX, pointY - 5, (pointX * 2), 5, 0xFF142F50);
		Render2D.drawRectWH(matrices, width / 2 - pointX, pointY + (height - (pointY * 2)), (pointX * 2), 5,
				0xFF142F50);

		Render2D.drawRectWH(matrices, width / 2 - pointX, pointY, pointX * 2, height - (pointY * 2), 0xFF142F50);

		double dx = width / 2 + pointX - 10;
		double dy = pointY + 2;

		xscale = Render2D.isHovered(mouseX, mouseY, dx - 10, dy - 10, 20, 20) ? Math.min(1.2, xscale + 0.1) : 1;

		matrices.push();
		matrices.translate(dx + 4, dy + 4, 0);
		matrices.scale((float) xscale, (float) xscale, 1);
		matrices.translate(-dx - 4, -dy - 4, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/exit.png"), dx, dy, 8, 8);
		matrices.pop();

		double offsetX = 0;
		double offsetY = 0;
		int tab = 1;

		boolean scrollActive = _cbuttonHeight > height - 40;
		int ws = scrollActive ? 2 : -2;
		Render2D.drawRectWH(matrices, width / 2 - (pointX - 5), 18, 67 * 3 + ws, height - 40, 0x80000000);
		Render2D.startScissor((width / 2 - (pointX - 5)), 18, 68 * 3, height - (20 * 2));
		for (CapeBox capes : capeList) {
			_cbuttonHeight = offsetY;
			capes.setX(offsetX + (width / 2 - (pointX - 10)));
			capes.setY(20 + offsetY - getScrollProgress());
			capes.setWidth(60);
			capes.setHeight(80);
			capes.render(matrices, mouseX, mouseY, delta);

			offsetX += 65;

			if (offsetX > 130) {
				offsetX = 0;
				offsetY += 85;
				tab += 1;
			}
		}

		if (scrollActive) {
			Render2D.drawVRoundedRect(matrices, width / 2 - (pointX - 10) + 66 * 3 - 3, 21, 2, height - 46, 0xFF090909);
			Render2D.drawVRoundedRect(matrices, width / 2 - (pointX - 10) + 66 * 3 - 3, 21 + getScrollProgress() / 3, 2,
					height - 46 - (getHeightDifference() / 3), -1);
		}

		Render2D.stopScissor();

		tabs = tab;

		drawPlayer(matrices, Helper.MC.getSession().getUsername(), width / 2 + 60, height / 2 - 100, 100, 200);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		if (_cbuttonHeight < this.height - 40)
			return;

		double difference = getHeightDifference();

		setScrollProgress(scrollProgress + scrollSpeed);
		scrollSpeed *= 0.54;

		if (scrollTimer.hasReached(100)) {
			if (scrollProgress < 0)
				scrollSpeed = scrollProgress * -0.35;
			else if (scrollProgress > difference)
				scrollSpeed = (scrollProgress - difference) * -0.35;
		}

		super.tick();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		capeList.forEach(capes -> {
			capes.mouseClicked(mouseX, mouseY, button);
		});
		double dx = width / 2 + pointX - 10;
		double dy = pointY + 2;

		if (Render2D.isHovered(mouseX, mouseY, dx - 10, dy - 10, 20, 20))
			onClose();

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		if (amount != 0) {
			double sa = amount < 0 ? amount - 10 : amount + 10;
			scrollTimer.reset();
			scrollSpeed -= sa;
		}
		return super.mouseScrolled(e, e, amount);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return super.charTyped(chr, keyCode);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void done() {
		InfMain.getCape().update();
		InfMain.getCape().initCapes();
		onClose();
	}

	public double getHeightDifference() {
		double diffHeight = height - 40;
		return (getListHeight() - diffHeight);
	}

	private double getListHeight() {
		double height = 0;
		for (CapeBox list : capeList)
			height = tabs * (list.getHeight() + 5);
		return height;
	}

	private double getScrollProgress() {
		return prevScrollProgress + (scrollProgress - prevScrollProgress) * Helper.MC.getLastFrameDuration();
	}

	private void setScrollProgress(double value) {
		prevScrollProgress = scrollProgress;
		scrollProgress = value;
	}

	public static void drawPlayer(MatrixStack matrixStack, String name, int x, int y, int width, int height) {
		try {
			String role = InfMain.getUser().getRole().getName();
			String profileName = ColorUtils.getUserRoleColor() + role + " " + Formatting.WHITE
					+ InfMain.getUser().getName();
			IFont.legacy13.drawCenteredString(matrixStack, profileName, x + width / 2,
					y - 10 - IFont.legacy13.getFontHeight(), -1);

			RenderUtil.bindSkinTexture(Helper.MC.getSession().getProfile().getId(), name);

			boolean slim = DefaultSkinHelper.getModel(PlayerEntity.getOfflinePlayerUuid(name)).equals("slim");

			GL11.glEnable(GL11.GL_BLEND);
			RenderSystem.setShaderColor(1, 1, 1, 1);

			// Face
			x = x + width / 4;
			y = y + 0;
			int w = width / 2;
			int h = height / 4;
			int fw = height * 2;
			int fh = height * 2;
			float u = height / 4 * 3;
			float v = height / 4;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Hat
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 4;
			u = height / 4 * 7;
			v = height / 4;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Chest
			x = x + 0;
			y = y + height / 4;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 4;
			v = height / 4 * 2.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Jacket
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 8 * 3;
			u = height / 4 * 4;
			v = height / 4 * 4.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Left Arm
			x = x - width / 16 * (slim ? 3 : 4);
			y = y + (slim ? height / 32 : 0);
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 2.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Left Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 4.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Right Arm
			x = x + width / 16 * (slim ? 11 : 12);
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 2.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Right Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			u = height / 4 * (slim ? 6.375F : 6.5F);
			v = height / 4 * 4.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Left Leg
			x = x - width / 2;
			y = y + height / 32 * (slim ? 11 : 12);
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 2.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Left Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 4.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Right Leg
			x = x + width / 4;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 2.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			// Right Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			u = height / 4 * 1.5F;
			v = height / 4 * 4.5F;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, w, h, fw, fh);

			GL11.glDisable(GL11.GL_BLEND);

			// Cape
			if (InfMain.getCape().CURRENT_NAME.isEmpty() || InfMain.getCape().CURRENT_NAME == null)
				return;

			Identifier id = new Identifier("infinity_capes/" + InfMain.getCape().CURRENT_NAME.toLowerCase());

			int ys = slim ? 66 : 72;
			// Background for transparent capes
			Render2D.drawRectWH(matrixStack, x - 29, y - 66, 61, 103, 0xFF222222);

			RenderSystem.setShaderTexture(0, id);

			RenderSystem.enableBlend();
			DrawableHelper.drawTexture(matrixStack, x - 29, y - ys, 7, 7, 61, 103, 404, 210);
			RenderSystem.disableBlend();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class CapeBox {

		private double x, y, width, height;
		private String capeName;
		private String capeUrl;
		private CapeModel capeModel;
		private Identifier id;

		private boolean selected;

		private Role role;

		private double animX;
		private double fade;

		public CapeBox(String capeName, String capeURL, Role role) throws MalformedURLException {
			this.capeName = capeName;
			this.capeUrl = capeURL;
			this.role = role;
			capeModel = new CapeModel(
					Helper.MC.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER).getChild("cloak"));
			id = new Identifier("infinity_capes/" + capeName.toLowerCase());
			RenderUtil.TEXTURE.downloadCapeFromUrl(id, capeURL);

			if (InfMain.getCape().CURRENT_NAME.equalsIgnoreCase(capeName) && !locked())
				setSelected(true);
		}

		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			fade = Render2D.isHovered(mouseX, mouseY, x, y, width, height) ? Math.min(1.04, fade + 0.03)
					: Math.max(1, fade - 0.03);

			if (!capeUrl.toString().equalsIgnoreCase(InfMain.getCape().CURRENT_CAPE))
				setSelected(false);

			matrices.push();

			matrices.translate(x + (width / 2), y + (height / 2), 0);
			matrices.scale((float) fade, (float) fade, 1f);
			matrices.translate(-x - (width / 2), -y - (height / 2), 0);

			Render2D.drawBorderedRect(matrices, x, y, width, height, 0.5F, isSelected() ? 0xFFDFBA33 : 0xFF585757,
					new Color(10, 10, 10, 255).getRGB());

			boolean locked = locked();

			renderCape(matrices, locked, mouseX, mouseY, delta);

//			RenderUtil.TEXTURE.bindTexture(capeName, capeUrl);
//  		DrawableHelper.drawTexture(matrices, (int) (x + (width / 2) - 22), (int) y + 2, 6, 6, 43, 73, 100, 80);

			IFont.legacy12.drawCenteredString(matrices, capeName, x + width / 2,
					y + height - 7 - (IFont.legacy12.getFontHeight()), 0xFFFFFFFF);

			if (locked) {
				Render2D.verticalGradient(matrices, x, y, x + width, y + height, 0x90101010, 0x902C2C2C);
				RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/locked.png"),
						x + width / 2 - 8, y + height / 2 - 21, 16, 16);

				String srole = role.name().substring(0, 1).toUpperCase() + role.name().substring(1).toLowerCase();
				if (srole.equalsIgnoreCase("Youtube"))
					srole = Formatting.RED + "You" + Formatting.BLACK + "Tube";

				IFont.legacy10.drawCenteredString(matrices, "Available only for the", x + width / 2,
						y + height / 2 - 5 + IFont.legacy10.getFontHeight(), -1);
				IFont.legacy10.drawCenteredString(matrices,
						ColorUtils.getStringUserColor(srole) + srole + Formatting.WHITE + " License", x + width / 2 - 3,
						y + height / 2 - 5 + IFont.legacy10.getFontHeight() + 7, -1);

			}

			matrices.pop();
		}

		public void renderCape(MatrixStack matrices, boolean locked, int mouseX, int mouseY, float delta) {
			matrices.push();
			matrices.translate(x + width / 2, y + 7, 500D);
			matrices.multiply(new Quaternion(-16f, (float) animX, 0f, true));
			matrices.scale(50f, 50f, 54f);

			VertexConsumerProvider.Immediate immediate = Helper.MC.getBufferBuilders().getEntityVertexConsumers();
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(immediate,
					RenderLayer.getArmorCutoutNoCull(id), false, isSelected());

			float alpha = locked ? 0.5f : 1f;
//			RenderSystem.setShaderTexture(0, id);
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);

			RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

//			RenderSystem.enableBlend();
//			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			capeModel.render(matrices, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
//			Tessellator.getInstance().draw();
			immediate.draw();

//			RenderSystem.disableBlend();

			if (Render2D.isHovered(mouseX, mouseY, x + width / 2 - 20, y + 7, 40, height - 26)) {
				animX -= 2;
				if (animX < -180)
					animX = 180;
			} else
				animX = 144;

			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			matrices.pop();
		}

		public void mouseClicked(double mouseX, double mouseY, int button) {
			if (Render2D.isHovered(mouseX, mouseY, x, y, width, height)) {
				if (locked()) {
					setSelected(false);
					return;
				}

				setSelected(!isSelected());

				if (isSelected()) {
					InfMain.getCape().setCurrent(capeName, capeUrl);
				} else {
					InfMain.getCape().setCurrent("", "");
				}

				if (!capeUrl.toString().equalsIgnoreCase(InfMain.getCape().CURRENT_CAPE))
					setSelected(false);
			}
		}

		private boolean locked() {
			return InfMain.getCape().locked(capeName);
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

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

	}

}
