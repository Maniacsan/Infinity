package org.infinity.features.module.visual;

import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.PacketUtil;

import com.darkmagician6.eventapi.EventTarget;

@ModuleInfo(category = Category.VISUAL, desc = "Removes packet rotations leaving only your movements", key = -2, name = "NoServerRotation", visible = true)
public class NoServerRotation extends Module {

	@EventTarget
	public void onPacket(PacketEvent event) {
		PacketUtil.cancelServerRotation(event);
	}

}
