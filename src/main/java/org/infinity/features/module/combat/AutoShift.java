package org.infinity.features.module.combat;

import org.infinity.event.ClickEvent;
import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.mixin.IKeyBinding;
import org.infinity.utils.Helper;
import org.infinity.utils.PacketUtil;
import org.infinity.utils.PacketUtil.InteractType;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@ModuleInfo(category = Category.COMBAT, desc = "Sneaking on attack or hit", key = -2, name = "AutoShift", visible = true)
public class AutoShift extends Module {

	public Setting onlyAttack = new Setting(this, "Only on Attack", false);

	@Override
	public void onDisable() {

		KeyBinding.setKeyPressed(((IKeyBinding) Helper.MC.options.keySneak).getBoundKey(), false);
	}

	@EventTarget
	public void onClick(ClickEvent event) {
		if (onlyAttack.isToggle())
			return;
		shift();
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
				PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
				if (PacketUtil.getInteractType(packet) == InteractType.INTERACT_AT) {

					if (onlyAttack.isToggle())
						shift();
				}
			}
		}
	}

	public static void shift() {
		if (InfMain.getModuleManager().getModuleByClass(AutoShift.class).isEnabled()) {
			if (!Helper.getPlayer().isOnGround()) {

				(new Thread() {
					@Override
					public void run() {
						try {

							KeyBinding.setKeyPressed(
									((IKeyBinding) Helper.MC.options.keySneak).getBoundKey(), true);
							Thread.sleep(100);

							KeyBinding.setKeyPressed(
									((IKeyBinding) Helper.MC.options.keySneak).getBoundKey(), false);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}).start();
			}
		}

	}

}
