package dev.xkmc.l2library.base.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;

public abstract class SideBar {

	protected final float max_time;
	protected final float max_ease;

	protected int prev = 0;

	protected float idle = 0;
	protected float ease_time = 0;
	protected float prev_time = -1;

	public SideBar(float duration, float ease) {
		this.max_time = duration;
		this.max_ease = ease;
	}

	public abstract int getSignature();

	public abstract boolean isScreenOn();

	protected boolean isOnHold() {
		return Minecraft.getInstance().options.keyShift.isDown();
	}

	protected boolean ease(float current_time) {
		if (!isScreenOn()) {
			prev = 0;
			idle = 0;
			ease_time = 0;
			prev_time = -1;
			return false;
		}
		float time_diff = prev_time < 0 ? 0 : (current_time - prev_time);
		prev_time = current_time;

		int signature = getSignature();
		if (signature != prev || isOnHold()) {
			prev = signature;
			idle = 0;
		} else {
			idle += time_diff;
		}
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

	protected int getXOffset(int width) {
		return 0;
	}

	protected int getYOffset(int height) {
		return 0;
	}

}
