package dev.xkmc.l2library.base.ingredients;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import javax.annotation.Nullable;

@SerialClass
public class EnchantmentIngredient extends BaseIngredient<EnchantmentIngredient> {

	public static final Serializer<EnchantmentIngredient> INSTANCE =
			new Serializer<>(EnchantmentIngredient.class, new ResourceLocation(L2Library.MODID, "enchantment"));

	@SerialClass.SerialField
	public Enchantment enchantment;
	@SerialClass.SerialField
	public int min_level;

	@Deprecated
	public EnchantmentIngredient() {

	}

	public EnchantmentIngredient(Enchantment enchantment, int minLevel) {
		super(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, minLevel)));
		this.enchantment = enchantment;
		this.min_level = minLevel;
	}

	protected EnchantmentIngredient validate() {
		return new EnchantmentIngredient(enchantment, min_level);
	}

	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		return EnchantmentHelper.getEnchantments(stack).getOrDefault(this.enchantment, 0) >= this.min_level;
	}

	public Serializer<EnchantmentIngredient> getSerializer() {
		return INSTANCE;
	}

}
