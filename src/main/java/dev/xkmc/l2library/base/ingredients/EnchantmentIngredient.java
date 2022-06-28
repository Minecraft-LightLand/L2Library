package dev.xkmc.l2library.base.ingredients;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@SerialClass
public class EnchantmentIngredient extends BaseIngredient<EnchantmentIngredient> {

	public static final Serializer<EnchantmentIngredient> INSTANCE =
			new Serializer<>(EnchantmentIngredient.class, new ResourceLocation(L2Library.MODID, "enchantment"));

	@SerialClass.SerialField
	public Enchantment enchantment;
	@SerialClass.SerialField
	public int minLevel;

	@Deprecated
	public EnchantmentIngredient() {

	}

	protected EnchantmentIngredient validate() {
		return new EnchantmentIngredient(enchantment, minLevel);
	}

	public EnchantmentIngredient(Enchantment enchantment, int minLevel) {
		super(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, minLevel)));
		this.enchantment = enchantment;
		this.minLevel = minLevel;
	}

	public boolean test(ItemStack stack) {
		return stack.getEnchantmentLevel(this.enchantment) >= this.minLevel;
	}

	public Serializer<EnchantmentIngredient> getSerializer() {
		return INSTANCE;
	}

}
