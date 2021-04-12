package me.infinity.features.module.movement;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "At the end of the block, you sit down and don't fall", key = -2, name = "Eagle", visible = true)
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

		if (Helper.minecraftClient.world.getBlockState(eaglePos).getBlock() != Blocks.AIR) {
			Helper.minecraftClient.options.keySneak.setPressed(false);
		} else {
			Helper.minecraftClient.options.keySneak.setPressed(true);
		}
	}

}
