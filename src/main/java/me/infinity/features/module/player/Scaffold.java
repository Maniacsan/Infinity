package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Placing block", key = -2, name = "Scaffold", visible = true)
public class Scaffold extends Module {

	private Settings eagle = new Settings(this, "Eagle", false, true);
	private Settings maxDelay = new Settings(this, "Max Delay", 200D, 0D, 500D, true);
	private Settings minDelay = new Settings(this, "Min Delay", 200D, 0D, 500D, true);

	private Settings speed = new Settings(this, "Speed", 80D, 0D, 180D, true);

	private TimeHelper timer = new TimeHelper();

	// raycast this shit
	public HitResult crosshairTarget;

	private float prevYaw;
	private float prevPitch;
	
	@Override
	public void onEnable() {
		timer.reset();
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			prevYaw = Helper.getPlayer().yaw;
			prevPitch = Helper.getPlayer().pitch;

			BlockPos pos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getBoundingBox().minY - 0.5,
					Helper.getPlayer().getZ());

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (calculatePosition()) {

					if (eagle.isToggle()) {
						Helper.minecraftClient.options.keySneak.setPressed(true);
					}

					// slot calculate
					int blockSlot = -2;
					for (int i = 0; i < 9; i++) {
						ItemStack stack = Helper.getPlayer().inventory.getStack(i);
						if (isBlock(stack.getItem())) {
							blockSlot = i;
						}
					}

					// rotation
					rotation(pos);

					// fucking raycast

					BlockHitResult blockHitResult = (BlockHitResult) Helper.minecraftClient.crosshairTarget;
					BlockPos placePos = blockHitResult.getBlockPos();
					// placing
					if (blockSlot != -2 && blockHitResult != null) {

						if (timer.hasReached(
								Math.random() * (maxDelay.getCurrentValueDouble() - minDelay.getCurrentValueDouble())
										+ minDelay.getCurrentValueDouble())) {

							int selectedSlot = Helper.getPlayer().inventory.selectedSlot;
							Helper.getPlayer().inventory.selectedSlot = blockSlot;

							if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos)) {
								Helper.getPlayer().inventory.selectedSlot = selectedSlot;
							}
							timer.reset();
							if (eagle.isToggle()) {
								Helper.minecraftClient.options.keySneak.setPressed(false);
							}
						}
					}
				}
			}

		} else if (event.getType().equals(EventType.POST)) {

		}
	}

	public void rotation(BlockPos pos) {
		int i = 1;
		BlockPos rotationPos = new BlockPos(
				Helper.getPlayer().getX() + (Helper.getPlayer().getHorizontalFacing() == Direction.WEST ? -i
						: Helper.getPlayer().getHorizontalFacing() == Direction.EAST ? i : 0),
				Helper.getPlayer().getY()
						- (Helper.getPlayer().getY() == (int) Helper.getPlayer().getY() + 0.5D ? 0D : 1.0D),
				Helper.getPlayer().getZ() + (Helper.getPlayer().getHorizontalFacing() == Direction.NORTH ? -i
						: Helper.getPlayer().getHorizontalFacing() == Direction.SOUTH ? i : 0));
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

		Vec3d rotationVec = new Vec3d(rotationPos.getX(), rotationPos.getY(), rotationPos.getZ());
		float[] toBlockRotation = RotationUtils.lookAtVecPos(rotationVec, (float) speed.getCurrentValueDouble(),
				(float) speed.getCurrentValueDouble());

		if (hitVec == null) {
			Helper.getPlayer().yaw = toBlockRotation[0];
			Helper.getPlayer().pitch = toBlockRotation[1];
			Helper.getPlayer().bodyYaw = toBlockRotation[0];
			Helper.getPlayer().headYaw = toBlockRotation[0];
		}

		if (neighbor == null)
			neighbor = pos;
		if (side2 == null)
			side2 = Direction.UP;

		float[] rotation = RotationUtils.lookAtVecPos(hitVec, (float) speed.getCurrentValueDouble(),
				(float) speed.getCurrentValueDouble());
		if (hitVec != null) {
			Helper.getPlayer().yaw = rotation[0];
			Helper.getPlayer().pitch = rotation[1];
			Helper.getPlayer().bodyYaw = rotation[0];
			Helper.getPlayer().headYaw = rotation[0];
		}
	}

	private boolean isBlock(Item item) {
		if (item instanceof BlockItem) {
			BlockItem itemBlock = (BlockItem) item;
			Block block = itemBlock.getBlock();
			return block != null;
		}
		return false;
	}

	private boolean calculatePosition() {
		// Spizjeno from PlayerEntity sneak stoping
		if (!Helper.getPlayer().abilities.flying) {
			double d = Helper.getPlayer().getX();
			double e = Helper.getPlayer().getZ();
			while (d != 0.0D && e != 0.0D && Helper.minecraftClient.world.isSpaceEmpty(Helper.getPlayer(),
					Helper.getPlayer().getBoundingBox().offset(d, (double) (-Helper.getPlayer().stepHeight), e))) {
				if (d < 0.05D && d >= -0.05D) {
					d = 0.0D;
				} else if (d > 0.0D) {
					d -= 0.05D;
				} else {
					d += 0.05D;
				}

				if (e < 0.05D && e >= -0.05D) {
					e = 0.0D;
				} else if (e > 0.0D) {
					e -= 0.05D;
				} else {
					e += 0.05D;
				}
				return true;
			}
		}
		return false;
	}


}
