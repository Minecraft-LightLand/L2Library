package dev.xkmc.l2library.compat.curio;

import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public abstract class BaseCuriosListMenu<T extends BaseContainerMenu<T>> extends BaseContainerMenu<T> {

	public static final SpriteManager[] MANAGER;

	static {
		MANAGER = new SpriteManager[4];
		for (int i = 0; i < 4; i++) {
			MANAGER[i] = new SpriteManager(L2Library.MODID, "curios_" + (i + 3));
		}
	}

	private static SpriteManager getManager(int size) {
		return MANAGER[Math.min(Math.max(size - 3, 0), 3)];
	}

	public final BaseCuriosWrapper curios;

	protected BaseCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, BaseCuriosWrapper curios) {
		super(type, wid, plInv, getManager(curios.getRows()), e -> new BaseContainer<>(curios.getSize(), e), false);
		addCurioSlot("grid", curios);
		this.curios = curios;
	}

	protected void addCurioSlot(String name, BaseCuriosWrapper curios) {
		int current = added;
		int[] removed = new int[]{0};
		sprite.getSlot(name, (x, y) -> {
			int i = added - current;
			var slot = curios.getSlotAtPosition(i + removed[0]);
			if (slot == null) {
				removed[0]++;
				return null;
			}
			var ans = slot.toSlot(x, y);
			added++;
			return ans;
		}, this::addSlot);
	}


	private boolean checkSwitch(Player player, int page) {
		if (page >= 0 && page < curios.total) {
			if (player instanceof ServerPlayer sp) {
				CuriosEventHandler.openMenuWrapped(sp, () -> switchPage(sp, page));
			} else {
				slots.clear();
			}
			return true;
		}
		return false;
	}

	public abstract void switchPage(ServerPlayer sp, int page);

	@Override
	public boolean clickMenuButton(Player player, int btn) {
		if (btn == 1) {
			return checkSwitch(player, curios.page - 1);
		} else if (btn == 2) {
			return checkSwitch(player, curios.page + 1);
		}
		return super.clickMenuButton(player, btn);
	}

}
