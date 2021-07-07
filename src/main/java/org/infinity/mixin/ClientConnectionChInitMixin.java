package org.infinity.mixin;

import org.infinity.via.handler.CommonTransformer;
import org.infinity.via.handler.FabricDecodeHandler;
import org.infinity.via.handler.FabricEncodeHandler;
import org.infinity.via.protocol.ViaFabricHostnameProtocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class ClientConnectionChInitMixin {

	@Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
	private void onInitChannel(Channel channel, CallbackInfo ci) {
		if (channel instanceof SocketChannel) {
			UserConnection user = new UserConnectionImpl(channel, true);
			new ProtocolPipelineImpl(user).add(ViaFabricHostnameProtocol.INSTANCE);

			channel.pipeline()
					.addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new FabricEncodeHandler(user))
					.addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new FabricDecodeHandler(user));
		}
	}
}
