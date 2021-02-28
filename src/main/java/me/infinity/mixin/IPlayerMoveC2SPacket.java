package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacket {
	
	@Accessor("yaw")
    void setYaw(float yaw);
	
	@Accessor("pitch")
    void setPitch(float yaw);

}
