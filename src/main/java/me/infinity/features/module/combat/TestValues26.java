package me.infinity.features.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@ModuleInfo(category = Module.Category.COMBAT, desc = "TEST", key = 49, name = "TestValues26", visible = true)
public class TestValues26 extends Module {

	private Settings testbool = new Settings(this, "TestBool", true);
	private Settings testbool2 = new Settings(this, "TestBool2", true);
	private Settings testbool3 = new Settings(this, "TestBool23", true);
	private Settings testbool4 = new Settings(this, "TestBool24", true);
	private Settings testbool5 = new Settings(this, "TestBool25", true);
	private Settings testFloat = new Settings(this, "testfloat", 1.0F, 0.0F, 5.0F);
	private Settings testNum = new Settings(this, "TestNum", 5.0D, 0.0D, 10.0D);
	private Settings testInt = new Settings(this, "TestNum", 5, 0, 10);
	private Settings testMode = new Settings(this, "TestMode", "SUCK",
			new ArrayList<>(Arrays.asList(new String[] { "SUCK", "SUCK2", "OMG" })));
	private Settings testMode2 = new Settings(this, "TestMode2", "SUCK",
			new ArrayList<>(Arrays.asList(new String[] { "SUCK", "SUCK2", "OMG" })));
	private Settings testMode3 = new Settings(this, "TestMode3", "SUCK",
			new ArrayList<>(Arrays.asList(new String[] { "SUCK", "SUCK2", "OMG" })));
	private Settings testNum2 = new Settings(this, "TestNum2", 5.0D, 0.0D, 10.0D);
	private Settings testInt3 = new Settings(this, "TestNum3", 5, 0, 10);
	private Settings testColor = new Settings(this, "TestColor", new Color(250, 50, 50));
	private Settings testColor2 = new Settings(this, "TestColor2", new Color(250, 50, 50));
	private Settings testColor3 = new Settings(this, "TestColor3", new Color(250, 50, 50));
	private Settings testColor4 = new Settings(this, "TestColor4", new Color(250, 50, 50));
	private Settings testColor5 = new Settings(this, "TestColor5", new Color(250, 50, 50));
	private Settings testInt2 = new Settings(this, "TestInt", 5, 0, 10);

}
