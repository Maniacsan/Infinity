package org.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public interface IChunkDeltaUpdateS2CPacket {
	
	@Accessor("blockStates")
	BlockState[] getBlockStates();

}
