package me.infinity.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

public class ItemHelper {

	public static ArrayList<Block> getBlockList() {
		ArrayList<Block> list = new ArrayList<>();
		for (Block blocks : Registry.BLOCK) {
			list.add(blocks);
		}
		return list;
	}

}
