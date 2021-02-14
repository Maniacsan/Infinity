package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

@Mixin(MinecraftClient.class)
public interface IMinecraftClient {
	
    @Accessor("session")
    void setSession(Session session);


}
