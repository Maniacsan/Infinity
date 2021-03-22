package me.infinity.features.module.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IKeyBinding;
import me.infinity.utils.BlockUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.render.WorldRender;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.WORLD, desc = "Auto farms the necessary resources", key = -2, name = "AutoFarm", visible = true)
public class AutoFarm extends Module {

	private Settings beans = new Settings(this, "Cocoa Beans", true, () -> true);
	private Settings range = new Settings(this, "Range", 15.0D, 0.0D, 25.0D, () -> true);
	private Settings delay = new Settings(this, "Delay", 5.0D, 0.0D, 15.0D, () -> true);

	private Settings esp = new Settings(this, "ESP", true, () -> true);
	private Settings color = new Settings(this, "ESP Color", new Color(17, 223, 161), () -> esp.isToggle());

	private BlockPos renderPos;;

	private final HashMap<BlockPos, Item> plants = new HashMap<>();

	private int time;

	@Override
	public void onDisable() {
		KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyForward).getBoundKey(), false);
		renderPos = null;
		time = 0;
	}

	@Override
	public void onPlayerTick() {
		if (!beans.isToggle())
			return;

		int beansSlot = InvUtil.findItemOnHotbar(Items.COCOA_BEANS);

		Vec3d eyesVec = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
		double rangeSq = Math.pow(range.getCurrentValueDouble(), 2);
		int blockRange = (int) Math.ceil(range.getCurrentValueDouble());

		List<BlockPos> blocks = getBlockStream(eyesBlock, blockRange)
				.filter(pos -> eyesVec.squaredDistanceTo(Vec3d.of(pos)) <= rangeSq).collect(Collectors.toList());

		HashMap<Block, Item> seeds = new HashMap<>();
		seeds.put(Blocks.COCOA, Items.COCOA_BEANS);

		blocks.parallelStream().filter(pos -> seeds.containsKey(BlockUtil.getBlock(pos)));

		for (BlockPos pos : getSearchPos(Blocks.JUNGLE_LOG, range.getCurrentValueDouble())) {

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (pos == null)
					return;

				Vec3d posVec = new Vec3d(pos.getX(), pos.getY() - 1, pos.getZ());
				float[] rotation = RotationUtils.lookAtVecPos(posVec, 145, 145);
				Helper.getPlayer().yaw = rotation[0];
				Helper.getPlayer().pitch = rotation[1];

				if (beansSlot != -2)
					Helper.getPlayer().inventory.selectedSlot = beansSlot;

				renderPos = pos;

				if (time > 0) {
					time--;
					return;
				}

				if (EntityUtil.distanceToBlock(pos) <= Helper.minecraftClient.interactionManager.getReachDistance()) {
					if (Helper.getPlayer().getMainHandStack().getItem() == Items.COCOA_BEANS) {
						if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos)) {
							time = (int) delay.getCurrentValueDouble();
							renderPos = null;
						}
					}
				}
			}
		}
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		if (esp.isToggle()) {
			if (EntityUtil.distanceToBlock(renderPos) <= Helper.minecraftClient.interactionManager.getReachDistance()) {
				if (renderPos != null)
					WorldRender.drawFill(renderPos, color.getColor().getRGB());
			}
		}
	}

	private void registerPlants(List<BlockPos> blocks) {
		HashMap<Block, Item> seeds = new HashMap<>();
		seeds.put(Blocks.COCOA, Items.COCOA_BEANS);

		plants.putAll(blocks.parallelStream().filter(pos -> seeds.containsKey(BlockUtil.getBlock(pos)))
				.collect(Collectors.toMap(pos -> pos, pos -> seeds.get(BlockUtil.getBlock(pos)))));
	}

	private Stream<BlockPos> getBlockStream(BlockPos center, int range) {
		BlockPos min = center.add(-range, -range, -range);
		BlockPos max = center.add(range, range, range);

		return BlockUtil.getAllInBox(min, max).stream();
	}

	private List<BlockPos> getSearchPos(Block selectBlock, double range) {
		List<BlockPos> bPos = new ArrayList<>();
		for (int y = (int) 0; y < range; y++) {
			for (int x = (int) range; x >= -range; x--) {
				for (int z = (int) range; z >= -range; z--) {
					int posX = (int) ((Helper.getPlayer()).getX() - x);
					int posY = (int) ((Helper.getPlayer()).getY() + y);
					int posZ = (int) ((Helper.getPlayer()).getZ() - z);
					BlockPos pos = new BlockPos(posX, posY, posZ);
					if (BlockUtil.getBlock(pos.offset(Helper.getPlayer().getHorizontalFacing())) == selectBlock)
						bPos.add(pos);

				}
			}
		}
		return bPos;
	}

}
