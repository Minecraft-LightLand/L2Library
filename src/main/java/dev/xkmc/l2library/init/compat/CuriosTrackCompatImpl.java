package dev.xkmc.l2library.init.compat;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.compat.tab.CuriosListMenu;
import dev.xkmc.l2library.init.compat.track.CurioInvTrace;
import dev.xkmc.l2library.init.compat.track.CurioSource;
import dev.xkmc.l2library.init.compat.track.CurioTabTrace;
import dev.xkmc.l2library.init.events.screen.base.ScreenTrackerRegistry;
import dev.xkmc.l2library.init.events.screen.source.MenuSourceRegistry;
import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import dev.xkmc.l2library.init.events.screen.source.SimpleSlotData;
import dev.xkmc.l2library.init.events.screen.track.MenuTraceRegistry;
import dev.xkmc.l2library.init.events.screen.track.NoData;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;

import java.util.Map;
import java.util.Optional;

public class CuriosTrackCompatImpl {

	private static CuriosTrackCompatImpl INSTANCE;

	public static CuriosTrackCompatImpl get() {
		if (INSTANCE == null) {
			INSTANCE = new CuriosTrackCompatImpl();
		}
		return INSTANCE;
	}

	ItemStack getItemFromSlotImpl(Player player, int slot) {
		LazyOptional<IItemHandlerModifiable> curios = CuriosApi.getCuriosHelper().getEquippedCurios(player);
		if (curios.isPresent() && curios.resolve().isPresent()) {
			IItemHandlerModifiable e = curios.resolve().get();
			return e.getStackInSlot(slot);
		} else {
			return ItemStack.EMPTY;
		}
	}

	public RegistryEntry<CurioSource> IS_CURIOS;
	public RegistryEntry<CurioInvTrace> TE_CURIO_INV;
	public RegistryEntry<CurioTabTrace> TE_CURIO_TAB;

	void onStartUp() {
		IS_CURIOS = L2Library.REGISTRATE.simple("curios", ScreenTrackerRegistry.ITEM_SOURCE.key(), CurioSource::new);
		TE_CURIO_INV = L2Library.REGISTRATE.simple("curios_inv", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), CurioInvTrace::new);
		TE_CURIO_TAB = L2Library.REGISTRATE.simple("curios_tab", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), CurioTabTrace::new);
	}

	void onCommonSetup() {
		MenuSourceRegistry.register(CuriosRegistry.CURIO_MENU.get(), (menu, slot, index, wid) ->
				getPlayerSlotImpl(slot, index, wid, menu));

		MenuSourceRegistry.register(CuriosScreenCompatImpl.get().menuType.get(), (menu, slot, index, wid) ->
				getPlayerSlotImpl(slot, index, wid, menu));

		MenuTraceRegistry.register(CuriosRegistry.CURIO_MENU.get(), menu ->
				Optional.of(TrackedEntry.of(TE_CURIO_INV.get(), NoData.DATA)));

		MenuTraceRegistry.register(CuriosScreenCompatImpl.get().menuType.get(), menu ->
				Optional.of(TrackedEntry.of(TE_CURIO_TAB.get(), NoData.DATA)));
	}

	private Optional<PlayerSlot<?>> getPlayerSlotImpl(int slot, int index, int wid, AbstractContainerMenu menu) {
		Slot s;
		CurioSlot curioSlot;
		int slotIndex;
		int val;
		if (menu.containerId == wid && menu instanceof CuriosContainer cont) {
			s = cont.getSlot(index);
			if (s instanceof CurioSlot) {
				curioSlot = (CurioSlot) s;
				slotIndex = getSlotIndexInContainer(cont.player, curioSlot.getIdentifier());
				if (slotIndex < 0) {
					return Optional.empty();
				}

				val = slotIndex + curioSlot.getSlotIndex();
				return Optional.of(new PlayerSlot<>(IS_CURIOS.get(), new SimpleSlotData(val)));
			}
		}

		if (menu.containerId == wid && menu instanceof CuriosListMenu cont) {
			s = cont.getSlot(index);
			if (s instanceof CurioSlot) {
				curioSlot = (CurioSlot) s;
				slotIndex = getSlotIndexInContainer(cont.inventory.player, curioSlot.getIdentifier());
				if (slotIndex < 0) {
					return Optional.empty();
				}

				val = slotIndex + curioSlot.getSlotIndex();
				return Optional.of(new PlayerSlot<>(IS_CURIOS.get(), new SimpleSlotData(val)));
			}
		}

		return Optional.empty();
	}

	private static int getSlotIndexInContainer(Player player, String id) {
		var curiosLazy = CuriosApi.getCuriosHelper().getCuriosHandler(player);
		if (!curiosLazy.isPresent() || !curiosLazy.resolve().isPresent()) {
			return -1;
		}
		Map<String, ICurioStacksHandler> curios = curiosLazy.resolve().get().getCurios();
		int index = 0;

		for (ICurioStacksHandler stacksHandler : curios.values()) {
			if (stacksHandler.getIdentifier().equals(id)) {
				return index;
			}
			index += stacksHandler.getSlots();
		}
		return -1;
	}

}
