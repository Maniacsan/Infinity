package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import io.netty.channel.ChannelHandlerContext;
import me.infinity.event.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(at = @At("HEAD"), method = "channelRead0")
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo call) {
		PacketEvent packetEvent = new PacketEvent(EventType.RECIEVE, packet);
		EventManager.call(packetEvent);

		if (packetEvent.isCancelled()) {
			call.cancel();
		}
	}

}
