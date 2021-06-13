package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.COMBAT, desc = "Ignore / Remove npc by created anti-cheat ", key = -2, name = "AntiBot", visible = true)
public class AntiBot extends Module {

	public Setting mode = new Setting(this, "Mode", "Matrix 6.1.1",
			new ArrayList<>(Arrays.asList("Matrix 6.1.1", "Custom", "Need Hit")));

	// Custom
	private Setting invisible = new Setting(this, "Invisible", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting entityID = new Setting(this, "Entity ID", true)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting zeroHealth = new Setting(this, "Zero Health", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting remove = new Setting(this, "Remove Bots", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));

	private List<Integer> bots = new ArrayList<>();
	private List<Integer> needHit = new ArrayList<>();

	@Override
	public void onDisable() {
		bots.clear();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (Helper.getPlayer() == null || Helper.getWorld() == null)
			return;

		for (Object object : Helper.getWorld().getEntities()) {
			if (object instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) object;
				if (entity instanceof PlayerEntity && !(entity instanceof ArmorStandEntity)
						&& entity != Helper.getPlayer()) {
					PlayerEntity bot = (PlayerEntity) entity;
					if (mode.getCurrentMode().equalsIgnoreCase("Custom")) {
						if (containsBot(bot)) {
							message(bot.getName().getString());

							if (remove.isToggle())
								Helper.getWorld().removeEntity(bot.getEntityId());
							bots.add(bot.getEntityId());
						}
					} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.1")) {
						boolean botContains = RotationUtil.isInFOV(bot, Helper.getPlayer(), 60)
								&& Helper.getPlayer().distanceTo(bot) > 9 && bot.handSwinging
								&& bot.getAttacking() == null && bot.canTarget(Helper.getPlayer()) && bot.age < 80;
						boolean speedAnalysis = bot.getStatusEffect(StatusEffects.SPEED) == null
								&& bot.getStatusEffect(StatusEffects.JUMP_BOOST) == null
								&& bot.getStatusEffect(StatusEffects.LEVITATION) == null && !bot.isTouchingWater()
								&& bot.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA
								&& !bot.hasVehicle() && EntityUtil.getSpeedBPS(bot) > 13 && !bot.isOnGround()
								&& !bot.velocityDirty;

						if (botContains && speedAnalysis) {
							Helper.getWorld().removeEntity(bot.getEntityId());

							message(bot.getName().getString());
						}
					}
				}
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (!mode.getCurrentMode().equalsIgnoreCase("Need Hit"))
				return;

			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
				PlayerInteractEntityC2SPacket pa = (PlayerInteractEntityC2SPacket) event.getPacket();
				if (pa.getEntity(Helper.getWorld()).equals(Helper.getPlayer()))
					return;

				if (!needHit.contains(pa.getEntity(Helper.getWorld()).getEntityId())) {
					needHit.add(pa.getEntity(Helper.getWorld()).getEntityId());
					Helper.infoMessage(Formatting.GRAY + "[AntiBot] " + Formatting.WHITE
							+ pa.getEntity(Helper.getWorld()).getName().getString() + Formatting.GRAY
							+ " added to targets");
				}
			}
		}
	}

	public boolean isBot(Entity entity) {
		if (!isEnabled())
			return false;

		if (bots.contains(entity.getEntityId()))
			return true;

		return false;
	}

	private boolean containsBot(PlayerEntity bot) {
		if (invisible.isToggle() && bot.isInvisible() && !bots.contains(bot.getEntityId()))
			return true;

		if (entityID.isToggle() && bot.getEntityId() >= 1000000000 && !bots.contains(bot.getEntityId()))
			return true;

		if (zeroHealth.isToggle() && bot.getHealth() <= 0 && !bots.contains(bot.getEntityId()))
			return true;

		return false;
	}

	public boolean isHitted(Entity entity) {
		return needHit.contains(entity.getEntityId());
	}

	private void message(String bot) {
		if (remove.isToggle() && mode.getCurrentMode().equalsIgnoreCase("Custom"))
			Helper.infoMessage(
					Formatting.GRAY + "[AntiBot] " + Formatting.WHITE + "Removed a bot: " + Formatting.BLUE + bot);
		else
			Helper.infoMessage(
					Formatting.GRAY + "[AntiBot] " + Formatting.WHITE + "Bot detected: " + Formatting.BLUE + bot);
	}

}
