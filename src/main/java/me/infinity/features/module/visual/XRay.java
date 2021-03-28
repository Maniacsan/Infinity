package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@ModuleInfo(category = Module.Category.VISUAL, desc = "View ore blocks", key = GLFW.GLFW_KEY_X, name = "XRay", visible = true)
public class XRay extends Module {

	private ArrayList<Block> blocks = new ArrayList<>();
	public Settings block = new Settings(this, "Blocks", blocks,
			new ArrayList<Block>(Arrays.asList(Blocks.DIAMOND_ORE, Blocks.COAL_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE,
					Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
					Blocks.REDSTONE_ORE, Blocks.ORANGE_CONCRETE, Blocks.STONE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK,
					Blocks.NETHER_GOLD_ORE, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB,
					Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_PORTAL, Blocks.NETHERITE_BLOCK,
					Blocks.BEACON, Blocks.BEDROCK, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS, Blocks.CRIMSON_PLANKS,
					Blocks.CHORUS_PLANT, Blocks.DARK_OAK_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.OAK_PLANKS,
					Blocks.SPRUCE_PLANKS, Blocks.WARPED_PLANKS, Blocks.SAND

			)), () -> true);

	@Override
	public void onEnable() {
		Helper.minecraftClient.worldRenderer.reload();
	}

	@Override
	public void onDisable() {
		Helper.minecraftClient.worldRenderer.reload();
	}

	@Override
	public void onPlayerTick() {
		Helper.minecraftClient.options.gamma = 68.5;
	}

	public boolean isValid(Block block1) {
		return !isEnabled() || block.getBlocks().contains(block1);
	}

}