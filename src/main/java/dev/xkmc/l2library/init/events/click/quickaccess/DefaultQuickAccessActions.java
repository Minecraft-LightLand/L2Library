package dev.xkmc.l2library.init.events.click.quickaccess;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Items;

public class DefaultQuickAccessActions {

	@SuppressWarnings("removal")
	public static void register() {
		QuickAccessClickHandler.register(Items.CRAFTING_TABLE, new SimpleMenuAction(CraftingMenu::new, "container.crafting"));
		QuickAccessClickHandler.register(Items.STONECUTTER, new SimpleMenuAction(StonecutterMenu::new, "container.stonecutter"));
		QuickAccessClickHandler.register(Items.GRINDSTONE, new SimpleMenuAction(GrindstoneMenu::new, "container.grindstone_title"));
		QuickAccessClickHandler.register(Items.CARTOGRAPHY_TABLE, new SimpleMenuAction(CartographyTableMenu::new, "container.cartography_table"));
		QuickAccessClickHandler.register(Items.LOOM, new SimpleMenuAction(LoomMenu::new, "container.loom"));

		QuickAccessClickHandler.register(Items.SMITHING_TABLE, (player, stack) -> (new SimpleMenuAction(
				player.level.enabledFeatures().contains(FeatureFlags.UPDATE_1_20) ?
						SmithingMenu::new : LegacySmithingMenu::new,
				"container.upgrade")).perform(player, stack));

	}

}
