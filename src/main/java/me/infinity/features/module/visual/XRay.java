package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.block.BlockUtil;
import me.infinity.utils.render.WorldRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@ModuleInfo(category = Module.Category.VISUAL, desc = "View ore blocks", key = GLFW.GLFW_KEY_X, name = "XRay", visible = true)
public class XRay extends Module {

	private ArrayList<Block> blocks = new ArrayList<>();

	private ArrayList<BlockPos> oreBlocks = new ArrayList<>();
	private ArrayList<BlockPos> clickedBlocks = new ArrayList<>();
	private ArrayList<BlockPos> renderBlocks = new ArrayList<>();

	// orebfuscator
	private Settings orebfuscator = new Settings(this, "Orebfuscator", false, () -> true);
	private Settings radius = new Settings(this, "Radius", 10.0D, 1.0D, 50.0D, () -> orebfuscator.isToggle());
	private Settings up = new Settings(this, "Up Distance", 10.0D, 1.0D, 50.0D, () -> orebfuscator.isToggle());
	private Settings down = new Settings(this, "Down Distance", 10.0D, 1.0D, 50.0D, () -> orebfuscator.isToggle());
	private Settings info = new Settings(this, "Info", false, () -> orebfuscator.isToggle());

	public Settings block = new Settings(this, "Blocks", blocks,
			new ArrayList<Block>(Arrays.asList(Blocks.DIAMOND_ORE, Blocks.COAL_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE,
					Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
					Blocks.REDSTONE_ORE, Blocks.ORANGE_CONCRETE, Blocks.STONE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK,
					Blocks.NETHER_GOLD_ORE, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB,
					Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_PORTAL, Blocks.NETHERITE_BLOCK,
					Blocks.BEACON, Blocks.BEDROCK, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS, Blocks.CRIMSON_PLANKS,
					Blocks.CHORUS_PLANT, Blocks.DARK_OAK_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.OAK_PLANKS,
					Blocks.SPRUCE_PLANKS, Blocks.WARPED_PLANKS, Blocks.SAND

			)), () -> true);

	private TimeHelper updater = new TimeHelper();

	private BlockPos currentBlock = new BlockPos(-1, -1, -1);
	private float breakProgress;
	private boolean hitBlock;

	private int findTimer;

	@Override
	public void onEnable() {
		Helper.minecraftClient.worldRenderer.reload();

		findTimer = 0;
		if (!clickedBlocks.isEmpty())
			clickedBlocks.clear();
	}

	@Override
	public void onDisable() {
		clickedBlocks.clear();
		oreBlocks.clear();
		renderBlocks.clear();
		findTimer = 0;

		Helper.minecraftClient.worldRenderer.reload();
	}

	@Override
	public void onPlayerTick() {
		if (orebfuscator.isToggle()) {
			if (oreBlocks.isEmpty()) {

				if (updater.hasReached(3000)) {
					for (BlockPos pos : BlockUtil.getAllInBox(
							(int) (Helper.getPlayer().getPos().x - radius.getCurrentValueDouble()),
							(int) (Helper.getPlayer().getPos().y - down.getCurrentValueDouble()),
							(int) (Helper.getPlayer().getPos().z - radius.getCurrentValueDouble()),
							(int) (Helper.getPlayer().getPos().x + radius.getCurrentValueDouble()),
							(int) (Helper.getPlayer().getPos().y + up.getCurrentValueDouble()),
							(int) (Helper.getPlayer().getPos().z + radius.getCurrentValueDouble()))) {

						for (Block valid : block.getBlocks()) {
							if (BlockUtil.getBlock(pos) == valid && !clickedBlocks.contains(pos)) {
								oreBlocks.add(pos);
							}
						}
					}
					updater.reset();
				}
			}
		}

		if (findTimer > 0) {
			findTimer--;
			return;
		}

		if (!clickedBlocks.isEmpty()) {
			for (BlockPos clicked : clickedBlocks) {

				for (Block valid : block.getBlocks()) {
					if (BlockUtil.getBlock(clicked) == valid) {
						if (!renderBlocks.contains(clicked)) {

							if (info.isToggle())
								Helper.infoMessage("Finded " + Formatting.AQUA + valid.getName().getString()
										+ Formatting.WHITE + " - x:" + Formatting.GRAY + clicked.getX()
										+ Formatting.WHITE + " y:" + Formatting.GRAY + clicked.getY() + Formatting.WHITE
										+ " z:" + Formatting.GRAY + clicked.getZ());

							renderBlocks.add(clicked);
						}
					}
				}
			}
		}

	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			for (BlockPos pos : oreBlocks) {
				clickBlock(pos, Helper.getPlayer().getHorizontalFacing());
				oreBlocks.remove(pos);
				// System.out.println(clickedBlocks.size());
				clickedBlocks.add(pos);
				findTimer = 10;
			}
		}
	}

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		if (renderBlocks.isEmpty())
			return;

		for (BlockPos renderPos : renderBlocks) {

			WorldRender.drawBox(renderPos, 2, 0xff6BE979);
		}
	}

	private boolean clickBlock(BlockPos pos, Direction direction) {

		BlockState blockState2;

		if (Helper.getPlayer().isCreative()) {
			blockState2 = Helper.getWorld().getBlockState(pos);
			Helper.minecraftClient.getTutorialManager().onBlockAttacked(Helper.getWorld(), pos, blockState2, 1.0F);
			Helper.sendPacket(
					new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));

		} else if (!this.hitBlock) {
			if (this.hitBlock) {
				Helper.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
						this.currentBlock, direction));
			}

			blockState2 = Helper.getWorld().getBlockState(pos);
			Helper.minecraftClient.getTutorialManager().onBlockAttacked(Helper.getWorld(), pos, blockState2, 0.0F);
			Helper.sendPacket(
					new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
			boolean bl = !blockState2.isAir();
			if (bl && this.breakProgress == 0.0F) {
				blockState2.onBlockBreakStart(Helper.getWorld(), pos, Helper.getPlayer());
			}

			breakProgress = 0.0f;
			hitBlock = true;
			this.currentBlock = pos;
		}

		return true;
	}

	public boolean isValid(Block block1) {
		return !isEnabled() || block.getBlocks().contains(block1);
	}

}