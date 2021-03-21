package me.infinity.features.module.visual;

import java.awt.Color;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.render.WorldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Draw item esp", key = -2, name = "ItemESP", visible = true)
public class ItemESP extends Module {

	private Settings color = new Settings(this, "Color", new Color(143, 124, 241), () -> true);

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity e : Helper.getWorld().getEntities()) {

			if (e instanceof ItemEntity) {

				WorldRender.drawBox(e.getBoundingBox(), 1.5f, color.getColor().getRGB())	;

			}
		}

	}

}
