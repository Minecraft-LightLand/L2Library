package dev.xkmc.l2library.init.events.select.item;

import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.data.L2Keys;
import dev.xkmc.l2library.init.events.select.ISelectionListener;
import dev.xkmc.l2library.init.events.select.SetSelectedToServer;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ItemSelectionListener implements ISelectionListener {

	@Override
	public boolean handleServerSetSelection(SetSelectedToServer packet, Player sender) {
		IItemSelector sel = IItemSelector.getSelection(sender);
		if (sel != null) {
			sel.swap(sender, packet.slot);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleClientScroll(int diff, LocalPlayer player) {
		if (!ItemSelectionOverlay.INSTANCE.isScreenOn()) return false;
		if (L2LibraryConfig.CLIENT.selectionScrollRequireShift.get() && !player.isShiftKeyDown()) return false;
		IItemSelector sel = IItemSelector.getSelection(Proxy.getClientPlayer());
		if (sel == null) return false;
		sel.move(diff, player);
		return true;
	}

	@Override
	public boolean handleClientKey(L2Keys key, LocalPlayer player) {
		if (!ItemSelectionOverlay.INSTANCE.isScreenOn()) return false;
		IItemSelector sel = IItemSelector.getSelection(player);
		if (sel == null) return false;
		if (key == L2Keys.UP) {
			sel.move(-1, player);
			return true;
		} else if (key == L2Keys.DOWN) {
			sel.move(1, player);
			return true;
		}
		return false;
	}
}
