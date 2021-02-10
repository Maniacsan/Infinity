package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ColorButton extends SettingButton {

	private boolean extended;

	private boolean selectingHue;

	private boolean selectingSB;

	private boolean hovered;
	
	private boolean hoveredExtended;

	private boolean hueHovered;

	public ColorButton(Settings setting) {
		super(setting);
		this.selectingHue = false;
		this.selectingSB = false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		double boxPosX = x + 80.0D;
		double boxPosY = y + 2.0D;
		double boxWidth = 115.0D;
		double boxHeight = this.extended ? 115.0D : 10.0D;
		hoveredExtended = Render2D.isHovered(mouseX, mouseY, boxPosX + 40, boxPosY, 9, boxHeight);
		hovered = Render2D.isHovered(mouseX, mouseY, boxPosX, boxPosY, boxWidth - 12, boxHeight - 12);
		hueHovered = Render2D.isHovered(mouseX, mouseY, boxPosX + boxWidth - 12, boxPosY, 12, boxHeight);
		if (!this.extended) {
			Render2D.drawRectangle(boxPosX + 51.0D, boxPosY + 2.0D, boxPosX + boxWidth - 51.0D,
					boxPosY + boxHeight - 2.0D, (this.setting.getColor()).getRGB());
			this.selectingHue = false;
			this.selectingSB = false;
		} else {
			Render2D.drawRectangle(boxPosX + 1.0D, boxPosY + 1.0D, boxPosX + boxWidth - 1.0D,
					boxPosY + boxHeight - 1.0D, (new Color(24, 24, 24)).getRGB());
			float inc = 0.01F;
			int j;
			for (j = 0; j < 100; j++) {
				float currentSaturation = j * inc;
				Render2D.drawGradientRect(boxPosX + 2.0D + j, boxPosY + 2.0D, boxPosX + 3.0D + j,
						boxPosY + boxHeight - 14.0D,
						Color.getHSBColor(this.setting.getHue(), currentSaturation, 1.0F).getRGB(), 0xFF000000);
		        for (int i2 = 0; i2 < 100; i2++) {
		        	if (selectingSB) {
			            this.setting.setSaturation(j * inc);
			            this.setting.setBrightness(i2 * inc);
		        	}
		        }
			}
			this.y = boxPosY;
			this.height = boxHeight;
			if (!this.selectingHue) {
				
			} else {
				double barHeight = boxHeight - 17.0D;
				double mousePosOnBar = mouseY - boxPosY + 2.0D;
				if (mousePosOnBar < 0.0D) {
					mousePosOnBar = 0.0D;
				} else if (mousePosOnBar > barHeight) {
					mousePosOnBar = barHeight;
				}
				this.setting.setHue((float) (1.0D / barHeight * mousePosOnBar));
			}
			for (int i = 0; i < 100; i++) {
				Render2D.drawRectangle(boxPosX + boxWidth - 11.0D, boxPosY + 2.0D + i, boxPosX + boxWidth - 2.0D,
						boxPosY + 3.0D + i, Color.getHSBColor(i * inc, 1.0F, 1.0F).getRGB());
				if (Math.abs(i * inc - this.setting.getHue()) <= 0.025D)
					Render2D.drawRectangle(boxPosX + boxWidth - 11.0D, boxPosY + 2.0D + i, boxPosX + boxWidth - 2.0D,
							boxPosY + 3.0D + i, 0xFF803300);
			}
			
		      for (j = 0; j < 100; j++) {
		          for (int i2 = 0; i2 < 100; i2++) {
		            float currentSaturation = j * inc;
		            float currentBrightness = i2 * inc;
		            if (Math.abs(currentSaturation - this.setting.getSaturation()) < 0.025D && Math.abs(currentBrightness - this.setting.getBrightness()) < 0.025D)
		            	Render2D.drawRectangle(boxPosX + 2.0D + j, boxPosY + boxHeight - 14.0D - i2, boxPosX + 3.0D + j, boxPosY + boxHeight - 15.0D - i2, 0xFF000000); 
		          } 
		        } 
			Render2D.drawRectangle(boxPosX + 2.0D, boxPosY + boxHeight - 12.0D, boxPosX + boxWidth - 2.0D,
					boxPosY + boxHeight - 2.0D, 0x90000000);
			Render2D.drawRectangle(boxPosX + 2.5D, boxPosY + boxHeight - 11.5D, boxPosX + boxWidth - 2.5D,
					boxPosY + boxHeight - 2.5D, (this.setting.getColor()).getRGB());
			FontUtils.drawHVCenteredString(matrices, String.valueOf(setting.getSaturation()), boxPosX + 4, boxPosY + 30, -1);
		      double barWidth = boxWidth - 4.0D;
			double pos = 255 / 255.0D * barWidth;
			Render2D.drawRectangle(boxPosX + pos + 1.0D, boxPosY + boxHeight - 11.5D, boxPosX + pos + 2.0D, boxPosY + boxHeight - 2.5D, 0xff000000);
		   
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (!this.extended) {
				if (hoveredExtended) {
					this.extended = true;
				}
			} else {
				if (hovered) {
					this.selectingSB = true;
				}
				if (hueHovered) {
					this.selectingHue = true;
				}
			}
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.selectingHue = false;
		this.selectingSB = false;
	}

}
