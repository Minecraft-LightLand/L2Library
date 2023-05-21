package dev.xkmc.l2library.init.events.click.writable;

import dev.xkmc.l2library.init.events.click.SlotClickHandler;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public abstract class WritableStackClickHandler extends SlotClickHandler {

	public WritableStackClickHandler(ResourceLocation rl) {
		super(rl);
	}

	@Nullable
	protected ClickedPlayerSlotResult getSlot(ServerPlayer player, int index, int slot, int wid) {
		if (slot >= 0) {
			return new ClickedPlayerSlotResult(player.getInventory().getItem(slot),
					PlayerSlot.ofInventory(slot),
					new NoContainerCallback());
		} else {
				AbstractContainerMenu menu = player.containerMenu;
			if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) return null;
			return new ClickedPlayerSlotResult(menu.getSlot(index).getItem(),
					PlayerSlot.ofOtherInventory(slot, index, wid, menu),
					new RealContainerCallback(menu.getSlot(index).container));
		}

	}

	protected abstract void handle(ServerPlayer player, ClickedPlayerSlotResult result);

	@Override
	public void handle(ServerPlayer player, int index, int slot, int wid) {
		ClickedPlayerSlotResult result = getSlot(player, index, slot, wid);
		if (result != null) {
			handle(player, result);
		}
	}

}
