package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.InfMain;
import org.infinity.event.MotionEvent;
import org.infinity.event.PacketEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
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

@ModuleInfo(name = "Velocity", key = -2, visible = true, desc = "Anti knockback", category = Module.Category.COMBAT)
public class Velocity extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix 6.1.0",
			new ArrayList<>(Arrays.asList("Packet", "Reverse", "Matrix 6.1.0")), () -> true);

	private Settings vertical = new Settings(this, "Vertical",
			mode.getCurrentMode().equalsIgnoreCase("Reverse") ? 50D : 0.0D, 0.0D, 100.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
					|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));
	private Settings horizontal = new Settings(this, "Horizontal",
			mode.getCurrentMode().equalsIgnoreCase("Reverse") ? 50D : 0.0D, 0.0D, 100.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Packet")
					|| mode.getCurrentMode().equalsIgnoreCase("Reverse"));

	private EntityVelocityUpdateS2CPacket sVel;

	@Override
	public void onDisable() {
		sVel = null;
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {

	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			Packet<?> packet = event.getPacket();
			if (packet instanceof EntityVelocityUpdateS2CPacket) {
				EntityVelocityUpdateS2CPacket vp = (EntityVelocityUpdateS2CPacket) packet;
				if (vp.getId() == Helper.getPlayer().getEntityId()) {
					sVel = vp;

					int x = vp.getVelocityX() * (int) horizontal.getCurrentValueDouble() / 100;
					int y = vp.getVelocityY() * (int) vertical.getCurrentValueDouble() / 100;
					int z = vp.getVelocityZ() * (int) horizontal.getCurrentValueDouble() / 100;

					if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(x);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(y);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(z);

					} else if (mode.getCurrentMode().equalsIgnoreCase("Reverse")) {
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(-x);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(-y);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(-z);

					} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {

						((IEntityVelocityUpdateS2CPacket) vp).setVelocityX(0);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(vp.getVelocityY() * 0);
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityZ(0);
					}
				}
			} else if (packet instanceof ExplosionS2CPacket) {
				ExplosionS2CPacket es = (ExplosionS2CPacket) packet;
				double x = es.getX() * horizontal.getCurrentValueDouble() / 100;
				double y = es.getY() * vertical.getCurrentValueDouble() / 100;
				double z = es.getZ() * horizontal.getCurrentValueDouble() / 100;

				if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
					((IExplosionS2CPacket) es).setX(x);
					((IExplosionS2CPacket) es).setY(y);
					((IExplosionS2CPacket) es).setZ(z);

				} else if (mode.getCurrentMode().equalsIgnoreCase("Reverse")) {
					((IExplosionS2CPacket) es).setX(-x);
					((IExplosionS2CPacket) es).setY(-y);
					((IExplosionS2CPacket) es).setZ(-z);

				} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
					((IExplosionS2CPacket) es).setX(0);
					((IExplosionS2CPacket) es).setY(0);
					((IExplosionS2CPacket) es).setZ(0);

				}
			}
		}
	}

	/* EntityMixin action */
	public static void pushAway(Entity e, Entity e2, CallbackInfo ci) {
		if (InfMain.getModuleManager().getModuleByClass(Velocity.class).isEnabled()
				&& ((Velocity) InfMain.getModuleManager().getModuleByClass(Velocity.class)).mode.getCurrentMode()
						.equalsIgnoreCase("Matrix 6.1.0")) {

			if (e != Helper.getPlayer() && e2 != Helper.getPlayer())
				return;

			double x = e2.getX() - e.getX();
			double y = e2.getY() - e.getY();
			double z = e2.getZ() - e.getZ();
			double distY = Math.max(y, -y);
			double dist = Math.max(Math.abs(x), Math.abs(z));

			if (dist < 0.01)
				return;
			
			if (distY < 0.01)
				return;

			dist = Math.sqrt(dist);
			distY = Math.sqrt(distY);
			x /= dist;
			y /= distY;
			z /= dist;

			double multiplier = 1.0D / dist;
			if (multiplier > 1.0D) {
				multiplier = 1.0D;
			}
			
			double multiplierY = 1.0D / distY;
			if (multiplierY > 1.0D) {
				multiplierY = 1.0D;
			}
			
			double collisionReduction = 1.0f - e.pushSpeedReduction;

			x *= multiplier * 0.05 * collisionReduction;
			y *= multiplierY * 0.05 * collisionReduction;
			z *= multiplier * 0.05 * collisionReduction;

			addPushVelocity(e, Helper.getPlayer(), -x, -y, -z);
			addPushVelocity(e2, Helper.getPlayer(), x, y, z);

			ci.cancel();
		}
	}

	private static void addPushVelocity(Entity e, ClientPlayerEntity player, double x, double y, double z) {
		if (e.equals(player) && !player.isRiding()) {
			Entity entity = player;
			entity.setVelocity(entity.getVelocity().getX() + x, entity.getVelocity().getY() + y,
					entity.getVelocity().getZ() + z);
			player.velocityDirty = true;
		}
	}

}
