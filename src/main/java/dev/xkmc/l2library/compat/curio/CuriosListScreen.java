package dev.xkmc.l2library.compat.curio;

import dev.xkmc.l2library.base.tabs.core.TabManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CuriosListScreen extends BaseCuriosListScreen<CuriosListMenu> {

	public CuriosListScreen(CuriosListMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void init() {
		super.init();
		new TabManager(this).init(this::addRenderableWidget, TabCurios.tab);

	}

}
