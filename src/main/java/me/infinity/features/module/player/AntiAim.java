package me.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerInWaterEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.features.module.combat.KillAura;
import me.infinity.utils.Helper;
import me.infinity.utils.MathAssist;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Moves differently to make it harder for the enemy to hit", key = -2, name = "AntiAim", visible = true)
public class AntiAim extends Module {

	private Settings mode = new Settings(this, "Mode", "Custom", new ArrayList<>(Arrays.asList("Custom", "Swimming")),
			() -> true);

	private Settings yawMode = new Settings(this, "Yaw Mode", "Spin", new ArrayList<>(Arrays.asList("Spin", "Random")),
			() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));

	private Settings spinSpeed = new Settings(this, "Spin Speed", 20D, 0D, 60D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Custom")
					&& yawMode.getCurrentMode().equalsIgnoreCase("Spin"));

	private Settings pitchMode = new Settings(this, "Pitch Mode", "Down",
			new ArrayList<>(Arrays.asList("Down", "Up", "Random", "None")),
			() -> mode.getCurrentMode().equalsIgnoreCase("Custom"));

	private float yaw;
	private float pitch;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Custom")) {

				// yaw
				if (yawMode.getCurrentMode().equalsIgnoreCase("Spin")) {
					yaw += spinSpeed.getCurrentValueDouble();

					if (yaw > 180)
						yaw = -180;

					if (yaw < -180)
						yaw = 180;
				} else if (yawMode.getCurrentMode().equalsIgnoreCase("Random")) {
					double randomYaw = Math.min(MathAssist.random(-180, 180), spinSpeed.getCurrentValueDouble());
					yaw = (float) randomYaw;
				}

				// pitch

				if (pitchMode.getCurrentMode().equalsIgnoreCase("Down")) {
					pitch = 90f;
				} else if (pitchMode.getCurrentMode().equalsIgnoreCase("Up")) {
					pitch = -90f;
				} else if (pitchMode.getCurrentMode().equalsIgnoreCase("None")) {
					pitch = Helper.getPlayer().pitch;
				} else if (pitchMode.getCurrentMode().equalsIgnoreCase("Random")) {
					pitch = (float) MathAssist.random(-90, 90);
				}

				float f = (float) (Helper.minecraftClient.options.mouseSensitivity * 0.6F + 0.2F);
				float gcd = f * f * f * 1.2F;

				yaw -= yaw % gcd;
				pitch -= pitch % gcd;

				if (KillAura.target != null)
					return;

				if (!Float.isNaN(yaw) || !Float.isNaN(pitch)) {
					event.setYaw(yaw);
					Helper.getPlayer().bodyYaw = yaw;
					Helper.getPlayer().headYaw = yaw;
					event.setPitch(pitch);
				}

			}
		}
	}
	
	@EventTarget
	public void onSwim(PlayerInWaterEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("Swimming")) {
			if (!Helper.getPlayer().isSprinting() && Helper.getPlayer().forwardSpeed != 0) {
				Helper.getPlayer().setSprinting(true);
			}
			event.setInWater(true);
		}
	}

}
