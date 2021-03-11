package me.infinity.mixin;

import javax.swing.text.JTextComponent.KeyBinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.util.InputUtil;

@Mixin(KeyBinding.class)
public interface IKeyBinding {

	// fucking mixinsssss
	@Accessor
	InputUtil.Key getBoundKey();

}
