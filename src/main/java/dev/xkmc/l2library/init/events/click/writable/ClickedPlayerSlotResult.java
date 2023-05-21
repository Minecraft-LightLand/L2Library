package dev.xkmc.l2library.init.events.click.writable;

import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import net.minecraft.world.item.ItemStack;

public record ClickedPlayerSlotResult(ItemStack stack, PlayerSlot<?> slot, ContainerCallback container) {

}
