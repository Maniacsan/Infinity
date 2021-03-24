package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.entity.PlayerSend;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Placing block", key = -2, name = "Scaffold", visible = true)
public class Scaffold extends Module {

	private Settings blockTake = new Settings(this, "Block Take", "Switch",
			new ArrayList<>(Arrays.asList("Pick", "Switch")), () -> true);
	private Settings eagle = new Settings(this, "Eagle", false, () -> true);
	public Settings safeWalk = new Settings(this, "SafeWalk", true, () -> true);
	private Settings maxDelay = new Settings(this, "Max Delay", 200D, 0D, 500D, () -> true);
	private Settings minDelay = new Settings(this, "Min Delay", 200D, 0D, 500D, () -> true);

	private Settings rotation = new Settings(this, "Rotation", "Always",
			new ArrayList<>(Arrays.asList("Always", "Place")), () -> true);

	private Settings speed = new Settings(this, "Rotation Speed", 140D, 0D, 180D, () -> true);

	private TimeHelper timer = new TimeHelper();

	// target pos
	public static BlockPos pos;
	private float[] look;

	@Override
	public void onDisable() {
		if (Helper.minecraftClient.options.keySneak.isPressed())
			Helper.minecraftClient.options.keySneak.setPressed(false);

		pos = null;
	}

	@Override
	public void onPlayerTick() {

		look = rotation(pos);

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

			pos = new BlockPos(Helper.getPlayer().getX(), Helper.getPlayer().getBoundingBox().minY - 1,
					Helper.getPlayer().getZ());

			if (Helper.minecraftClient.world.getBlockState(pos).getBlock() == Blocks.AIR) {

				if (pos == null) {
					timer.reset();
					return;
				}

				// rotation
				PlayerSend.setRotation(look[0], look[1]);
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

						int selectedSlot = Helper.getPlayer().inventory.selectedSlot;
						Helper.getPlayer().inventory.selectedSlot = blockSlot;

						if (EntityUtil.placeBlock(Hand.MAIN_HAND, pos)) {
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
	public void onPacket(PacketEvent event) {
		PacketUtil.setRotation(event, look[0], look[1]);
		PacketUtil.fixSensitive(event);

		if (pos != null) {
			PacketUtil.cancelServerRotation(event);
		}
	}

	@SuppressWarnings("unused")
	public float[] rotation(BlockPos pos) {
		float yaw = Helper.getPlayer().yaw;
		float pitch = Helper.getPlayer().pitch;
		Vec3d hitVec = null;
		BlockPos neighbor = null;
		Direction side2 = null;
		Direction unitSide = null;
		for (Direction side : Direction.values()) {
			neighbor = pos.offset(side);
			side2 = side.getOpposite();
			unitSide = side;

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
		if (unitSide == null)
			unitSide = Direction.UP;
		if (side2 == null)
			side2 = Direction.UP;

		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d dirVec = new Vec3d(unitSide.getUnitVector());

		final Vec3d hitVec2 = hitVec.add(new Vec3d(dirVec.getX() * 0.5, dirVec.getY() * 0.5, dirVec.getZ() * 0.5));

		final double diffX = hitVec2.getX() - eyesPos.getX();
		final double diffY = hitVec2.getY() - eyesPos.getY();
		final double diffZ = hitVec2.getZ() - eyesPos.getZ();

		final double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
		if (rotation.getCurrentMode().equalsIgnoreCase("Always") && hitVec == null) {
			yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F);
			pitch = MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)));
		}

		float[] rotation = RotationUtils.lookAtVecPos(hitVec, (float) speed.getCurrentValueDouble(),
				(float) speed.getCurrentValueDouble());
		if (hitVec != null) {
			yaw = rotation[0];
			pitch = rotation[1];
		}
		return new float[] { yaw, pitch };
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
