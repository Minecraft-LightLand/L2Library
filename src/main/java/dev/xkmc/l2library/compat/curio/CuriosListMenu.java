package dev.xkmc.l2library.compat.curio;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CuriosListMenu extends BaseCuriosListMenu<CuriosListMenu> {
	public static CuriosListMenu fromNetwork(MenuType<CuriosListMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		int page = buf.readInt();
		return new CuriosListMenu(type, wid, plInv, new CuriosWrapper(plInv.player, page));
	}

	protected CuriosListMenu(MenuType<?> type, int wid, Inventory plInv, CuriosWrapper curios) {
		super(type, wid, plInv, curios);
	}

	@Override
	public void switchPage(ServerPlayer sp, int page) {
		new CuriosMenuPvd(CuriosScreenCompatImpl.get().menuType.get(), page).open(sp);
	}
}
