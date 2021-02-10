package me.infinity.features;

import java.awt.Color;
import java.util.ArrayList;

import me.infinity.InfMain;
import net.minecraft.block.Block;

/**
 * @Enaium base
 *
 */
public class Settings {

	private Module module;
	private String name;
	private boolean toggle;
	private double currentValueDouble, minValueDouble, maxValueDouble;
	private int currentValueInt, minValueInt, maxValueInt;
	private float currentValueFloat, minValueFloat, maxValueFloat;
	private ArrayList<Block> blocks;
	private ArrayList<Block> renderBlocks;
	private ArrayList<String> modes;
	private String currentMode;
	private Color color;
	private float hue;
	private float saturation;
	private float brightness;
	private Category category;

	public enum Category {
		BOOLEAN, VALUE_INT, VALUE_DOUBLE, VALUE_FLOAT, MODE, COLOR, BLOCKS;
	}

	// boolean
	public Settings(Module module, String name, boolean toggle) {
		this.module = module;
		this.name = name;
		this.toggle = toggle;
		this.category = Category.BOOLEAN;
	}

	// int number
	public Settings(Module module, String name, int currentValueInt, int minValueInt, int maxValueInt) {
		this.module = module;
		this.name = name;
		this.currentValueInt = currentValueInt;
		this.minValueInt = minValueInt;
		this.maxValueInt = maxValueInt;
		this.category = Category.VALUE_INT;
	}

	// double number
	public Settings(Module module, String name, double currentValueDouble, double minValueDouble,
			double maxValueDouble) {
		this.module = module;
		this.name = name;
		this.currentValueDouble = currentValueDouble;
		this.minValueDouble = minValueDouble;
		this.maxValueDouble = maxValueDouble;
		this.category = Category.VALUE_DOUBLE;
	}

	// float number
	public Settings(Module module, String name, float currentValueFloat, float minValueFloat, float maxValueFloat) {
		this.module = module;
		this.name = name;
		this.currentValueFloat = currentValueFloat;
		this.minValueFloat = minValueFloat;
		this.maxValueFloat = maxValueFloat;
		this.category = Category.VALUE_FLOAT;
	}

	// String mode
	public Settings(Module module, String name, String currentMode, ArrayList<String> options) {
		this.module = module;
		this.name = name;
		this.currentMode = currentMode;
		this.modes = options;
		this.category = Category.MODE;
	}

