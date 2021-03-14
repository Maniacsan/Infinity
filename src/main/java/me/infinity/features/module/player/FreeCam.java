package me.infinity.features.module.player;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.PacketEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.entity.OtherPlayer;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Allows you to fly freely and explore the territory", key = -2, name = "FreeCam", visible = true)
public class FreeCam extends Module {

	private Settings speed = new Settings(this, "Speed", 1.5, 0, 3.0, () -> true);

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
