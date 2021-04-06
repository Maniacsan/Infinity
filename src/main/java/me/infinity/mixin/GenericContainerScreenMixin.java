package me.infinity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.infinity.InfMain;
import me.infinity.features.module.player.ChestSteal;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler>
		implements ScreenHandlerProvider<GenericContainerScreenHandler> {

	private ChestSteal stealer = ((ChestSteal) InfMain.getModuleManager().getModuleByClass(ChestSteal.class));

	@Shadow
	@Final
	private int rows;

	private int mode;

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();

		if (stealer.isEnabled()) {
			steal();
		}
	}

	private void steal() {
		runInThread(() -> shiftClickSlots(0, rows * 9, 1));
	}

	private void runInThread(Runnable r) {
		new Thread(() -> {
			try {
				r.run();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void shiftClickSlots(int from, int to, int mode) {
		this.mode = mode;

		for (int i = from; i < to; i++) {
			Slot slot = handler.slots.get(i);
			if (slot.getStack().isEmpty())
				continue;

			waitForDelay();
			if (this.mode != mode || client.currentScreen == null)
				break;

			onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
		}
	}

	private void waitForDelay() {
		try {
			Thread.sleep((long) stealer.delay);

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