	// Color
	public Settings(Module module, String name, Color currentColor, float hue, float saturation, float brightness) {
		this.module = module;
		this.name = name;
		this.setHue(hue);
		this.setSaturation(saturation);
		this.setBrightness(brightness);
		this.color = currentColor;
		Color hsb = Color.getHSBColor(hue, saturation, brightness);
		currentColor = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), 255);
		this.category = Category.COLOR;
	}

	// Blocks
	public Settings(Module module, ArrayList<Block> blocks, ArrayList<Block> renderBlocks) {
		this.module = module;
		this.blocks = blocks;
		this.renderBlocks = renderBlocks;
		this.category = Category.BLOCKS;
	}

	public String getCollectSettings() {
		String list = "";
		for (Module m : InfMain.getModuleManager().getList()) {
			for (Settings s : m.getSettings()) {
				if (s.isBoolean()) {
					list =(Boolean.toString(isToggle()));
				} else if (s.isMode()) {
					list = (getCurrentMode());
				} else if (s.isValueDouble()) {
					list = (Double.toString(getCurrentValueDouble()));
				} else if (s.isValueFloat()) {
					list = (Float.toString(getCurrentValueFloat()));
				} else if (s.isValueInt()) {
					list = (Integer.toString(getCurrentValueInt()));
				}
			}
		}
		return list;
	}

	public ArrayList<Block> getRenderBlocks() {
		return renderBlocks;
	}

	public void setRenderBlocks(ArrayList<Block> renderBlocks) {
		this.renderBlocks = renderBlocks;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	public double getCurrentValueDouble() {
		return currentValueDouble;
	}

	public void setCurrentValueDouble(double currentValueDouble) {
		this.currentValueDouble = currentValueDouble;
	}

	public double getMinValueDouble() {
		return minValueDouble;
	}

	public void setMinValueDouble(double minValueDouble) {
		this.minValueDouble = minValueDouble;
	}

	public double getMaxValueDouble() {
		return maxValueDouble;
	}

	public void setMaxValueDouble(double maxValueDouble) {
		this.maxValueDouble = maxValueDouble;
	}

	public int getCurrentValueInt() {
		return currentValueInt;
	}

	public void setCurrentValueInt(int currentValueInt) {
		this.currentValueInt = currentValueInt;
	}

	public int getMinValueInt() {
		return minValueInt;
	}

	public void setMinValueInt(int minValueInt) {
		this.minValueInt = minValueInt;
	}

	public int getMaxValueInt() {
		return maxValueInt;
	}

	public void setMaxValueInt(int maxValueInt) {
		this.maxValueInt = maxValueInt;
	}

	public float getCurrentValueFloat() {
		return currentValueFloat;
	}

	public void setCurrentValueFloat(float currentValueFloat) {
		this.currentValueFloat = currentValueFloat;
	}

	public float getMinValueFloat() {
		return minValueFloat;
	}

	public void setMinValueFloat(float minValueFloat) {
		this.minValueFloat = minValueFloat;
	}

	public float getMaxValueFloat() {
		return maxValueFloat;
	}

	public void setMaxValueFloat(float maxValueFloat) {
		this.maxValueFloat = maxValueFloat;
	}

	public ArrayList<String> getModes() {
		return modes;
	}

	public void setModes(ArrayList<String> modes) {
		this.modes = modes;
	}

	public String getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(String currentMode) {
		this.currentMode = currentMode;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	public int getCurrentModeIndex() {
		int index = 0;
		for (String s : modes) {
			if (s.equalsIgnoreCase(currentMode)) {
				return index;
			}
			index++;
		}
		return index;
	}

	public float getSaturation() {
		return saturation;
	}

	public void setSaturation(float saturation) {
		if (saturation < 0.0F || saturation > 1.0F)
			return;
		this.saturation = saturation;
		Color hsb = Color.getHSBColor(this.hue, saturation, this.brightness);
		Color converted = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), 100);
		this.color = converted;
	}

	public float getHue() {
		return hue;
	}

	public void setHue(float hue) {
		if (hue < 0.0F || hue > 1.0F)
			return;
		this.hue = hue;
		Color hsb = Color.getHSBColor(hue, this.saturation, this.brightness);
		Color converted = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), 255);
		this.color = converted;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		if (brightness < 0.0F || brightness > 1.0F)
			return;
		this.brightness = brightness;
		Color hsb = Color.getHSBColor(this.hue, this.saturation, brightness);
		Color converted = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), 255);
		this.color = converted;
	}

	public boolean isBoolean() {
		return this.category.equals(Category.BOOLEAN);
	}

	public boolean isValueInt() {
		return this.category.equals(Category.VALUE_INT);
	}

	public boolean isValueDouble() {
		return this.category.equals(Category.VALUE_DOUBLE);
	}

	public boolean isValueFloat() {
		return this.category.equals(Category.VALUE_FLOAT);
	}

	public boolean isValue() {
		return this.category.equals(Category.VALUE_INT) || this.category.equals(Category.VALUE_DOUBLE)
				|| this.category.equals(Category.VALUE_FLOAT);
	}

	public boolean isMode() {
		return this.category.equals(Category.MODE);
	}

	public boolean isColor() {
		return this.category.equals(Category.COLOR);
	}

	public boolean isBlock() {
		return this.category.equals(Category.BLOCKS);
	}

	public String getCategory() {
		if (isBoolean()) {
			return "Boolean";
		}
		if (isValueInt()) {
			return "Int";
		}
		if (isValueDouble()) {
			return "Double";
		}
		if (isValueFloat()) {
			return "Float";
		}
		if (isMode()) {
			return "String";
		}
		if (isColor()) {
			return "Color";
		}
		if (isBlock()) {
			return "Blocks";
		}
		return "";
	}

}
