package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MenuTraceRegistry {

	private static final HashMap<MenuType<?>, MenuTraceParser<?>> MAP = new HashMap<>();

	public static synchronized <T extends AbstractContainerMenu> void register(MenuType<T> type, MenuTraceParser<T> getter) {
		if (MAP.containsKey(type)) {
			MenuTraceParser<T> prev = get(type);
			assert prev != null;
			MenuTraceParser<T> next = MenuTraceParser.compound(prev, getter);
			MAP.put(type, next);
		} else {
			MAP.put(type, getter);
		}
	}

	@Nullable
	public static <T extends AbstractContainerMenu> MenuTraceParser<T> get(MenuType<T> type) {
		if (!MAP.containsKey(type))
			return null;
		return Wrappers.cast(MAP.get(type));
	}


}
