package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@ModuleInfo(category = Module.Category.VISUAL, desc = "View ore blocks", key = GLFW.GLFW_KEY_X, name = "XRay", visible = true)
public class XRay extends Module {
	
	private ArrayList<Block> blocks;
	
	public Settings block = new Settings(this, blocks, new ArrayList<Block>(Arrays.asList(
			Blocks.DIAMOND_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.GOLD_ORE)));
	
	@Override
	public void onDisable() {
	}
	
	public void add(Block block) {
		blocks.add(block);
	}
	
	public void remove(Block block) {
		blocks.remove(block);
	}

}
