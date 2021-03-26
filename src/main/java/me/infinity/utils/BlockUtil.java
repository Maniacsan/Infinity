package me.infinity.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockUtil {

	public static BlockState getState(BlockPos pos) {
		return Helper.getWorld().getBlockState(pos);
	}

	public static Block getBlock(BlockPos pos) {
		return getState(pos).getBlock();
	}

	public static List<BlockEntity> getRenderBlocks(boolean chest, boolean enderChest, boolean spawners,
			boolean shulkers) {
		return StreamSupport.stream(Helper.minecraftClient.world.blockEntities.spliterator(), false)
				.filter(entity -> isBlockValid(entity, chest, enderChest, spawners, shulkers))
				.collect(Collectors.toList());
	}

	public static boolean isBlockValid(BlockEntity entity, boolean chest, boolean enderChest, boolean spawners,
			boolean shulkers) {

		if (chest && entity instanceof ChestBlockEntity)
			return true;

		if (enderChest && entity instanceof EnderChestBlockEntity)
			return true;

		if (spawners && entity instanceof MobSpawnerBlockEntity)
			return true;

		if (shulkers && entity instanceof ShulkerBoxBlockEntity)
			return true;

		return false;
	}

	public static int getBlockEntitiesColor(BlockEntity entity, int chest, int enderChest, int shulker, int spawner) {
		int color = -1;
		if (entity instanceof ChestBlockEntity)
			color = chest;
		if (entity instanceof EnderChestBlockEntity)
			color = enderChest;
		if (entity instanceof ShulkerBoxBlockEntity)
			color = shulker;
		if (entity instanceof MobSpawnerBlockEntity)
			color = spawner;
		return color;
	}

	public static boolean isFluid(BlockPos pos) {
		List<Material> fluids = Arrays.asList(Material.WATER, Material.LAVA);

		return fluids.contains(Helper.getWorld().getBlockState(pos).getMaterial());
	}

}
