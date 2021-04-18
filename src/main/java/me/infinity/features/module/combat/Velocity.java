package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IEntityVelocityUpdateS2CPacket;
import me.infinity.mixin.IExplosionS2CPacket;
import me.infinity.utils.Helper;
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
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.1.0")) {
			if (sVel != null) {
				event.setX(event.getX() + sVel.getVelocityX() / 8000);
				event.setY(event.getY() + sVel.getVelocityY() / 8000);
				event.setZ(event.getZ() + sVel.getVelocityZ() / 8000);
				sVel = null;
			}
		}
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
						((IEntityVelocityUpdateS2CPacket) vp).setVelocityY(0);
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
	}

	private static void addPushVelocity(Entity e, ClientPlayerEntity player, double x, double z) {
		if (e.equals(player) && !player.isRiding()) {
			Entity entity = player;
			entity.setVelocity(entity.getVelocity().add(x, 0, z));
			player.velocityDirty = true;
		}
	}

}
