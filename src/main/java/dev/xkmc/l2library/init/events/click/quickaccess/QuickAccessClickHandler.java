package dev.xkmc.l2library.init.events.click.quickaccess;

import dev.xkmc.l2library.init.data.L2TagGen;
import dev.xkmc.l2library.init.events.click.SlotClickHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkHooks;

public class QuickAccessClickHandler extends SlotClickHandler {

	public QuickAccessClickHandler(ResourceLocation rl) {
		super(rl);
	}

	@Override
	public boolean isAllowed(ItemStack stack) {
		return stack.is(L2TagGen.QUICK_ACCESS_VANILLA);
	}

	@SuppressWarnings("removal")
	@Override
	protected void handle(ServerPlayer player, ItemStack stack) {//TODO fix close inventory

		if (stack.is(Items.CRAFTING_TABLE)) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					new CraftingMenu(wid, inv, new DummyHandler(pl)),
					Component.translatable("container.crafting")));
		}

		if (stack.is(Items.SMITHING_TABLE)) {
			if (player.level.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
				NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
						new SmithingMenu(wid, inv, new DummyHandler(pl)),
						Component.translatable("container.upgrade")));
			} else {
				NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
						new LegacySmithingMenu(wid, inv, new DummyHandler(pl)),
						Component.translatable("container.upgrade")));
			}
		}

		if (stack.is(Items.STONECUTTER)) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					new StonecutterMenu(wid, inv, new DummyHandler(pl)),
					Component.translatable("container.stonecutter")));
		}

		if (stack.is(Items.GRINDSTONE)) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					new GrindstoneMenu(wid, inv, new DummyHandler(pl)),
					Component.translatable("container.grindstone_title")));
		}

		if (stack.is(Items.CARTOGRAPHY_TABLE)) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					new CartographyTableMenu(wid, inv, new DummyHandler(pl)),
					Component.translatable("container.cartography_table")));
		}

		if (stack.is(Items.LOOM)) {
			NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
					new LoomMenu(wid, inv, new DummyHandler(pl)),
					Component.translatable("container.loom")));
		}
	}
}
