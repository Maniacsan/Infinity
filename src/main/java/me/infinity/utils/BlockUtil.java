package me.infinity.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockUtil {
	
	public static BlockState getState(BlockPos pos)
	{
		return Helper.getWorld().getBlockState(pos);
	}
	
	public static Block getBlock(BlockPos pos)
	{
		return getState(pos).getBlock();
	}
	

	public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
		ArrayList<BlockPos> blocks = new ArrayList<>();

		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()),
				Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()),
				Math.max(from.getZ(), to.getZ()));

		for (int x = min.getX(); x <= max.getX(); x++)
			for (int y = min.getY(); y <= max.getY(); y++)
				for (int z = min.getZ(); z <= max.getZ(); z++)
					blocks.add(new BlockPos(x, y, z));

		return blocks;
	}

}
