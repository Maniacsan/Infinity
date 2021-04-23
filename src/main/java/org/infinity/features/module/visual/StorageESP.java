package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.RenderEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.block.BlockUtil;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Draw tile entity esp", key = -2, name = "StorageESP", visible = true)
public class StorageESP extends Module {

	private Settings mode = new Settings(this, "Mode", "Fill", new ArrayList<>(Arrays.asList("Fill", "Box")),
			() -> true);

	// targets
	private Settings chest = new Settings(this, "Chests", true, () -> true);

	private Settings enderChest = new Settings(this, "EnderChest", true, () -> true);
	private Settings shulkers = new Settings(this, "Shulkers", true, () -> true);
	private Settings spawners = new Settings(this, "Spawners", false, () -> true);

	// colors
	private Settings chestColor = new Settings(this, "Chest Color", new Color(239, 244, 126), () -> chest.isToggle());
	private Settings enderChestColor = new Settings(this, "Ender Chest Color", new Color(229, 143, 223),
			() -> enderChest.isToggle());
	private Settings shulkersColor = new Settings(this, "Shulkers Color", new Color(143, 229, 209),
			() -> shulkers.isToggle());
	private Settings spawnersColor = new Settings(this, "Spawners Color", new Color(143, 243, 123),
			() -> spawners.isToggle());

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (BlockEntity blockEntity : BlockUtil.getRenderBlocks(chest.isToggle(), enderChest.isToggle(), spawners.isToggle(),
				shulkers.isToggle())) {

			int color = BlockUtil.getBlockEntitiesColor(blockEntity, chestColor.getColor().getRGB(),
					enderChestColor.getColor().getRGB(), shulkersColor.getColor().getRGB(),
					spawnersColor.getColor().getRGB());
			
			Box box = new Box(blockEntity.getPos());

			if (mode.getCurrentMode().equalsIgnoreCase("Fill")) {
				WorldRender.drawFill(box, color);
			} else if (mode.getCurrentMode().equalsIgnoreCase("Box")) {
				WorldRender.drawBox(box, 2, color);
			}

		}
	}

}
