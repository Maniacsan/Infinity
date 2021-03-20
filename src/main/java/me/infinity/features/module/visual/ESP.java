package me.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.RenderEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.render.WorldRender;
import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Draw entity esp", key = -2, name = "ESP", visible = true)
public class ESP extends Module {

	private Settings mode = new Settings(this, "Mode", "Fill", new ArrayList<>(Arrays.asList("Fill", "Box")),
			() -> true);

	private Settings width = new Settings(this, "Width", 2.0f, 0.5f, 3.0f,
			() -> mode.getCurrentMode().equalsIgnoreCase("Box"));

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", true, () -> players.isToggle());

	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);
	
	// colors
	private Settings playerColor = new Settings(this, "Player Color", new Color(247, 251, 247),
			() -> players.isToggle());
	private Settings friendsColor = new Settings(this, "Friends Color", new Color(247, 251, 247),
			() -> players.isToggle() && friends.isToggle());
	private Settings mobsColor = new Settings(this, "Mobs Color", new Color(236, 173, 24), () -> mobs.isToggle());
	private Settings animalsColor = new Settings(this, "Animals Color", new Color(108, 234, 42),
			() -> animals.isToggle());
	
	
	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}
	

	@EventTarget
	public void onWorldRender(RenderEvent event) {
		for (Entity e : EntityUtil.getRenderTargets(players.isToggle(), friends.isToggle(), invisibles.isToggle(),
				mobs.isToggle(), animals.isToggle())) {

			int color = EntityUtil.getEntitiesColor(e, playerColor.getColor().getRGB(),
					friendsColor.getColor().getRGB(), mobsColor.getColor().getRGB(), animalsColor.getColor().getRGB());

			
			if (mode.getCurrentMode().equalsIgnoreCase("Fill")) {
				WorldRender.drawFill(e.getBoundingBox(), color);

			} else if (mode.getCurrentMode().equalsIgnoreCase("Box")) {
				WorldRender.drawBox(e.getBoundingBox(), width.getCurrentValueFloat(), color);
				
			}
		}

	}
}
