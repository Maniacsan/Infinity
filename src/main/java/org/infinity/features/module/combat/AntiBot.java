package org.infinity.features.module.combat;

import java.util.ArrayList;
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
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

@ModuleInfo(category = Category.COMBAT, desc = "Ignore / Remove npc by created anti-cheat ", key = -2, name = "AntiBot", visible = true)
public class AntiBot extends Module {

	private Setting invisible = new Setting(this, "Invisible", false);
	private Setting entityID = new Setting(this, "Entity ID", false);
	private Setting zeroHealth = new Setting(this, "Zero Health", true);
	private Setting addAction = new Setting(this, "Add Action", true);

	private Setting remove = new Setting(this, "Remove Bots", false);

	private List<Integer> invisibleBots = new ArrayList<>();
	private List<Integer> idBots = new ArrayList<>();
	private List<Integer> zeroBots = new ArrayList<>();
	private boolean wasAdded;

	@Override
	public void onDisable() {
		invisibleBots.clear();
		idBots.clear();
		zeroBots.clear();
	}

	@Override
	public void onPlayerTick() {
		for (Entity e : Helper.getWorld().getEntities()) {
			if (e instanceof PlayerEntity) {
				if (e.equals(Helper.getPlayer()))
					continue;

				if (e.isInvisible() && !invisibleBots.contains(e.getEntityId())) {
					invisibleBots.add(e.getEntityId());
					if (remove.isToggle() && invisible.isToggle()) {
						Helper.getWorld().removeEntity(e.getEntityId());
						message(e.getName().getString());
					}
				}

				if (e.getEntityId() >= 1000000000 && !idBots.contains(e.getEntityId())) {
					idBots.add(e.getEntityId());
					if (remove.isToggle() && entityID.isToggle()) {
						Helper.getWorld().removeEntity(e.getEntityId());
						message(e.getName().getString());
					}
				}

				if (((PlayerEntity) e).getHealth() <= 0 && !zeroBots.contains(e.getEntityId())) {
					zeroBots.add(e.getEntityId());
					if (remove.isToggle() && zeroHealth.isToggle()) {
						Helper.getWorld().removeEntity(e.getEntityId());
						message(e.getName().getString());
					}
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

		return false;
	}

	private void message(String bot) {
		if (remove.isToggle())
			Helper.infoMessage("Removed a bot: " + Formatting.BLUE + bot);
		else
			Helper.infoMessage("Bot detected: " + Formatting.BLUE + bot);
	}

}
