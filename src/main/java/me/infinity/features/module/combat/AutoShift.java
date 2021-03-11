package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.AttackEvent;
import me.infinity.event.ClickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IKeyBinding;
import me.infinity.utils.Helper;
import me.infinity.utils.TimeHelper;
import net.minecraft.client.options.KeyBinding;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Sneaking on attack or hit", key = -2, name = "AutoShift", visible = true)
public class AutoShift extends Module {

	public Settings onlyAttack = new Settings(this, "Only on Attack", false, () -> true);
	private Settings method = new Settings(this, "Method", "PRE", new ArrayList<>(Arrays.asList("PRE", "POST")),
			() -> onlyAttack.isToggle());

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {

		Helper.minecraftClient.options.keySneak.setPressed(false);
	}

	@EventTarget
	public void onClick(ClickEvent event) {
		if (onlyAttack.isToggle())
			return;
		shift();
	}

	@EventTarget
	public void onAttack(AttackEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (onlyAttack.isToggle()) {
				if (method.getCurrentMode().equalsIgnoreCase("PRE")) {
					shift();
				}
			}
		} else if (event.getType().equals(EventType.POST)) {
			if (onlyAttack.isToggle()) {
				if (method.getCurrentMode().equalsIgnoreCase("POST")) {
					shift();
				}
			}
		}
	}

	public void shift() {
		KeyBinding.onKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keySneak).getBoundKey());
	}

}
