package dev.xkmc.l2library.init.events.select;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class SelectionRegistry {

	private static final Map<ResourceLocation, ISelectionListener> REGISTRY_MAP = new HashMap<>();
	private static final Map<Integer, ISelectionListener> PRIORITY_MAP = new TreeMap<>();

	/**
	 * -5000: Numeric Display
	 * -1000: Backpack Selection
	 * 0: ItemSelection
	 */
	public static synchronized void register(int priority, ISelectionListener sel) {
		REGISTRY_MAP.put(sel.getID(), sel);
		PRIORITY_MAP.put(priority, sel);
	}

	public static Optional<ISelectionListener> getClientActiveListener(Player player) {
		for (var e : PRIORITY_MAP.values()) {
			if (e.isClientActive(player)) {
				return Optional.of(e);
			}
		}
		return Optional.empty();
	}

	@Nullable
	public static ISelectionListener getEntry(ResourceLocation name) {
		return REGISTRY_MAP.get(name);
	}
}
