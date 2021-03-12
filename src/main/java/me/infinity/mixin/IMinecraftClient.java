package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

@Mixin(MinecraftClient.class)
public interface IMinecraftClient {

	@Accessor("itemUseCooldown")
	void setItemCooldown(int itemUseCooldown);

	@Accessor("session")
	void setSession(Session session);
	
	@Invoker("doAttack")
	void mouseClick();

}
