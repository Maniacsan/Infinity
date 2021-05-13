package org.infinity.features.module.movement;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Category.MOVEMENT, desc = "At the end of the block, you sit down and don't fall", key = -2, name = "Eagle", visible = true)
public class Eagle extends Module {

	@Override
	public void onDisable() {
		if (Helper.minecraftClient.options.keySneak.isPressed())
			Helper.minecraftClient.options.keySneak.setPressed(false);
	}

	@Override
	public void onPlayerTick() {
		BlockPos eaglePos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getY() - 1,
				Helper.getPlayer().getZ());
		
		if (Helper.getPlayer().abilities.flying)
			return;

		if (Helper.minecraftClient.world.getBlockState(eaglePos).getBlock() != Blocks.AIR) {
			Helper.minecraftClient.options.keySneak.setPressed(false);
		} else {
			Helper.minecraftClient.options.keySneak.setPressed(true);
		}
	}

}
