package dev.xkmc.l2library.init.events.select;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class SelectionRegistry {

	private static final Map<Integer, ISelectionListener> MAP = new TreeMap<>();

	/**
	 * 0: ItemSelection
	 * 1000: Backpack Selection
	 */
	public static synchronized void register(int priority, ISelectionListener sel) {
		MAP.put(priority, sel);
	}

	public static Collection<ISelectionListener> getListeners() {
		return MAP.values();
	}

}
