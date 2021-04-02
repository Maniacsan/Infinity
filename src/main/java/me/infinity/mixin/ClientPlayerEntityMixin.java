package me.infinity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.mojang.authlib.GameProfile;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.event.PushOutBlockEvent;
import me.infinity.features.module.movement.SafeWalk;
import me.infinity.features.module.player.NoSlow;
import me.infinity.features.module.player.Scaffold;
import me.infinity.utils.UpdateUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Shadow
	private double lastX;

	@Shadow
	private double lastBaseY;

	@Shadow
	private double lastZ;

	@Shadow
	private float lastYaw;

	@Shadow
	private float lastPitch;

	@Shadow
	private boolean lastSneaking;

	@Shadow
	private boolean lastSprinting;

	@Shadow
	private boolean lastOnGround;

	@Shadow
	private boolean autoJumpEnabled;

	@Shadow
	private int ticksSinceLastPositionPacketSent;

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	@Final
	public ClientPlayNetworkHandler networkHandler;

	@Shadow
	protected abstract boolean isCamera();

	@Shadow
	public abstract boolean isSneaking();

	@Shadow
	protected void autoJump(float float_1, float float_2) {
	}

	@Overwrite
	public void sendMovementPackets() {
		try {

			MotionEvent motionEvent = new MotionEvent(EventType.PRE, this.yaw, this.pitch, this.getX(), this.getY(),
					this.getZ(), this.onGround);
			EventManager.call(motionEvent);

			boolean bl = this.isSprinting();
			if (bl != this.lastSprinting) {
				ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING
						: ClientCommandC2SPacket.Mode.STOP_SPRINTING;
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
				this.lastSprinting = bl;
			}

			boolean bl2 = this.isSneaking();
			if (bl2 != this.lastSneaking) {
				ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
						: ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
				this.lastSneaking = bl2;
			}

			float yaw = motionEvent.getYaw();
			float pitch = motionEvent.getPitch();

			double x = motionEvent.getX();
			double y = motionEvent.getY();
			double z = motionEvent.getZ();

			boolean onGround = motionEvent.isOnGround();

			if (this.isCamera()) {
				double d = x - this.lastX;
				double e = y - this.lastBaseY;
				double f = z - this.lastZ;
				double g = (double) (yaw - this.lastYaw);
				double h = (double) (pitch - this.lastPitch);
				++this.ticksSinceLastPositionPacketSent;
				boolean bl3 = d * d + e * e + f * f > 9.0E-4D || this.ticksSinceLastPositionPacketSent >= 20;
				boolean bl4 = g != 0.0D || h != 0.0D;
				if (this.hasVehicle()) {
					Vec3d vec3d = this.getVelocity();
					this.networkHandler
							.sendPacket(new PlayerMoveC2SPacket.Both(vec3d.x, -999.0D, vec3d.z, yaw, pitch, onGround));
					bl3 = false;
				} else if (bl3 && bl4) {
					this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(x, y, z, yaw, pitch, onGround));
				} else if (bl3) {
					this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(x, y, z, onGround));
				} else if (bl4) {
					this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(yaw, pitch, onGround));
				} else if (this.lastOnGround != onGround) {
					this.networkHandler.sendPacket(new PlayerMoveC2SPacket(onGround));
				}

				if (bl3) {
					this.lastX = x;
					this.lastBaseY = y;
					this.lastZ = z;
					this.ticksSinceLastPositionPacketSent = 0;
				}

				if (bl4) {
					this.lastYaw = yaw;
					this.lastPitch = pitch;
				}

				this.lastOnGround = onGround;
				this.autoJumpEnabled = this.client.options.autoJump;
			}
			MotionEvent motionPostEvent = new MotionEvent(EventType.POST, this.yaw, this.pitch, this.getX(),
					this.getY(), this.getZ(), this.onGround);
			EventManager.call(motionPostEvent);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
	private boolean tickUseItem(ClientPlayerEntity player) {
		NoSlow noSlow = ((NoSlow) InfMain.getModuleManager().getModuleByClass(NoSlow.class));

		if (noSlow.isEnabled() && noSlow.mode.getCurrentMode().equalsIgnoreCase("Vanilla")
				|| noSlow.isEnabled() && noSlow.mode.getCurrentMode().equalsIgnoreCase("NCP")) {
			return false;
		}

		return player.isUsingItem();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		InfMain.getHookManager().onPlayerTick();

	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	public void move(MovementType movementType_1, Vec3d vec3d_1, CallbackInfo info) {
		PlayerMoveEvent moveEvent = new PlayerMoveEvent(movementType_1, vec3d_1);
		EventManager.call(moveEvent);
		if (moveEvent.isCancelled()) {
			info.cancel();
		} else if (!movementType_1.equals(moveEvent.type) || !vec3d_1.equals(moveEvent.vec3d)) {
			double double_1 = this.getX();
			double double_2 = this.getZ();
			super.move(moveEvent.type, moveEvent.vec3d);
			this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
			info.cancel();
		}
	}

	@Override
	protected boolean clipAtLedge() {
		return super.clipAtLedge() || InfMain.getModuleManager().getModuleByClass(SafeWalk.class).isEnabled()
				|| (InfMain.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()
						&& ((Scaffold) InfMain.getModuleManager().getModuleByClass(Scaffold.class)).safeWalk
								.isToggle());
	}

	@Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
	private void pushOutOfBlocks(double x, double d, CallbackInfo ci) {
		PushOutBlockEvent pushEvent = new PushOutBlockEvent();
		EventManager.call(pushEvent);
		if (pushEvent.isCancelled()) {
			ci.cancel();
		}
	}

}
