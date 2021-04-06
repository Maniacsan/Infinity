package me.infinity.features.module.visual;

import java.util.Arrays;
import java.util.List;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Lists;

import me.infinity.clickmenu.util.Render2D;
import me.infinity.event.RenderTooltipEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.Reflections;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

@ModuleInfo(category = Module.Category.VISUAL, desc = "See the contents of the shulker in the inventory", key = -2, name = "ShulkerView", visible = true)
public class ShulkerView extends Module {

	@EventTarget
	public void onRenderTooltip(RenderTooltipEvent event) {
		Slot slot = (Slot) Reflections.getFieldValue(event.getScreen(), "field_2787", "focusedSlot");
		if (slot == null)
			return;

		event.getMatrix().push();
		event.getMatrix().translate(0, 0, 300);

		draw(event, slot, event.getX(), event.getY());

		event.getMatrix().pop();
	}

	private void draw(RenderTooltipEvent event, Slot slot, int x, int y) {
		if (!(slot.getStack().getItem() instanceof BlockItem)) {
			return;
		}

		Block block = ((BlockItem) slot.getStack().getItem()).getBlock();

		if (!(block instanceof ShulkerBoxBlock) && !(block instanceof ChestBlock) && !(block instanceof BarrelBlock)
				&& !(block instanceof DispenserBlock) && !(block instanceof HopperBlock)
				&& !(block instanceof AbstractFurnaceBlock)) {
			return;
		}

		List<ItemStack> items = InvUtil.getContainerItems(slot.getStack());

		if (items.stream().allMatch(ItemStack::isEmpty)) {
			return;
		}

		event.setText(Lists.transform(Arrays.asList(slot.getStack().getName()), Text::asOrderedText));

		int count = block instanceof HopperBlock || block instanceof DispenserBlock
				|| block instanceof AbstractFurnaceBlock ? 18 : 0;

		for (ItemStack i : items) {
			if (count > 26) {
				break;
			}

			int x1 = x + 10 + (17 * (count % 9));
			int y1 = y - 67 + (17 * (count / 9));
			
			Render2D.drawRectWH(event.getMatrix(), x1 - 1, y1 - 1, 18, 18, 0xff2998E3);
			Render2D.drawRectWH(event.getMatrix(), x1, y1, 16, 16, 0xff555555);

			Helper.minecraftClient.getItemRenderer().zOffset = 400;
			Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(i, x1, y1);
			Helper.minecraftClient.getItemRenderer().renderGuiItemOverlay(Helper.minecraftClient.textRenderer, i, x1,
					y1, i.getCount() > 1 ? i.getCount() + "" : "");
			Helper.minecraftClient.getItemRenderer().zOffset = 300;
			count++;
		}
	}

}
