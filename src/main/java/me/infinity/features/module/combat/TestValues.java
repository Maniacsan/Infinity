package me.infinity.features.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;

@ModuleInfo(category = Module.Category.COMBAT, desc = "TEST", key = 49, name = "TestValues", visible = true)
public class TestValues extends Module {

	private Settings testbool = new Settings(this, "TestBool", true);
	private Settings testFloat = new Settings(this, "testfloat", 1.0F, 0.0F, 5.0F);
	private Settings testNum = new Settings(this, "TestNum", 5.0D, 0.0D, 10.0D);
	private Settings testInt = new Settings(this, "TestNum", 5, 0, 10);
	private Settings testMode = new Settings(this, "TestMode", "SUCK",
			new ArrayList<>(Arrays.asList(new String[] { "SUCK", "SUCK2", "OMG" })));
	private Settings testColor = new Settings(this, "TestColor", new Color(250, 50, 50));


}
