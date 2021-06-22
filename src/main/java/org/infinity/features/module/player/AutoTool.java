package org.infinity.features.module.player;

import org.infinity.event.ClickEvent;
import org.infinity.event.PacketEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Category.PLAYER, desc = "Automatically takes the desired tool when digging, attacking", key = -2, name = "AutoTool", visible = true)
public class AutoTool extends Module {

	private Setting destroyShield = new Setting(this, "Destroy Shield (Axe)", true);

	private int preSlot = -2;

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerActionC2SPacket) {
				PlayerActionC2SPacket p = (PlayerActionC2SPacket) event.getPacket();

				if (p.getAction() == Action.START_DESTROY_BLOCK) {
					if (Helper.getPlayer().isCreative() || Helper.getPlayer().isSpectator())
						return;

					int slot = getBestSlot(p.getPos());

					if (slot != Helper.getPlayer().getInventory().selectedSlot) {
						if (slot < 9) {
							Helper.getPlayer().getInventory().selectedSlot = slot;
							Helper.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
						}
					}
				}
			}
		}
	}

	@EventTarget
	public void onClick(ClickEvent event) {
		int slotAxe = InvUtil.findAxe();

		if (destroyShield.isToggle()) {

			Entity target = Helper.minecraftClient.targetedEntity;
			if (target instanceof PlayerEntity) {
				if (((PlayerEntity) target).isBlocking()) {
					if (slotAxe != -2) {
						preSlot = Helper.getPlayer().getInventory().selectedSlot;
						Helper.getPlayer().getInventory().selectedSlot = slotAxe;

						if (preSlot != -2) {
							(new Thread() {
								@Override
								public void run() {
									try {
										Thread.sleep(150);

										Helper.getPlayer().getInventory().selectedSlot = preSlot;
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

	@Override
	public void onPlayerTick() {

	}

	private int getBestSlot(BlockPos pos) {
		BlockState state = Helper.getWorld().getBlockState(pos);

		int bestSlot = Helper.getPlayer().getInventory().selectedSlot;

		if (state.isAir())
			return Helper.getPlayer().getInventory().selectedSlot;

		float bestSpeed = getMiningSpeed(Helper.getPlayer().getInventory().getStack(bestSlot), state);

		for (int slot = 0; slot < 36; slot++) {
			if (slot == Helper.getPlayer().getInventory().selectedSlot || slot == bestSlot)
				continue;

			ItemStack stack = Helper.getPlayer().getInventory().getStack(slot);

			float speed = getMiningSpeed(stack, state);
			if (speed > bestSpeed) {
				bestSpeed = speed;
				bestSlot = slot;
			}
		}

		return bestSlot;
	}

	private float getMiningSpeed(ItemStack stack, BlockState state) {
		float speed = stack.getMiningSpeedMultiplier(state);

		if (speed > 1) {
			int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
			if (efficiency > 0 && !stack.isEmpty())
				speed += efficiency * efficiency + 1;
		}

		return speed;
	}

}
