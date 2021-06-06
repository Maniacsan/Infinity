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

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

@ModuleInfo(category = Category.COMBAT, desc = "Ignore / Remove npc by created anti-cheat ", key = -2, name = "AntiBot", visible = true)
public class AntiBot extends Module {

	public Setting mode = new Setting(this, "Mode", "Custom", new ArrayList<>(Arrays.asList("Custom", "Need Hit")));

	// Need Hit
	private Setting deleteHit = new Setting(this, "Delete on next hit", true)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Need Hit"));

	// Custom
	private Setting invisible = new Setting(this, "Invisible", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting entityID = new Setting(this, "Entity ID", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting zeroHealth = new Setting(this, "Zero Health", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting addAction = new Setting(this, "Add Action", true)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting invalidGround = new Setting(this, "Invalid Ground", true)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));
	private Setting remove = new Setting(this, "Remove Bots", false)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));

	private List<Integer> invisibleBots = new ArrayList<>();
	private List<Integer> idBots = new ArrayList<>();
	private List<Integer> zeroBots = new ArrayList<>();
	private List<Integer> iGround = new ArrayList<>();

	private List<Integer> needHit = new ArrayList<>();
	private boolean wasAdded;

	@Override
	public void onDisable() {
		invisibleBots.clear();
		idBots.clear();
		zeroBots.clear();
		iGround.clear();
		;
		needHit.clear();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (!mode.getCurrentMode().equalsIgnoreCase("Custom"))
			return;
		for (Entity e : Helper.getWorld().getEntities()) {
			if (e instanceof PlayerEntity) {
				PlayerEntity bot = (PlayerEntity) e;
				if (bot.equals(Helper.getPlayer()))
					continue;

				if (bot.isInvisible() && !invisibleBots.contains(bot.getEntityId()) && invisible.isToggle()) {
					invisibleBots.add(bot.getEntityId());
					message(bot.getName().getString());
					if (remove.isToggle())
						Helper.getWorld().removeEntity(bot.getEntityId());
				}

				if (bot.getEntityId() >= 1000000000 && !idBots.contains(bot.getEntityId()) && entityID.isToggle()) {
					idBots.add(bot.getEntityId());
					message(bot.getName().getString());
					if (remove.isToggle())
						Helper.getWorld().removeEntity(bot.getEntityId());
				}

				if (bot.getHealth() <= 0 && !zeroBots.contains(bot.getEntityId()) && zeroHealth.isToggle()) {
					zeroBots.add(bot.getEntityId());
					message(bot.getName().getString());
					if (remove.isToggle())
						Helper.getWorld().removeEntity(bot.getEntityId());
				}

				boolean swingContains = Helper.getPlayer().distanceTo(bot) > 15 && bot.canSee(Helper.getPlayer())
						&& bot.handSwinging && bot.getAttacking() == null && bot.canTarget(Helper.getPlayer())
						&& bot.getLastAttackedTime() == 0 && bot.age < 50;
				// From GishCode (Ground value)
				if (bot.getVelocity().getY() == 0.0 && !bot.verticalCollision && bot.isOnGround()
						&& bot.getY() % 0.5 != 0.0 && Helper.getPlayer().prevY != Helper.getPlayer().getY()
						&& swingContains && !iGround.contains(bot.getEntityId()) && invalidGround.isToggle()) {
					iGround.add(bot.getEntityId());
					message(bot.getName().getString());
					if (remove.isToggle())
						Helper.getWorld().removeEntity(bot.getEntityId());
				}

			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (!addAction.isToggle())
				return;

			if (event.getPacket() instanceof DifficultyS2CPacket)
				wasAdded = false;

			if (event.getPacket() instanceof PlayerListS2CPacket
					&& ((PlayerListS2CPacket) event.getPacket()).getAction().equals(Action.ADD_PLAYER)) {
				String bot = ((PlayerListS2CPacket) event.getPacket()).getEntries().get(0).getProfile().getName();
				if (!wasAdded)
					wasAdded = bot == Helper.getPlayer().getName().getString();
				if (wasAdded && !Helper.getPlayer().isSpectator() && !Helper.getPlayer().abilities.allowFlying
						&& ((PlayerListS2CPacket) event.getPacket()).getEntries().get(0)
								.getGameMode() != (GameMode.NOT_SET)
						&& !bot.equalsIgnoreCase(Helper.getPlayer().getName().getString())) {
					event.cancel();
					message(bot);
				}
			}
		} else if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
				PlayerInteractEntityC2SPacket pa = (PlayerInteractEntityC2SPacket) event.getPacket();
				if (pa.getEntity(Helper.getWorld()).equals(Helper.getPlayer())
						|| !mode.getCurrentMode().equalsIgnoreCase("Need Hit"))
					return;

				if (!needHit.contains(pa.getEntity(Helper.getWorld()).getEntityId())) {
					needHit.add(pa.getEntity(Helper.getWorld()).getEntityId());
					Helper.infoMessage(Formatting.GRAY + "[AntiBot] " + Formatting.WHITE
							+ pa.getEntity(Helper.getWorld()).getName().getString() + Formatting.GRAY
							+ " added to targets");
				} else if (deleteHit.isToggle() && needHit.contains(pa.getEntity(Helper.getWorld()).getEntityId())) {
					needHit.remove(pa.getEntity(Helper.getWorld()).getEntityId());
					Helper.infoMessage(Formatting.GRAY + "[AntiBot] " + Formatting.WHITE
							+ pa.getEntity(Helper.getWorld()).getName().getString() + Formatting.GRAY
							+ " removed from targets");
				}
			}
		}
	}

	public boolean isBot(Entity entity) {
		if (!isEnabled())
			return false;

		if (invisible.isToggle() && invisibleBots.contains(entity.getEntityId()))
			return true;

		if (entityID.isToggle() && idBots.contains(entity.getEntityId()))
			return true;

		if (zeroHealth.isToggle() && zeroBots.contains(entity.getEntityId()))
			return true;

		if (invalidGround.isToggle() && iGround.contains(entity.getEntityId()))
			return true;

		return false;
	}

	public boolean isHitted(Entity entity) {
		return needHit.contains(entity.getEntityId());
	}

	private void message(String bot) {
		if (remove.isToggle())
			Helper.infoMessage(
					Formatting.GRAY + "[AntiBot] " + Formatting.WHITE + "Removed a bot: " + Formatting.BLUE + bot);
		else
			Helper.infoMessage(
					Formatting.GRAY + "[AntiBot] " + Formatting.WHITE + "Bot detected: " + Formatting.BLUE + bot);
	}

}
