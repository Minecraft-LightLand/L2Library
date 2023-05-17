package dev.xkmc.l2library.init.events.screen.track;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.Optional;

public interface MenuTraceParser<T extends AbstractContainerMenu> {

	static <T extends AbstractContainerMenu> MenuTraceParser<T> compound(MenuTraceParser<T> prev, MenuTraceParser<T> getter) {
		return (menu, comp) -> prev.track(menu, comp).or(() -> getter.track(menu, comp));
	}

	Optional<TrackedEntry<?>> track(T menu, @Nullable Component comp);

}
