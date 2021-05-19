package org.infinity.ui.util.font;

import net.minecraft.client.util.math.MatrixStack;

public interface ILegacyFont {

	double drawChar(MatrixStack matrices, char c, double x, double y);
	
	int drawString(MatrixStack matrices, String text, double x, double y, int color, double size, boolean shadow);

	int drawString(MatrixStack matrices, String text, double x, double y, int color, double size);

	int drawStringWithShadow(MatrixStack matrices, String text, double x, double y, int color, double size);

	int drawSplitString(MatrixStack matrices, String text, double x, double y, int color, double width);

	double drawSplitString(MatrixStack matrices, String text, double x, double y, int color, double width,
			double height);

	double getCharWidth(char c);

	double getStringWidth(String text);

	double getStringWidth(String text, double height);

	int getFontHeight();

	double getFontHeightWithCustomWidth(String text, double width);
}
