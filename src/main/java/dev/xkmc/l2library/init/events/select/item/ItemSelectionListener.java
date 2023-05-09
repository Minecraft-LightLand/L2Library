package dev.xkmc.l2library.init.events.select.item;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.data.L2Keys;
import dev.xkmc.l2library.init.events.select.ISelectionListener;
import dev.xkmc.l2library.init.events.select.SetSelectedToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

public class ItemSelectionListener implements ISelectionListener {

	public static final ISelectionListener INSTANCE = new ItemSelectionListener();

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(L2Library.MODID, "item");
	}

	@Override
	public boolean isClientActive(Player player) {
		if (Minecraft.getInstance().screen != null) return false;
		IItemSelector sel = IItemSelector.getSelection(player);
		return sel != null;
	}

	@Override
	public void handleServerSetSelection(SetSelectedToServer packet, Player sender) {
		IItemSelector sel = IItemSelector.getSelection(sender);
		if (sel != null) {
			sel.swap(sender, packet.slot);
		}
	}

	@Override
	public boolean handleClientScroll(int diff, Player player) {
		IItemSelector sel = IItemSelector.getSelection(player);
		if (sel == null) return false;
		toServer(sel.move(diff, player));
		return true;
	}

	@Override
	public void handleClientKey(L2Keys key, Player player) {
		IItemSelector sel = IItemSelector.getSelection(player);
		if (sel == null) return;
		if (key == L2Keys.UP) {
			toServer(sel.move(-1, player));
		} else if (key == L2Keys.DOWN) {
			toServer(sel.move(1, player));
		}
	}

	@Override
	public boolean handleClientNumericKey(int i, BooleanSupplier consumeClick) {
		return false;
	}

}
