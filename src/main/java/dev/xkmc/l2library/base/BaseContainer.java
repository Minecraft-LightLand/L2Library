package dev.xkmc.l2library.base;

import dev.xkmc.l2library.serial.codec.AliasCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class BaseContainer<T extends BaseContainer<T>> extends SimpleContainer implements AliasCollection<ItemStack> {

	private int max = 64;
	private Predicate<ItemStack> predicate = e -> true;

	public BaseContainer(int size) {
		super(size);
	}

	public T setMax(int max) {
		this.max = Mth.clamp(max, 1, 64);
		return getThis();
	}

	public T setPredicate(Predicate<ItemStack> predicate) {
		this.predicate = predicate;
		return getThis();
	}

	public T add(BaseContainerListener t) {
		addListener(t);
		return getThis();
	}

	public boolean canAddWhileHaveSpace(ItemStack add, int space) {
		int ans = 0;
		for (ItemStack stack : items) {
			if (stack.isEmpty())
				ans++;
			else if (ItemStack.isSameItemSameTags(stack, add) &&
					stack.getCount() + add.getCount() <=
							Math.min(stack.getMaxStackSize(), getMaxStackSize())) {
				return true;
			}
		}
		return ans - 1 >= space;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return predicate.test(stack);
	}

	@Override
	public boolean canAddItem(ItemStack stack) {
		return predicate.test(stack) && super.canAddItem(stack);
	}

	public boolean canRecipeAddItem(ItemStack stack) {
		stack = stack.copy();
		for (ItemStack slot : this.items) {
			if (slot.isEmpty() || ItemStack.isSameItemSameTags(slot, stack)) {
				int cap = Math.min(getMaxStackSize(), slot.getMaxStackSize());
				int amount = Math.min(stack.getCount(), cap - slot.getCount());
				if (amount > 0) {
					stack.shrink(amount);
					if (stack.getCount() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return max;
	}

	@Override
	public List<ItemStack> getAsList() {
		return items;
	}

	@Override
	public void clear() {
		super.clearContent();
	}

	@Override
	public void set(int n, int i, ItemStack elem) {
		items.set(i, elem);
	}

	@Override
	public Class<ItemStack> getElemClass() {
		return ItemStack.class;
	}

	@SuppressWarnings("unchecked")
	public T getThis() {
		return (T) this;
	}
}
