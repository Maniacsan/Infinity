package org.infinity.features.module.visual;

import java.awt.Color;

import org.infinity.event.RenderEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.render.WorldRender;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

@ModuleInfo(category = Category.VISUAL, desc = "Draw item esp", key = -2, name = "ItemESP", visible = true)
public class ItemESP extends Module {

	private Setting color = new Setting(this, "Color", new Color(143, 124, 241));
	private Setting width = new Setting(this, "Width", 1.1f, 0.5f, 3.0f);

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity e : Helper.getWorld().getEntities()) {

			if (e instanceof ItemEntity) {
				WorldRender.drawBox(e.getBoundingBox(), width.getCurrentValueFloat(), color.getColor().getRGB());

			}
		}

	}

}
