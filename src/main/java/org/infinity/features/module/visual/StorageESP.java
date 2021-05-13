package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.block.BlockUtil;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;

@ModuleInfo(category = Category.VISUAL, desc = "Draw tile entity esp", key = -2, name = "StorageESP", visible = true)
public class StorageESP extends Module {

	private Setting mode = new Setting(this, "Mode", "Fill", new ArrayList<>(Arrays.asList("Fill", "Box")));

	// targets
	private Setting chest = new Setting(this, "Chests", true);

	private Setting enderChest = new Setting(this, "EnderChest", true);
	private Setting shulkers = new Setting(this, "Shulkers", true);
	private Setting spawners = new Setting(this, "Spawners", false);

	// colors
	private Setting chestColor = new Setting(this, "Chest Color", new Color(239, 244, 126))
			.setVisible(() -> chest.isToggle());
	private Setting enderChestColor = new Setting(this, "Ender Chest Color", new Color(229, 143, 223))
			.setVisible(() -> enderChest.isToggle());
	private Setting shulkersColor = new Setting(this, "Shulkers Color", new Color(143, 229, 209))
			.setVisible(() -> shulkers.isToggle());
	private Setting spawnersColor = new Setting(this, "Spawners Color", new Color(143, 243, 123))
			.setVisible(() -> spawners.isToggle());

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (BlockEntity blockEntity : BlockUtil.getRenderBlocks(chest.isToggle(), enderChest.isToggle(),
				spawners.isToggle(), shulkers.isToggle())) {

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
