package dev.xkmc.l2library.init.events.screen.source;

import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Optional;

public interface MenuSourceGetter<T extends AbstractContainerMenu> {

	static <T extends AbstractContainerMenu> MenuSourceGetter<T> compound(MenuSourceGetter<T> prev, MenuSourceGetter<T> getter) {
		return (menu, slot, index, wid) -> prev.getSlot(menu, slot, index, wid).or(() -> getter.getSlot(menu, slot, index, wid));
	}

	Optional<PlayerSlot<?>> getSlot(T menu, int slot, int index, int wid);

}
