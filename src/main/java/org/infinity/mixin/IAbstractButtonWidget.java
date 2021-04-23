package org.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Mixin(AbstractButtonWidget.class)
public interface IAbstractButtonWidget {
	
	@Accessor("focused")
	void setCustomFocused(boolean focused);

}
