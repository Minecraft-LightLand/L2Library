package dev.xkmc.l2library.init.events.screen.base;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.source.*;
import dev.xkmc.l2library.init.events.screen.track.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import java.util.Optional;

public class ScreenTrackerRegistry {

	public static final L2Registrate.RegistryInstance<ItemSource<?>> ITEM_SOURCE = L2Library.REGISTRATE.newRegistry("item_source", ItemSource.class);
	public static final L2Registrate.RegistryInstance<TrackedEntryType<?>> TRACKED_ENTRY_TYPE = L2Library.REGISTRATE.newRegistry("tracked_entry_type", TrackedEntryType.class);

	public static final RegistryEntry<InventorySource> IS_INVENTORY = L2Library.REGISTRATE.simple("inventory", ITEM_SOURCE.key(), InventorySource::new);
	public static final RegistryEntry<EnderSource> IS_ENDER = L2Library.REGISTRATE.simple("ender", ITEM_SOURCE.key(), EnderSource::new);

	public static final RegistryEntry<InventoryTrace> TE_INVENTORY = L2Library.REGISTRATE.simple("inventory", TRACKED_ENTRY_TYPE.key(), InventoryTrace::new);
	public static final RegistryEntry<EnderTrace> TE_ENDER = L2Library.REGISTRATE.simple("ender", TRACKED_ENTRY_TYPE.key(), EnderTrace::new);

	public static void register() {
	}

	public static void commonSetup() {

		MenuSourceRegistry.register(MenuType.GENERIC_9x3, (menu, slot, index, wid) ->
				menu.getContainer() instanceof PlayerEnderChestContainer ?
						Optional.of(new PlayerSlot<>(IS_ENDER.get(), new SimpleSlotData(index))) :
						Optional.empty());

		MenuTraceRegistry.register(MenuType.GENERIC_9x3, (menu, comp) ->
				menu.getContainer() instanceof PlayerEnderChestContainer ?
						Optional.of(TrackedEntry.of(TE_ENDER.get(), NoData.DATA, comp)) :
						Optional.empty());

	}

}
