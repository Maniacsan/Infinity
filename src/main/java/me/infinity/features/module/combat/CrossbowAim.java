package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.ICrossbowItem;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Aimbot for Crossbow to target", key = -2, name = "CrossbowAim", visible = true)
public class CrossbowAim extends Module {

	private Settings mode = new Settings(this, "Mode", "Packet", new ArrayList<>(Arrays.asList("Packet", "Client")),
			() -> true);
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	private Settings throughWalls = new Settings(this, "Through Walls", false, () -> true);

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);
	private Settings range = new Settings(this, "Range", 40, 1, 80, () -> true);
	private Settings speed = new Settings(this, "Speed", 90F, 1F, 180F, () -> true);

	private Entity target;

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {

		String assist = null;

		if (Helper.getPlayer().getMainHandStack().getItem() instanceof CrossbowItem
				&& ((ICrossbowItem) Helper.getPlayer().getMainHandStack().getItem()).isLoaded())
			assist = Formatting.GRAY + "Press " + Formatting.BLUE + "LKM" + Formatting.GRAY + " for aiming";
		else
			assist = null;

		if (Helper.getPlayer().getMainHandStack().getItem() instanceof CrossbowItem
				&& ((ICrossbowItem) Helper.getPlayer().getMainHandStack().getItem()).isLoaded()) {
			if (assist != null)
				FontUtils.drawHCenteredString(matrices, assist, width / 2, height / 2 - 70, -1);
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			if (Helper.getPlayer().getMainHandStack().getItem() instanceof CrossbowItem
					&& ((ICrossbowItem) Helper.getPlayer().getMainHandStack().getItem()).isLoaded()
					&& Helper.minecraftClient.options.keyAttack.isPressed()) {
				target = EntityUtil.setTarget(range.getCurrentValueInt(), fov.getCurrentValueDouble(),
						players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(),
						animals.isToggle(), throughWalls.isToggle());
				if (target == null)
					return;
				float[] look = RotationUtils.bowAimRotation(target);
				
				look[0] = RotationUtils.limitAngleChange(event.getYaw(), look[0], speed.getCurrentValueFloat());
				look[1] = RotationUtils.limitAngleChange(event.getPitch(), look[1], speed.getCurrentValueFloat());

				event.setRotation(look[0], look[1], mode.getCurrentMode().equalsIgnoreCase("Client"));
	
			}
		}
	}
}
