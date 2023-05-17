package dev.xkmc.l2library.init.events.screen.base;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.packets.AddTrackedToClient;
import dev.xkmc.l2library.init.events.screen.packets.PopLayerToClient;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import dev.xkmc.l2library.init.events.screen.track.MenuTraceRegistry;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntry;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Stack;

/**
 * 1. Server open BaseOpenableContainer: add previous and current menu to stack, tell client
 * 2. Client open BaseOpenableContainer: record title of previous and current title to stack
 */
@SerialClass
@SuppressWarnings("unused")
public class ScreenTracker extends PlayerCapabilityTemplate<ScreenTracker> {

	public static final Capability<ScreenTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final PlayerCapabilityHolder<ScreenTracker> HOLDER = new PlayerCapabilityHolder<>(
			new ResourceLocation(L2Library.MODID, "screen_tracker"), CAPABILITY,
			ScreenTracker.class, ScreenTracker::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

	public static ScreenTracker get(Player player) {
		return HOLDER.get(player);
	}

	public static void onServerOpen(ServerPlayer player, AbstractContainerMenu prev, PlayerSlot<?> slot) {
		get(player).serverOpen(player, prev, slot);
	}

	public static void onServerClose(Player player, int wid) {

	}

	// non-static

	final Stack<TrackedEntry<?>> stack = new Stack<>();

	int wid;

	// --- server only values

	public MenuProvider provider;

	private AbstractContainerMenu current;

	private void serverOpen(ServerPlayer player, AbstractContainerMenu cont, PlayerSlot<?> slot) {
		Component comp = provider == null ? null : provider.getDisplayName();
		var getter = MenuTraceRegistry.get(cont.getType());
		if (getter == null) return;
		var entry = getter.track(Wrappers.cast(cont), comp);
		if (entry.isEmpty()) return;
		serverOpen(player, entry.get(), cont);
	}

	private void serverOpen(ServerPlayer player, TrackedEntry<?> prev, AbstractContainerMenu cont) {
		int toRemove = 0;
		if (!stack.isEmpty()) {
			for (int i = 0; i < stack.size(); i++) {
				TrackedEntry<?> itr = stack.get(i);
				if (itr.type() == prev.type()) {
					if (!itr.type().match(cont, Wrappers.cast(prev.data()))) {
						continue;
					}
				} else continue;
				toRemove = stack.size() - i;
				for (int j = 0; j < toRemove; j++) {
					stack.pop();
				}
			}
		}
		stack.push(prev);
		current = cont;
		wid = cont.containerId;
		L2Library.PACKET_HANDLER.toClientPlayer(new AddTrackedToClient(prev, toRemove, wid), player);
	}

	public boolean serverRestore(ServerPlayer player, int wid) {
		if (stack.isEmpty()) return false;
		if (this.wid == wid) {
			LayerPopType type = stack.pop().restoreServerMenu(player);
			if (type != LayerPopType.FAIL) {
				int id = player.containerMenu.containerId;
				this.wid = id;
				L2Library.PACKET_HANDLER.toClientPlayer(new PopLayerToClient(type, id), player);
				return true;
			}
		}
		return false;
	}

	// --- client only values
	boolean isWaiting;

}
