package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Placing block", key = -2, name = "Scaffold", visible = true)
public class Scaffold extends Module {

	private Settings eagle = new Settings(this, "Eagle", false, () -> true);
	public Settings safeWalk = new Settings(this, "SafeWalk", true, () -> true);
	private Settings maxDelay = new Settings(this, "Max Delay", 200D, 0D, 500D, () -> true);
	private Settings minDelay = new Settings(this, "Min Delay", 200D, 0D, 500D, () -> true);

	private Settings speed = new Settings(this, "Speed", 140D, 0D, 180D, () -> true);

	private TimeHelper timer = new TimeHelper();

	// target pos
	public static BlockPos pos;

	private float prevYaw;
	private float prevPitch;

	@Override
	public void onDisable() {
		pos = null;
	}

	@Override
	public void onPlayerTick() {
		// eagle
		BlockPos eaglePos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getY() - 1,
				Helper.getPlayer().getZ());

		if (eagle.isToggle()) {
			if (Helper.minecraftClient.world.getBlockState(eaglePos).getBlock() != Blocks.AIR) {
				Helper.minecraftClient.options.keySneak.setPressed(false);
			} else {
				Helper.minecraftClient.options.keySneak.setPressed(true);
			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			prevYaw = Helper.getPlayer().yaw;
			prevPitch = Helper.getPlayer().pitch;

			pos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getBoundingBox().minY - 1,
					Helper.getPlayer().getZ());

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (pos == null) {
					timer.reset();
					return;
				}

				// slot calculate
				int blockSlot = -2;
				for (int i = 0; i < 9; i++) {
					ItemStack stack = Helper.getPlayer().inventory.getStack(i);
					if (isBlock(stack.getItem())) {
						blockSlot = i;
					}
				}

				// placing
				if (blockSlot != -2) {

					if (timer.hasReached(
							Math.random() * (maxDelay.getCurrentValueDouble() - minDelay.getCurrentValueDouble())
									+ minDelay.getCurrentValueDouble())) {

						int selectedSlot = Helper.getPlayer().inventory.selectedSlot;
						Helper.getPlayer().inventory.selectedSlot = blockSlot;

						if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos)) {
							pos = null;
							Helper.getPlayer().inventory.selectedSlot = selectedSlot;
						}
						timer.reset();
					}
				}
			}

		} else if (event.getType().equals(EventType.POST)) {
			// spoof rotation
			Helper.getPlayer().yaw = prevYaw;
			Helper.getPlayer().pitch = prevPitch;
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		float[] look = rotation(pos);
		PacketUtil.setRotation(event, look[0], look[1]);

		if (pos != null) {
			PacketUtil.cancelServerRotation(event);
		}
	}

	public float[] rotation(BlockPos pos) {
		Vec3d hitVec = null;
		BlockPos neighbor = null;
		Direction side2 = null;
		for (Direction side : Direction.values()) {
			neighbor = pos.offset(side);
			side2 = side.getOpposite();

			if (Helper.minecraftClient.world.getBlockState(neighbor).isAir()) {
				neighbor = null;
				side2 = null;
				continue;
			}

			hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ()).add(0.0, 0, 0.0)
					.add(new Vec3d(side2.getUnitVector()).multiply(0.65));
			break;
		}

		if (neighbor == null)
			neighbor = pos;
		if (side2 == null)
			side2 = Direction.UP;

		float[] rotation = RotationUtils.lookAtVecPos(hitVec, (float) speed.getCurrentValueDouble(),
				(float) speed.getCurrentValueDouble());
		if (hitVec != null) {
			return new float[] { rotation[0], rotation[1] };
		}
		return new float[] { Helper.getPlayer().yaw, Helper.getPlayer().pitch };
	}

	private boolean isBlock(Item item) {
		if (item instanceof BlockItem) {
			BlockItem itemBlock = (BlockItem) item;
			Block block = itemBlock.getBlock();
			return block != null;
		}
		return false;
	}

}
