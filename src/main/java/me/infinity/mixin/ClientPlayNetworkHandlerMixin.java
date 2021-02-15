package me.infinity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.PacketEvent;
import me.infinity.features.command.Command;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Shadow
	@Final
	private ClientConnection connection;

	@Inject(at = @At("HEAD"), method = "sendPacket")
	public void sendPacket(Packet<?> packet, CallbackInfo call) {
		PacketEvent sendEvent = new PacketEvent(EventType.SEND, packet);
		EventManager.call(sendEvent);

		if (sendEvent.isCancelled()) {
			call.cancel();
			this.connection.send(sendEvent.getPacket());
		}
	}

}
