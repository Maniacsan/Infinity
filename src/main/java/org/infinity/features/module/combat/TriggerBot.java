package org.infinity.features.module.combat;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IMinecraftClient;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.Timer;
import org.infinity.utils.entity.EntityUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(category = Category.COMBAT, desc = "Automatically hits when hovering over an entity", key = -2, name = "TriggerBot", visible = true)
public class TriggerBot extends Module {

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", true);

	private Setting destroyShield = new Setting(this, "Destroy Shield (Axe)", true);

	public Setting range = new Setting(this, "Range", 3.7D, 0D, 6.0D);

	private Setting coolDown = new Setting(this, "CoolDown", true);
	private Setting aps = new Setting(this, "APS", 1.8D, 0.1D, 15.0D).setVisible(() -> !coolDown.isToggle());

	private Timer timer = new Timer();

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
