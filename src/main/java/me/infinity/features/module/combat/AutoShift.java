package me.infinity.features.module.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.ClickEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IKeyBinding;
import me.infinity.utils.Helper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Sneaking on attack or hit", key = -2, name = "AutoShift", visible = true)
public class AutoShift extends Module {

	public Settings onlyAttack = new Settings(this, "Only on Attack", false, () -> true);

	@Override
	public void onDisable() {

		KeyBinding.setKeyPressed(((IKeyBinding) Helper.minecraftClient.options.keySneak).getBoundKey(), false);
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
			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket
					&& ((PlayerInteractEntityC2SPacket) event.getPacket())
							.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
				if (onlyAttack.isToggle()) {
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
									((IKeyBinding) Helper.minecraftClient.options.keySneak).getBoundKey(), true);
							Thread.sleep(100);

							KeyBinding.setKeyPressed(
									((IKeyBinding) Helper.minecraftClient.options.keySneak).getBoundKey(), false);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}).start();
			}
		}

	}

}
