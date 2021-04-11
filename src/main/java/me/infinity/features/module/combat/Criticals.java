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
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Critical hit", key = GLFW.GLFW_KEY_B, name = "Criticals", visible = true)
public class Criticals extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList(new String[] { "Jump", "Packet", "Spoof", "Sentiel" })), () -> true);

	private Settings falling = new Settings(this, "Falling", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Jump"));
	private Settings fallDistance = new Settings(this, "Fall Distance", 0.4, 0.15, 0.42,
			() -> mode.getCurrentMode().equalsIgnoreCase("Jump") && falling.isToggle());

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
		attackCount = 0;
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget()
	public void onMotion(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			double yOff = MathAssist.random(0.08, 0.12);
			if (this.mode.getCurrentMode().equalsIgnoreCase("Spoof")) {
				if (attackCount > 0) {
					double ypos = Helper.getPlayer().getY();
					if (Helper.getPlayer().isOnGround()) {
						event.setOnGround(false);
						if (this.stage == 0) {
							this.y = ypos + 0.1;

							event.setOnGround(true);

						} else if (this.stage == 1) {
							this.y -= 5.0E-15D;
						} else {
							this.y -= 4.0E-15D;
						}

						if (this.y <= Helper.getPlayer().getY()) {
							this.stage = 0;
							this.y = Helper.getPlayer().getY();
							event.setOnGround(true);
						}
						event.setY(this.y);
						MoveUtil.setYVelocity(yOff);

						this.stage++;
					} else {
						this.stage = 0;
					}
				}
			}
		}
	}

	@EventTarget
	public void onAttack(AttackEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
				if (!Helper.getPlayer().isOnGround())
					return;

				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY() + 0.0645, Helper.getPlayer().getZ(), false));
				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY(), Helper.getPlayer().getZ(), false));
			} else if (mode.getCurrentMode().equalsIgnoreCase("Sentiel")) {

				double boostY = MathAssist.random(0.07, 0.1);
				if (!Helper.getPlayer().isOnGround())
					return;

				MoveUtil.setYVelocity(MathAssist.random(0.08, 0.1));

				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY() + boostY, Helper.getPlayer().getZ(), false));
				Helper.sendPacket(new PlayerMoveC2SPacket.PositionOnly(Helper.getPlayer().getX(),
						Helper.getPlayer().getY(), Helper.getPlayer().getZ(), false));
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Spoof")
					|| this.mode.getCurrentMode().equalsIgnoreCase("Jump") && !falling.isToggle()
					|| mode.getCurrentMode().equalsIgnoreCase("Test")) {
				if (event.getPacket() instanceof PlayerInteractEntityC2SPacket
						&& ((PlayerInteractEntityC2SPacket) event.getPacket())
								.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
					if (skipCrit() || attackCount > 0)
						return;

					attackCount++;
					doJumpMode(event);
				} else if (event.getPacket() instanceof HandSwingC2SPacket) {
					if (skipCrit())
						return;
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
				if (this.mode.getCurrentMode().equalsIgnoreCase("Spoof"))
					Helper.getPlayer().setOnGround(true);

				if (attackPacket == null || swingPacket == null) {
					attackCount = 0;
					return;
				}
				Helper.minecraftClient.getNetworkHandler().sendPacket(attackPacket);
				Helper.minecraftClient.getNetworkHandler().sendPacket(swingPacket);

				attackCount = 0;
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
			sendTimer = this.mode.getCurrentMode().equalsIgnoreCase("Jump") && !falling.isToggle() ? 8 : 2;
			attackPacket = (PlayerInteractEntityC2SPacket) event.getPacket();

			if (this.mode.getCurrentMode().equalsIgnoreCase("Jump") && !falling.isToggle())
				Helper.getPlayer().jump();

			event.cancel();
		}
	}

	private void doJumpModeSwing(PacketEvent event) {
		if (sendPackets && swingPacket == null) {
			swingPacket = (HandSwingC2SPacket) event.getPacket();

			event.cancel();
		}
	}

	private boolean skipCrit() {
		boolean a = !Helper.getPlayer().isSubmergedInWater() && !Helper.getPlayer().isInLava()
				&& !Helper.getPlayer().isClimbing();
		if (!Helper.getPlayer().isOnGround())
			return true;
		return !a;
	}

	public static boolean fall() {
		Criticals criticals = ((Criticals) InfMain.getModuleManager().getModuleByClass(Criticals.class));
		// lul
		if (criticals.isEnabled() && criticals.mode.getCurrentMode().equalsIgnoreCase("Jump")
				&& criticals.falling.isToggle()) {
			if (Helper.getPlayer().fallDistance <= criticals.fallDistance.getCurrentValueDouble()
					&& !Helper.getPlayer().isOnGround()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
