package me.infinity.features.module.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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

	private BlockPos renderPos;
	private BlockPos placePos;

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

		for (BlockPos pos : getSearchPos(Blocks.JUNGLE_LOG, range.getCurrentValueDouble())) {

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (pos == null)
					return;

				if (EntityUtil.distanceToBlock(pos) > Helper.minecraftClient.interactionManager.getReachDistance()) {
					KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyForward).getBoundKey(),
							true);
				} else if (EntityUtil.distanceToBlock(pos) <= Helper.minecraftClient.interactionManager
						.getReachDistance()) {
					KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyForward).getBoundKey(),
							false);
				}
				if (EntityUtil.distanceToBlock(pos) < 0.6) {
					KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyBack).getBoundKey(),
							true);
				} else if (EntityUtil.distanceToBlock(pos) >= 0.6) {
					KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyBack).getBoundKey(),
							false);
				}

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

	private List<BlockPos> getSearchPos(Block selectBlock, double range) {
		List<BlockPos> bPos = new ArrayList<>();
		for (int y = (int) 0; y < range; y++) {
			for (int x = (int) range; x >= -range; x--) {
				for (int z = (int) range; z >= -range; z--) {
					int posX = (int) ((Helper.getPlayer()).getX() - x);
					int posY = (int) ((Helper.getPlayer()).getY() + y);
					int posZ = (int) ((Helper.getPlayer()).getZ() - z);
					BlockPos pos = new BlockPos(posX, posY, posZ);
					if (BlockUtil.getBlock(pos.east()) == selectBlock || BlockUtil.getBlock(pos.north()) == selectBlock
							|| BlockUtil.getBlock(pos.south()) == selectBlock
							|| BlockUtil.getBlock(pos.west()) == selectBlock) {
						if (RotationUtils.isInFOVPos(pos, 360))
							bPos.add(pos);
					}

				}
			}
		}
		return bPos;
	}

}
