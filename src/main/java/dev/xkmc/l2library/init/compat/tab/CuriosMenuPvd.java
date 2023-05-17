package dev.xkmc.l2library.init.compat.tab;

import dev.xkmc.l2library.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public record CuriosMenuPvd(MenuType<CuriosListMenu> type) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return LangData.CURIOS.get();
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return new CuriosListMenu(type, wid, inv, new CuriosWrapper(player));
	}
}
