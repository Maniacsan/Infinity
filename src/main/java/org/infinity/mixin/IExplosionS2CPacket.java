package org.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

@Mixin(ExplosionS2CPacket.class)
public interface IExplosionS2CPacket {
	
	@Accessor("x")
	void setX(double x);
	
	@Accessor("y")
	void setY(double y);
	
	@Accessor("z")
	void setZ(double z);

}
