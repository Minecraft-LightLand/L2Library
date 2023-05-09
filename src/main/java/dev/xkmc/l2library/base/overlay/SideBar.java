package dev.xkmc.l2library.base.overlay;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public abstract class SideBar<S extends SideBar.Signature<S>> {

	public interface Signature<S extends Signature<S>> {

		boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable S old);

	}

	public record IntSignature(int val) implements Signature<IntSignature> {

		@Override
		public boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable SideBar.IntSignature old) {
			return old == null || val != old.val;
		}

	}

	protected final float max_time;
	protected final float max_ease;

	@Nullable
	protected S prev;

	protected float idle = 0;
	protected float ease_time = 0;
	protected float prev_time = -1;

	public SideBar(float duration, float ease) {
		this.max_time = duration;
		this.max_ease = ease;
	}

	public abstract S getSignature();

	public abstract boolean isScreenOn();

	protected boolean isOnHold() {
		return Minecraft.getInstance().options.keyShift.isDown();
	}

	protected boolean ease(float current_time) {
		if (!isScreenOn()) {
			prev = null;
			idle = 0;
			ease_time = 0;
			prev_time = -1;
			return false;
		}
		float time_diff = prev_time < 0 ? 0 : (current_time - prev_time);
		prev_time = current_time;

		S signature = getSignature();
		if (signature.shouldRefreshIdle(this, prev) || isOnHold()) {
			idle = 0;
		} else {
			idle += time_diff;
		}
		prev = signature;
		if (idle < max_time) {
			if (ease_time < max_ease) {
				ease_time += time_diff;
				if (ease_time > max_ease) {
					ease_time = max_ease;
				}
			}
		} else {
			if (ease_time > 0) {
				ease_time -= time_diff;
				if (ease_time < 0) {
					ease_time = 0;
				}
			}
		}
		return ease_time > 0;
	}

	protected float getXOffset(int width) {
		return 0;
	}

	protected float getYOffset(int height) {
		return 0;
	}

}
