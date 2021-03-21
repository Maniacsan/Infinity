package me.infinity.features.module.combat;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(category = Module.Category.COMBAT, desc = "When holding the bowstring and LKM, the bow will automatically spam with arrows", key = -2, name = "BetterBow", visible = true)
public class BetterBow extends Module {

	private Settings counter = new Settings(this, "Count Helper", true, () -> true);
	private Settings autoShoot = new Settings(this, "Auto Shoot", false, () -> true);

	private Settings delay = new Settings(this, "Delay", 2.5, 0.1, 25.0, () -> true);

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().getMainHandStack().getItem() instanceof BowItem && Helper.getPlayer().isUsingItem()) {

			if (Helper.getPlayer().getItemUseTime() >= delay.getCurrentValueDouble()) {

				if (autoShoot.isToggle() || Helper.minecraftClient.options.keyAttack.isPressed()) {
					Helper.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN,
							Helper.getPlayer().getHorizontalFacing()));
					Helper.sendPacket(new PlayerInteractItemC2SPacket(Helper.getPlayer().getActiveHand()));
					Helper.getPlayer().stopUsingItem();
				}
			}
		}
	}

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		String count = "Counting... " + Formatting.BLUE + Helper.getPlayer().getItemUseTime();

		if (Helper.getPlayer().getItemUseTime() >= delay.getCurrentValueDouble())
			if (!autoShoot.isToggle())
				count = "Delay Ready " + Formatting.GRAY + "press " + Formatting.BLUE + "LKM" + Formatting.GRAY
						+ " for use";

		if (counter.isToggle()) {

			if (Helper.getPlayer().getMainHandStack().getItem() instanceof BowItem
					&& Helper.getPlayer().isUsingItem()) {
				FontUtils.drawHCenteredString(matrices, count, width / 2, height / 2 - 70, -1);
			}

		}
	}

}
