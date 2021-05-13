package org.infinity.features.module.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IKeyBinding;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.block.BlockUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.render.WorldRender;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Category.WORLD, desc = "Auto farms the necessary resources", key = -2, name = "AutoFarm", visible = true)
public class AutoFarm extends Module {

	private Setting beans = new Setting(this, "Cocoa Beans", true);
	private Setting range = new Setting(this, "Range", 15.0D, 0.0D, 25.0D);
	private Setting delay = new Setting(this, "Delay", 5.0D, 0.0D, 15.0D);

	private Setting esp = new Setting(this, "ESP", true);
	private Setting color = new Setting(this, "ESP Color", new Color(17, 223, 161)).setVisible(() -> esp.isToggle());

	private BlockPos renderPos;

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

				move(pos);

				Vec3d posVec = new Vec3d(pos.getX(), pos.getY() - 1, pos.getZ());
				float[] rotation = RotationUtil.lookAtVecPos(posVec);
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
						if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos, false)) {
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

	private void move(BlockPos pos) {
		if (EntityUtil.distanceToBlock(pos) > Helper.minecraftClient.interactionManager.getReachDistance()) {
			KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyForward).getBoundKey(), true);
		} else if (EntityUtil.distanceToBlock(pos) <= Helper.minecraftClient.interactionManager.getReachDistance()) {
			KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyForward).getBoundKey(), false);
		}
		if (EntityUtil.distanceToBlock(pos) < 0.6) {
			KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyBack).getBoundKey(), true);
		} else if (EntityUtil.distanceToBlock(pos) >= 0.6) {
			KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keyBack).getBoundKey(), false);
		}
	}

	private List<BlockPos> getSearchPos(Block selectBlock, double range) {
		List<BlockPos> bPos = new ArrayList<>();
		for (int y = (int) 0; y < range; y++) {
			for (int x = (int) range; x >= -range; x--) {
				for (int z = (int) range; z >= -range; z--) {
					BlockPos pos = new BlockPos(x, y, z);
					Vec3d vecPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
					if (BlockUtil.getBlock(new BlockPos(vecPos)) == Blocks.JUNGLE_LOG) {
						bPos.add(pos);
					}

				}
			}
		}
		return bPos;
	}
}
