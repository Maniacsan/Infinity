package org.infinity.features.module.visual;

import java.awt.Color;

import org.infinity.event.RenderEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;

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
