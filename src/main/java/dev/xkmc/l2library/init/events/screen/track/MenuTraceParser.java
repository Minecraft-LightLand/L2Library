package dev.xkmc.l2library.init.events.screen.track;

import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Optional;

public interface MenuTraceParser<T extends AbstractContainerMenu> {

	static <T extends AbstractContainerMenu> MenuTraceParser<T> compound(MenuTraceParser<T> prev, MenuTraceParser<T> getter) {
		return menu -> prev.track(menu).or(() -> getter.track(menu));
	}

	Optional<TrackedEntry<?>> track(T menu);

}
