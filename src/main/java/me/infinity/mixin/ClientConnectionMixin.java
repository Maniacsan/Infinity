package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.infinity.InfMain;
import me.infinity.event.PacketEvent;
import me.infinity.features.command.Command;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

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

	@Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
	public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1,
			CallbackInfo callback) {
		if (packet instanceof ChatMessageC2SPacket) {
			ChatMessageC2SPacket chatPacket = (ChatMessageC2SPacket) packet;
			if (chatPacket.getChatMessage().startsWith(Command.prefix)) {
				InfMain.getCommandManager().callCommand(chatPacket.getChatMessage().substring(Command.prefix.length()));
				callback.cancel();
			}
		}

		PacketEvent sendEvent = new PacketEvent(EventType.SEND, packet);
		EventManager.call(sendEvent);

		if (sendEvent.isCancelled()) {
			callback.cancel();
		}
	}

}
