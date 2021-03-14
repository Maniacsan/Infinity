package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;

import me.infinity.InfMain;
import me.infinity.features.module.player.ChestSteal;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler>
		implements ScreenHandlerProvider<GenericContainerScreenHandler> {

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	public void init() {
		super.init();
		ChestSteal stealer = ((ChestSteal) InfMain.getModuleManager().getModuleByClass(ChestSteal.class));
		if (stealer.isEnabled()) {
			stealer.steal(handler);
		}
	}

}
