package dev.xkmc.l2library.serial.ingredients;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EnchantmentIngredient extends BaseIngredient<EnchantmentIngredient> {

	private record EnchValue(Enchantment ench, int min) implements Value {

		@Override
		public Collection<ItemStack> getItems() {
			return IntStream.range(min, ench.getMaxLevel() + 1).mapToObj(i -> EnchantedBookItem.createForEnchantment(
					new EnchantmentInstance(ench, i))).toList();
		}

	}

	@SerialClass.SerialField
	public Enchantment enchantment;
	@SerialClass.SerialField
	public int min_level;

	@Deprecated
	public EnchantmentIngredient() {

	}

	public EnchantmentIngredient(Enchantment enchantment, int minLevel) {
		super(Stream.of(new EnchValue(enchantment, minLevel)));
		this.enchantment = enchantment;
		this.min_level = minLevel;
	}

	public EnchantmentIngredient validate() {
		return new EnchantmentIngredient(enchantment, min_level);
	}

	public boolean test(ItemStack stack) {
		return EnchantmentHelper.getEnchantments(stack).getOrDefault(this.enchantment, 0) >= this.min_level;
	}

}
