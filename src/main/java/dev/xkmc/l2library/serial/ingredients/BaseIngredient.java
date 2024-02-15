package dev.xkmc.l2library.serial.ingredients;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

@SerialClass
public abstract class BaseIngredient<T extends BaseIngredient<T>> extends Ingredient {

	@Deprecated
	protected BaseIngredient() {
		super(Stream.of());
	}

	public BaseIngredient(ItemStack display) {
		super(Stream.of(new ItemValue(display)));
	}

	/**
	 * Value constructor, for ingredients that have some vanilla representation
	 */
	protected BaseIngredient(Stream<? extends Value> values) {
		super(values);
	}

	public abstract T validate();

	public abstract boolean test(ItemStack stack);

	public boolean isSimple() {
		return false;
	}

}
