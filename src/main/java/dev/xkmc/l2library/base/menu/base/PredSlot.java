package dev.xkmc.l2library.base.menu.base;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Slot added by BaseContainerMenu. Contains multiple helpers
 */
public class PredSlot extends Slot {

	private final Predicate<ItemStack> pred;
	private final int slotCache;

	@Nullable
	private BooleanSupplier pickup;

	@Nullable
	private BooleanSupplier inputLockPred;

	private int max = 64;

	private boolean changed = false;
	private boolean lockInput = false, lockOutput = false;

	/**
	 * Should be called by BaseContainerMenu::addSlot only.
	 * Predicate supplied from subclasses pf BaseContainerMenu.
	 */
	public PredSlot(Container inv, int ind, int x, int y, Predicate<ItemStack> pred) {
		super(inv, ind, x, y);
		this.pred = pred;
		slotCache = ind;
	}

	/**
	 * Set the condition to unlock a slot for input.
	 * Parallel with manual lock and item predicate.
	 */
	public PredSlot setInputLockPred(BooleanSupplier pred) {
		this.inputLockPred = pred;
		return this;
	}

	/**
	 * Set restriction for pickup.
	 */
	public PredSlot setPickup(BooleanSupplier pickup) {
		this.pickup = pickup;
		return this;
	}

	public PredSlot setMax(int max) {
		this.max = max;
		return this;
	}

	@Override
	public int getMaxStackSize() {
		return Math.min(max, super.getMaxStackSize());
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (isInputLocked()) return false;
		return pred.test(stack);
	}

	@Override
	public boolean mayPickup(Player player) {
		if (isOutputLocked()) return false;
		return pickup == null || pickup.getAsBoolean();
	}

	@Override
	public void setChanged() {
		changed = true;
		super.setChanged();
	}

	/**
	 * run only if the content of this slot is changed
	 */
	public boolean clearDirty(Runnable r) {
		if (changed) {
			r.run();
			changed = false;
			return true;
		}
		return false;
	}

	public boolean clearDirty() {
		if (changed) {
			changed = false;
			return true;
		}
		return false;
	}

	/**
	 * eject the content of this slot if the item is no longer allowed
	 */
	public void updateEject(Player player) {
		if (!mayPlace(getItem())) clearSlot(player);
	}

	/**
	 * Lock the input of this slot.
	 * Parallel with lock conditions and item predicate
	 */
	public void setLockInput(boolean lock) {
		lockInput = lock;
	}

	/**
	 * See if the input is locked manually or locked by lock conditions
	 */
	public boolean isInputLocked() {
		return lockInput || (inputLockPred != null && inputLockPred.getAsBoolean());
	}

	/**
	 * Lock the output of this slot.
	 * Parallel with pickup restrictions.
	 */
	public void setLockOutput(boolean lock) {
		lockOutput = lock;
	}

	/**
	 * See if the output is locked manually.
	 */
	public boolean isOutputLocked() {
		return lockOutput;
	}

	/**
	 * eject the content of this slot.
	 */
	public void clearSlot(Player player) {
		BaseContainerMenu.clearSlot(player, container, slotCache);
	}

}
