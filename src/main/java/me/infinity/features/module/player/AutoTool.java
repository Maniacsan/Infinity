package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.InvUtil;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically takes the totem in the off hand", key = -2, name = "AutoTool", visible = true)
public class AutoTool extends Module {

	private Settings shieldCheck = new Settings(this, "Axe on target Shield", true, () -> true);

	private int lastSlot = -1;
	private int queueSlot = -1;

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (event.getPacket() instanceof PlayerActionC2SPacket) {
				PlayerActionC2SPacket p = (PlayerActionC2SPacket) event.getPacket();

				if (p.getAction() == Action.START_DESTROY_BLOCK) {
					if (Helper.getPlayer().isCreative() || Helper.getPlayer().isSpectator())
						return;

					queueSlot = -1;

					lastSlot = Helper.getPlayer().inventory.selectedSlot;

					int slot = getBestSlot(p.getPos());

					if (slot != Helper.getPlayer().inventory.selectedSlot) {
						if (slot < 9) {
							Helper.getPlayer().inventory.selectedSlot = slot;
							Helper.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
						} else if (Helper.getPlayer().playerScreenHandler == Helper.getPlayer().currentScreenHandler) {
							boolean itemInHand = !Helper.getPlayer().inventory.getMainHandStack().isEmpty();
							Helper.minecraftClient.interactionManager.clickSlot(
									Helper.getPlayer().currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP,
									Helper.getPlayer());
							Helper.minecraftClient.interactionManager.clickSlot(
									Helper.getPlayer().currentScreenHandler.syncId,
									36 + Helper.getPlayer().inventory.selectedSlot, 0, SlotActionType.PICKUP,
									Helper.getPlayer());

							if (itemInHand)
								Helper.minecraftClient.interactionManager.clickSlot(
										Helper.getPlayer().currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP,
										Helper.getPlayer());
						}
					}
				} else if (p.getAction() == Action.STOP_DESTROY_BLOCK) {
					if (queueSlot == Helper.getPlayer().inventory.selectedSlot) {
						queueSlot = Helper.getPlayer().inventory.selectedSlot == 0 ? 1
								: Helper.getPlayer().inventory.selectedSlot - 1;
					} else if (lastSlot >= 0 && lastSlot <= 8
							&& lastSlot != Helper.getPlayer().inventory.selectedSlot) {
						queueSlot = lastSlot;
					}
				}
			}
		}
	}

	@Override
	public void onPlayerTick() {

		if (queueSlot != -1) {
			Helper.getPlayer().inventory.selectedSlot = queueSlot;
			Helper.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(queueSlot));
			queueSlot = -1;
		}

		/*
		 * Shield check
		 */

		List<Item> collectedAxe = new ArrayList<>(Arrays.asList(Items.DIAMOND_AXE, Items.GOLDEN_AXE, Items.IRON_AXE,
				Items.NETHERITE_AXE, Items.STONE_AXE, Items.WOODEN_AXE));

		int slotAxe = -2;
		for (Item axe : collectedAxe) {
			slotAxe = InvUtil.findItemOnHotbar(axe);
		}

		int preSlot = -2;

		if (shieldCheck.isToggle()) {
			if (Helper.minecraftClient.options.keyAttack.isPressed()) {

				Entity target = Helper.minecraftClient.targetedEntity;
				if (target instanceof PlayerEntity) {
					if (((PlayerEntity) target).isBlocking()) {
						if (slotAxe != -2) {
							preSlot = Helper.getPlayer().inventory.selectedSlot;
							Helper.getPlayer().inventory.selectedSlot = slotAxe;
						}
					} else if (!((PlayerEntity) target).isBlocking()) {
						if (preSlot != -2)
							Helper.getPlayer().inventory.selectedSlot = preSlot;
					}
				}
			}
		}
	}

	private int getBestSlot(BlockPos pos) {
		BlockState state = Helper.getWorld().getBlockState(pos);

		int bestSlot = Helper.getPlayer().inventory.selectedSlot;

		bestSlot = bestSlot == 0 ? 1 : bestSlot - 1;

		if (state.isAir())
			return Helper.getPlayer().inventory.selectedSlot;

		float bestSpeed = getMiningSpeed(Helper.getPlayer().inventory.getStack(bestSlot), state);

		for (int slot = 0; slot < 36; slot++) {
			if (slot == Helper.getPlayer().inventory.selectedSlot || slot == bestSlot)
				continue;

			ItemStack stack = Helper.getPlayer().inventory.getStack(slot);

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
