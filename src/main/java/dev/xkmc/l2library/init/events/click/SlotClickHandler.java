package dev.xkmc.l2library.init.events.click;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public abstract class SlotClickHandler {

	public static final ConcurrentHashMap<ResourceLocation, SlotClickHandler> MAP = new ConcurrentHashMap<>();

	private final ResourceLocation id;

	public SlotClickHandler(ResourceLocation rl) {
		MAP.put(rl, this);
		this.id = rl;
	}

	protected final void slotClickToServer(int index, int slot, int wid) {
		L2Library.PACKET_HANDLER.toServer(new SlotClickToServer(id, index, slot, wid));
	}

	public abstract boolean isAllowed(ItemStack stack);

	public abstract void handle(ServerPlayer player, int index, int slot, int wid);

}
