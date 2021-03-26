package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.AttackEvent;
import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.entity.PlayerSend;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Critical hit", key = GLFW.GLFW_KEY_B, name = "Criticals", visible = true)
public class Criticals extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList(new String[] { "Jump", "Packet", "Spoof", "Packet2" })), () -> true);

	private Settings delay = new Settings(this, "Delay", 3, 0, 20, () -> true);

	private Settings falling = new Settings(this, "Falling", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Jump"));

	private double y;
	private int stage;
	private int attackCount;

	// delay for attack
	private PlayerInteractEntityC2SPacket attackPacket;
	private HandSwingC2SPacket swingPacket;
	private boolean sendPackets;
	private int sendTimer;

	@Override
	public void onEnable() {
		sendPackets = false;
		swingPacket = null;
		attackPacket = null;
		sendTimer = 0;
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotion(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Spoof")) {
				if (attackCount > 0) {
					attackCount = 0;
				}
			}

			if (this.mode.getCurrentMode().equalsIgnoreCase("Packet")) {
				double ypos = Helper.getPlayer().getY();
				if (isOnGround(0.001D)) {
					PlayerSend.setOnGround(false);
					if (this.stage == 0) {
						this.y = ypos + 1.0E-8D;
						PlayerSend.setOnGround(true);
					} else if (this.stage == 1) {
						this.y -= 5.0E-15D;
					} else {
						this.y -= 4.0E-15D;
					}
					if (this.y <= Helper.getPlayer().getY()) {
						this.stage = 0;
						this.y = Helper.getPlayer().getY();
						PlayerSend.setOnGround(true);
					}
					PlayerSend.setY(this.y);

					this.stage++;
				} else {
					this.stage = 0;
				}

			}
		}
	}

	@EventTarget
	public void onAttack(AttackEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (this.mode.getCurrentMode().equalsIgnoreCase("Jump") && !falling.isToggle()) {
				Helper.getPlayer().jump();
			} else if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
				Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().getX(), 0.008D,
						Helper.getPlayer().getVelocity().getZ());
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (this.mode.getCurrentMode().equals("Packet2")) {
				if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
					PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
					if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
						if (event.getPacket() instanceof PlayerMoveC2SPacket)
							event.cancel();

						Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().getX(),
								Helper.getPlayer().getVelocity().getY() + 0.1, Helper.getPlayer().getVelocity().getZ());
						Helper.minecraftClient.getNetworkHandler()
								.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
										Helper.getPlayer().getY() + 0.1, Helper.getPlayer().getZ(), false));
						Helper.minecraftClient.getNetworkHandler()
								.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
										Helper.getPlayer().getY(), Helper.getPlayer().getZ(), false));
					}
				}
			} else if (mode.getCurrentMode().equalsIgnoreCase("Spoof")) {
				if (event.getPacket() instanceof PlayerInteractEntityC2SPacket
						&& ((PlayerInteractEntityC2SPacket) event.getPacket())
								.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {

					doJumpMode(event);
				} else if (event.getPacket() instanceof HandSwingC2SPacket) {
					doJumpModeSwing(event);
				}
			}
		}
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (sendPackets) {
			if (sendTimer <= 0) {
				sendPackets = false;

				if (attackPacket == null || swingPacket == null) {
					System.out.println("null");
					return;
				}
				Helper.minecraftClient.getNetworkHandler().sendPacket(attackPacket);
				Helper.minecraftClient.getNetworkHandler().sendPacket(swingPacket);

				attackPacket = null;
				swingPacket = null;
			} else {
				sendTimer--;
			}
		}
	}

	private void doJumpMode(PacketEvent event) {
		if (!sendPackets) {
			sendPackets = true;
			sendTimer = delay.getCurrentValueInt();
			attackPacket = (PlayerInteractEntityC2SPacket) event.getPacket();

			Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().getX(),
					Helper.getPlayer().getVelocity().getY() + 0.25, Helper.getPlayer().getVelocity().getZ());
			event.cancel();
		}
	}

	private void doJumpModeSwing(PacketEvent event) {
		if (sendPackets && swingPacket == null) {
			swingPacket = (HandSwingC2SPacket) event.getPacket();

			event.cancel();
		}
	}

	public static boolean fall() {
		Criticals criticals = ((Criticals) InfMain.getModuleManager().getModuleByClass(Criticals.class));
		// lul
		if (criticals.isEnabled() && criticals.mode.getCurrentMode().equalsIgnoreCase("Jump")
				&& criticals.falling.isToggle()) {
			if (Helper.getPlayer().fallDistance != 0 && !Helper.getPlayer().isOnGround()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private boolean isOnGround(double height) {
		if (!Helper.minecraftClient.world.isSpaceEmpty(Helper.getPlayer(),
				Helper.getPlayer().getBoundingBox().offset(0.0D, -height, 0.0D)))
			return true;
		return false;
	}

}
