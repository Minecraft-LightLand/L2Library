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

	public int countSpace() {
		int ans = 0;
		for (ItemStack stack : items) {
			if (stack.isEmpty())
				ans++;
		}
		return ans;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return predicate.test(stack);
	}

	@Override
	public boolean canAddItem(ItemStack stack) {
		return predicate.test(stack) && super.canAddItem(stack);
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
