package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.PacketEvent;
import org.infinity.event.VelocityEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IEntityVelocityUpdateS2CPacket;
import org.infinity.mixin.IExplosionS2CPacket;
import org.infinity.utils.Helper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

@ModuleInfo(name = "Velocity", key = -2, visible = true, desc = "Anti knockback", category = Category.COMBAT)
public class Velocity extends Module {

	public Setting mode = new Setting(this, "Mode", "Matrix 6.1.0",
			new ArrayList<>(Arrays.asList("Packet", "Reverse", "Matrix 6.1.0")));

	private Setting vertical = new Setting(this, "Vertical",
			mode.getCurrentMode().equalsIgnoreCase("Reverse") ? 50D : 0.0D, 0.0D, 100.0D)
					.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
							|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));
	private Setting horizontal = new Setting(this, "Horizontal",
			mode.getCurrentMode().equalsIgnoreCase("Reverse") ? 50D : 0.0D, 0.0D, 100.0D)
					.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
							|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			Packet<?> packet = event.getPacket();
			if (packet instanceof EntityVelocityUpdateS2CPacket
					&& ((EntityVelocityUpdateS2CPacket) packet).getId() == Helper.getPlayer().getEntityId()) {
				EntityVelocityUpdateS2CPacket ep = (EntityVelocityUpdateS2CPacket) packet;
				double x = ep.getVelocityX() * horizontal.getCurrentValueDouble() / 100;
				double y = ep.getVelocityY() * vertical.getCurrentValueDouble() / 100;
				double z = ep.getVelocityZ() * horizontal.getCurrentValueDouble() / 100;

				switch (mode.getCurrentMode()) {
				case "Packet":
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityX((int) x);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityY((int) y);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityZ((int) z);
					break;

				case "Reverse":
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityX((int) -x);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityY((int) -y);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityZ((int) -z);
					break;

				case "Matrix 6.1.0":
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityX(0);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityY(0);
					((IEntityVelocityUpdateS2CPacket) ep).setVelocityZ(0);
					break;
				}
			}

			if (packet instanceof ExplosionS2CPacket) {
				ExplosionS2CPacket es = (ExplosionS2CPacket) packet;
				float x = (float) (es.getPlayerVelocityX() * horizontal.getCurrentValueDouble() / 100F);
				float y = (float) (es.getPlayerVelocityY() * vertical.getCurrentValueDouble() / 100F);
				float z = (float) (es.getPlayerVelocityZ() * horizontal.getCurrentValueDouble() / 100F);

				switch (mode.getCurrentMode()) {
				case "Packet":
					((IExplosionS2CPacket) es).setX(x);
					((IExplosionS2CPacket) es).setY(y);
					((IExplosionS2CPacket) es).setZ(z);
					break;

				case "Reverse":
					((IExplosionS2CPacket) es).setX(-x);
					((IExplosionS2CPacket) es).setY(-y);
					((IExplosionS2CPacket) es).setZ(-z);
					break;

				case "Matrix 6.1.0":
					((IExplosionS2CPacket) es).setX(0);
					((IExplosionS2CPacket) es).setY(0);
					((IExplosionS2CPacket) es).setZ(0);
					break;
				}
			}
		}
	}

	@EventTarget
	public void onSetVelocity(VelocityEvent event) {
		int x = (int) (event.getDefaultX() * (int) horizontal.getCurrentValueDouble() / 100);
		int y = (int) (event.getDefaultY() * (int) vertical.getCurrentValueDouble() / 100);
		int z = (int) (event.getDefaultZ() * (int) horizontal.getCurrentValueDouble() / 100);
		switch (mode.getCurrentMode()) {
		case "Packet":
			event.setX(x);
			event.setY(y);
			event.setZ(z);
			break;

		case "Reverse":
			event.setX(-x);
			event.setY(-y);
			event.setZ(-z);
			break;

		case "Matrix 6.1.0":
			event.setX(0);
			event.setY(0);
			event.setZ(0);
			break;
		}
	}

	/* EntityMixin action */
	public void pushAway(Entity e, Entity e2, CallbackInfo ci) {
		if (e != Helper.getPlayer() && e2 != Helper.getPlayer())
			return;

		double x = e2.getX() - e.getX();
		double z = e2.getZ() - e.getZ();
		double dist = Math.max(Math.abs(x), Math.abs(z));

		if (dist < 0.01)
			return;

		dist = Math.sqrt(dist);
		x /= dist;
		z /= dist;

		double multiplier = 1.0D / dist;
		if (multiplier > 1.0D) {
			multiplier = 1.0D;
		}

		double collisionReduction = 1.0f - e.pushSpeedReduction;

		x *= multiplier * 0.05 * collisionReduction;
		z *= multiplier * 0.05 * collisionReduction;

		addPushVelocity(e, Helper.getPlayer(), -x, -z);
		addPushVelocity(e2, Helper.getPlayer(), x, z);

		ci.cancel();
	}

	private void addPushVelocity(Entity e, ClientPlayerEntity player, double x, double z) {
		if (e.equals(player) && !player.isRiding()) {
			Entity entity = player;
			entity.setVelocity(entity.getVelocity().getX() + x, entity.getVelocity().getY(),
					entity.getVelocity().getZ() + z);
			player.velocityDirty = true;
		}
	}

}
