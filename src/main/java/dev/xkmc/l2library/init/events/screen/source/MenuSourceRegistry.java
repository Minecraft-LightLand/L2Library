package dev.xkmc.l2library.init.events.screen.source;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MenuSourceRegistry {

	private static final HashMap<MenuType<?>, MenuSourceGetter<?>> MAP = new HashMap<>();

	public static synchronized <T extends AbstractContainerMenu> void register(MenuType<T> type, MenuSourceGetter<T> getter) {
		if (MAP.containsKey(type)) {
			MenuSourceGetter<T> prev = get(type);
			assert prev != null;
			MenuSourceGetter<T> next = MenuSourceGetter.compound(prev, getter);
			MAP.put(type, next);
		} else {
			MAP.put(type, getter);
		}
	}

	@Nullable
	public static <T extends AbstractContainerMenu> MenuSourceGetter<T> get(MenuType<T> type) {
		if (!MAP.containsKey(type))
			return null;
		return Wrappers.cast(MAP.get(type));
	}

}
