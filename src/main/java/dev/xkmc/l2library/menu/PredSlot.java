package dev.xkmc.l2library.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PredSlot extends Slot {

	private final Predicate<ItemStack> pred;
	private BooleanSupplier pickup;
	private Consumer<ItemStack> take;

	public PredSlot(Container inv, int ind, int x, int y, Predicate<ItemStack> pred) {
		super(inv, ind, x, y);
		this.pred = pred;
	}

	public PredSlot setPickup(BooleanSupplier pickup) {
		this.pickup = pickup;
		return this;
	}

	public PredSlot setTake(Consumer<ItemStack> take) {
		this.take = take;
		return this;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return pred.test(stack);
	}

	@Override
	public boolean mayPickup(Player player) {
		return pickup == null ? super.mayPickup(player) : pickup.getAsBoolean();
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		if (take != null) {
			take.accept(stack);
		}
		super.onTake(player, stack);
	}

}
