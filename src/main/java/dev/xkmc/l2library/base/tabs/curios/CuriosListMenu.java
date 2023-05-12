package dev.xkmc.l2library.base.tabs.curios;

import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CuriosListMenu extends BaseContainerMenu<CuriosListMenu> {

	public static final SpriteManager[] MANAGER;

	static {
		MANAGER = new SpriteManager[4];
		for (int i = 0; i < 4; i++) {
			MANAGER[i] = new SpriteManager(L2Library.MODID, "curios_" + (i + 3));
		}
	}

	private static SpriteManager getManager(int size) {
		int n = (size + 8) / 9;
		return MANAGER[Math.min(Math.max(n - 3, 0), 3)];
	}

	public static CuriosListMenu fromNetwork(MenuType<CuriosListMenu> type, int wid, Inventory plInv) {
		return new CuriosListMenu(type, wid, plInv, new CuriosWrapper(plInv.player));
	}

	protected CuriosWrapper curios;

	protected CuriosListMenu(MenuType<?> type, int wid, Inventory plInv, CuriosWrapper curios) {
		super(type, wid, plInv, getManager(curios.getSize()), e -> new BaseContainer<>(curios.getSize(), e), false);
		addCurioSlot("grid", curios);
		this.curios = curios;
	}

	protected void addCurioSlot(String name, CuriosWrapper curios) {
		int current = added;
		sprite.getSlot(name, (x, y) -> {
			int i = added - current;
			if (i >= curios.getSize()) return null;
			var ans = curios.get(i).toSlot(x, y);
			added++;
			return ans;
		}, this::addSlot);
	}

}
