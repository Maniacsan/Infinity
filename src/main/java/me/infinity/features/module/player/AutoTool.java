package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.ClickEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
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
	private int preSlot = -2;

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

	@EventTarget
	public void onClick(ClickEvent event) {
		int slotAxe = findAxe();

		if (shieldCheck.isToggle()) {

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
										Thread.sleep(120);

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

	@Override
	public void onPlayerTick() {

		if (queueSlot != -1) {
			Helper.getPlayer().inventory.selectedSlot = queueSlot;
			Helper.getPlayer().networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(queueSlot));
			queueSlot = -1;
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

	private int findAxe() {
		int find = -2;
		for (int i = 0; i <= 8; i++)
			if (Helper.getPlayer().inventory.getStack(i).getItem() instanceof AxeItem)
				find = i;
		return find;
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
