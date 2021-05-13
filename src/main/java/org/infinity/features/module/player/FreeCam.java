package org.infinity.features.module.player;

import org.infinity.event.PacketEvent;
import org.infinity.event.PlayerMoveEvent;
import org.infinity.event.PushOutBlockEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.PacketUtil;
import org.infinity.utils.entity.OtherPlayer;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Category.PLAYER, desc = "Allows you to fly freely and explore the territory", key = -2, name = "FreeCam", visible = true)
public class FreeCam extends Module {

	private Setting speed = new Setting(this, "Speed", 0.8, 0, 2.0);

	private OtherPlayer spawnPlayer = null;

	private boolean prevFly;
	private float prevFlySpeed;

	private double x, y, z;
	private float yaw, pitch;

	@Override
	public void onEnable() {
		x = Helper.getPlayer().getX();
		y = Helper.getPlayer().getY();
		z = Helper.getPlayer().getZ();
		yaw = Helper.getPlayer().yaw;
		pitch = Helper.getPlayer().pitch;

		spawnPlayer = new OtherPlayer(Helper.getPlayer());
		spawnPlayer.setBoundingBox(spawnPlayer.getBoundingBox().expand(0.1));
		spawnPlayer.spawn();

		prevFly = Helper.getPlayer().abilities.flying;
		prevFlySpeed = Helper.getPlayer().abilities.getFlySpeed();
	}

	@Override
	public void onDisable() {
		if (spawnPlayer != null)
			spawnPlayer.despawn();
		spawnPlayer = null;

		Helper.getPlayer().abilities.flying = prevFly;
		Helper.getPlayer().abilities.setFlySpeed(prevFlySpeed);

		Helper.getPlayer().refreshPositionAndAngles(x, y, z, yaw, pitch);
		Helper.getPlayer().setVelocity(Vec3d.ZERO);
	}

	@Override
	public void onPlayerTick() {
		Helper.getPlayer().setOnGround(false);
		Helper.getPlayer().abilities.setFlySpeed((float) (speed.getCurrentValueDouble() / 5));
		Helper.getPlayer().abilities.flying = true;
	}

	@EventTarget
	public void onPushBlock(PushOutBlockEvent event) {
		// noclip to block fix
		event.cancel();
	}

	@EventTarget
	public void onClientMove(PlayerMoveEvent event) {
		Helper.getPlayer().noClip = true;
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		// cancelling real player packets
		PacketUtil.cancelMotionPackets(event);
		PacketUtil.cancelCommandPackets(event);
	}

}
