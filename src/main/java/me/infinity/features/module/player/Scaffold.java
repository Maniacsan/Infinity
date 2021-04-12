package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.RotationEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.block.BlockUtil;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Placing block", key = -2, name = "Scaffold", visible = true)
public class Scaffold extends Module {

	private Settings mode = new Settings(this, "Mode", "Normal", new ArrayList<>(Arrays.asList("Normal", "Safe")),
			() -> true);

	private Settings maxDelay = new Settings(this, "Max Delay", 200D, 0D, 500D,
			() -> true);
	private Settings minDelay = new Settings(this, "Min Delay", 200D, 0D, 500D,
			() -> true);

	private Settings blockTake = new Settings(this, "Block Take", "Switch",
			new ArrayList<>(Arrays.asList("Pick", "Switch")), () -> true);
	private Settings eagle = new Settings(this, "Eagle", false, () -> true);
	public Settings safeWalk = new Settings(this, "SafeWalk", true, () -> true);

	private Settings speed = new Settings(this, "Rotation Speed", 140D, 0D, 180D, () -> true);

	public Settings airPlace = new Settings(this, "Air Place", false, () -> true);

	private TimeHelper timer = new TimeHelper();

	private PlaceData pData;

	private float[] look;

	// target pos
	public static BlockPos pos;

	@Override
	public void onEnable() {
		timer.reset();
	}

	@Override
	public void onDisable() {
		if (Helper.minecraftClient.options.keySneak.isPressed())
			Helper.minecraftClient.options.keySneak.setPressed(false);

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

		if (mode.getCurrentMode().equalsIgnoreCase("Safe")) {
			MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.06);
		}
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (pos == null) {
			return;
		}

		look = rotation(pos);
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			pos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getY() - 1, Helper.getPlayer().getZ());
			
			PlaceData data = getPlaceData(pos);

			if (pData != null) {
				BlockPos lookPos = pData.pos;
				Vec3d vec = new Vec3d(lookPos.getX(), lookPos.getY(), lookPos.getZ());
				float[] alwaysL = RotationUtils.lookAtVecPos(vec, (float) speed.getCurrentValueDouble(),
						(float) speed.getCurrentValueDouble());
				event.setRotation(alwaysL[0], alwaysL[1]);
				Helper.getPlayer().bodyYaw = alwaysL[0];
				Helper.getPlayer().headYaw = alwaysL[0];
			}

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (pos == null) {
					timer.reset();
					return;
				}

				// rotation

				pData = data;

				event.setRotation(look[0], look[1]);
				Helper.getPlayer().bodyYaw = look[0];
				Helper.getPlayer().headYaw = look[0];

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

						if (!safe())
							return;
						
						if (Helper.minecraftClient.options.keyJump.isPressed() && Helper.getPlayer().fallDistance == 0) {
							return;
						}

						int selectedSlot = Helper.getPlayer().inventory.selectedSlot;
						Helper.getPlayer().inventory.selectedSlot = blockSlot;

						if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos, airPlace.isToggle())) {
							pos = null;
							if (blockTake.getCurrentMode().equalsIgnoreCase("Switch"))
								Helper.getPlayer().inventory.selectedSlot = selectedSlot;
						}
						timer.reset();
					}
				}
			}
		} else if (event.getType().equals(EventType.POST)) {

		}
	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("Safe")) {

			if (pos == null)
				return;

			event.setYaw(look[0]);
			event.setPitch(look[1]);
			event.cancel();
		}
	}

	public float[] rotation(BlockPos pos) {
		Vec3d eyesPos = new Vec3d(Helper.getPlayer().getX(),
				Helper.getPlayer().getY() + Helper.getPlayer().getEyeHeight(Helper.getPlayer().getPose()),
				Helper.getPlayer().getZ());

		Vec3d hitVec = null;
		BlockPos neighbor = null;
		Direction side2 = null;
		for (Direction side : Direction.values()) {
			neighbor = pos.offset(side);
			side2 = side.getOpposite();

			if (Helper.getWorld().getBlockState(neighbor).isAir()) {
				neighbor = null;
				side2 = null;
				continue;
			}

			hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ()).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getUnitVector()).multiply(0.5));
			break;
		}

		// Air place if no neighbour was found
		if (airPlace.isToggle() && hitVec == null)
			hitVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		if (neighbor == null)
			neighbor = pos;
		if (side2 == null)
			side2 = Direction.UP;

		// place block
		double diffX = hitVec.x - eyesPos.x;
		double diffY = hitVec.y - eyesPos.y;
		double diffZ = hitVec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[] { yaw, pitch };
	}

	private PlaceData getPlaceData(BlockPos pos) {
		if (BlockUtil.canBeClicked(pos.add(0, -1, 0)))
			return new PlaceData(pos.add(0, -1, 0), Direction.UP);
		if (BlockUtil.canBeClicked(pos.add(0, 0, 1)))
			return new PlaceData(pos.add(0, 0, 1), Direction.NORTH);
		if (BlockUtil.canBeClicked(pos.add(-1, 0, 0)))
			return new PlaceData(pos.add(-1, 0, 0), Direction.EAST);
		if (BlockUtil.canBeClicked(pos.add(0, 0, -1)))
			return new PlaceData(pos.add(0, 0, -1), Direction.SOUTH);
		if (BlockUtil.canBeClicked(pos.add(1, 0, 0)))
			return new PlaceData(pos.add(1, 0, 0), Direction.WEST);

		return null;
	}

	private boolean safe() {
		if (mode.getCurrentMode().equalsIgnoreCase("Safe")) {
			if (Helper.minecraftClient.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean isBlock(Item item) {
		if (item instanceof BlockItem) {
			BlockItem itemBlock = (BlockItem) item;
			Block block = itemBlock.getBlock();
			return block != null;
		}
		return false;
	}

	public class PlaceData {

		public BlockPos pos;
		public Direction side;

		public PlaceData(BlockPos pos, Direction side) {
			this.pos = pos;
			this.side = side;
		}
	}

}
