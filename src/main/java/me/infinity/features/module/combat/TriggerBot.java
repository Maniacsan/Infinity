package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IMinecraftClient;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.entity.EntityUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically hits when hovering over an entity", key = -2, name = "TriggerBot", visible = true)
public class TriggerBot extends Module {

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	private Settings destroyShield = new Settings(this, "Destroy Shield (Axe)", true, () -> true);

	public Settings range = new Settings(this, "Range", 3.7D, 0D, 6.0D, () -> true);

	private Settings coolDown = new Settings(this, "CoolDown", true, () -> true);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D, () -> Boolean.valueOf(!coolDown.isToggle()));

	private TimeHelper timer = new TimeHelper();

	private int preSlot = -2;

	@Override
	public void onPlayerTick() {
		// update target
		EntityUtil.updateTargetRaycast(Helper.minecraftClient.targetedEntity, range.getCurrentValueDouble(),
				Helper.getPlayer().yaw, Helper.getPlayer().pitch);

		if (EntityUtil.isTarget(Helper.minecraftClient.targetedEntity, players.isToggle(), friends.isToggle(),
				invisibles.isToggle(), mobs.isToggle(), animals.isToggle())) {
			if (Helper.minecraftClient.targetedEntity != null) {

				if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
						: timer.hasReached(1000 / aps.getCurrentValueDouble())) {
					if (Criticals.fall(Helper.minecraftClient.targetedEntity)) {
						destroyShield();

						((IMinecraftClient) MinecraftClient.getInstance()).mouseClick();

						Helper.getPlayer().resetLastAttackedTicks();
						timer.reset();
					}
				}
			}
		}
	}

	private void destroyShield() {
		int slotAxe = InvUtil.findAxe();

		if (destroyShield.isToggle()) {

			Entity target = Helper.minecraftClient.targetedEntity;
			if (target instanceof PlayerEntity) {
				if (((PlayerEntity) target).isBlocking()) {
					if (slotAxe != -2) {
						preSlot = Helper.getPlayer().inventory.selectedSlot;
						Helper.getPlayer().inventory.selectedSlot = slotAxe;

						if (preSlot != -2) {
							(new Thread() {
								@Override
								public void run() {
									try {
										Thread.sleep(110);

										Helper.getPlayer().inventory.selectedSlot = preSlot;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					}
				}
			}
		}
	}

}
