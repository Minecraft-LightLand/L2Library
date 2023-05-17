package dev.xkmc.l2library.init.events.screen.base;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.packets.RestoreMenuToServer;
import dev.xkmc.l2library.init.events.screen.packets.ScreenType;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntry;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import javax.annotation.Nullable;

public class ScreenTrackerClient {

	public static boolean onClientClose(int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		if (onClientCloseImpl(tracker, wid)) {
			tracker.isWaiting = true;
			L2Library.PACKET_HANDLER.toServer(new RestoreMenuToServer(wid));
			return true;
		}
		return false;
	}

	public static void clientAddLayer(TrackedEntry<?> entry, int toRemove, int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		for (int i = 0; i < toRemove; i++) {
			if (tracker.stack.size() > 0) {
				tracker.stack.pop();
			} else break;
		}
		tracker.wid = wid;
		tracker.stack.add(entry);
	}

	public static void clientClear(ScreenType type) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		tracker.isWaiting = false;
		tracker.stack.clear();
		type.perform();
	}

	private static boolean onClientCloseImpl(ScreenTracker tracker, int wid) {
		if (Screen.hasShiftDown() || tracker.isWaiting) {
			// second exit: close screen
			tracker.isWaiting = false;
			L2Library.PACKET_HANDLER.toServer(new RestoreMenuToServer(-1));
			return false;
		}
		if (tracker.stack.isEmpty()) return false;
		return tracker.wid == wid;
	}

	public static void clientPop(LayerPopType type, int wid) {
		ScreenTracker tracker = ScreenTracker.get(Proxy.getClientPlayer());
		tracker.isWaiting = false;
		if (type == LayerPopType.REMAIN) {
			tracker.stack.pop();
		} else {
			tracker.stack.clear();
		}
		tracker.wid = wid;
	}

}
