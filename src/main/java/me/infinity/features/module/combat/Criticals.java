package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.AttackEvent;
import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.PlayerSend;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Critical hit", key = GLFW.GLFW_KEY_B, name = "Criticals", visible = true)
public class Criticals extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet",
			new ArrayList<>(Arrays.asList(new String[] { "Jump", "Packet" })), () -> true);

	private Settings falling = new Settings(this, "Falling", false,
			() -> mode.getCurrentMode().equalsIgnoreCase("Jump"));

	private double y;
	private int stage;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotion(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (this.mode.getCurrentMode().equals("Packet")) {
				Helper.getPlayer().lastRenderY = 0.0D;
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
			if (this.mode.getCurrentMode().equals("Jump") && !falling.isToggle()) {
				Helper.getPlayer().jump();
			}
		}
	}

	public static boolean fall() {
		Criticals criticals = ((Criticals) InfMain.getModuleManager().getModuleByClass(Criticals.class));
		// lul
		if (criticals.isEnabled() && criticals.mode.getCurrentMode().equalsIgnoreCase("Jump")
				&& criticals.falling.isToggle()) {
			if (Helper.getPlayer().fallDistance != 0 && !Helper.getPlayer().isOnGround()) {
				return true; 
			} else  {
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
