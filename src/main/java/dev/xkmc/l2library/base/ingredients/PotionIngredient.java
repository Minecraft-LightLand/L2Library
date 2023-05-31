package dev.xkmc.l2library.base.ingredients;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import javax.annotation.Nullable;

@SerialClass
public class PotionIngredient extends BaseIngredient<PotionIngredient> {

	public static final BaseIngredient.Serializer<PotionIngredient> INSTANCE =
			new BaseIngredient.Serializer<>(PotionIngredient.class, new ResourceLocation(L2Library.MODID, "potion"));

	@SerialClass.SerialField
	public Potion potion;

	@Deprecated
	public PotionIngredient() {

	}

	protected PotionIngredient validate() {
		return new PotionIngredient(potion);
	}

	public PotionIngredient(Potion potion) {
		super(PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
		this.potion = potion;
	}

	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		return PotionUtils.getPotion(stack) == potion;
	}

	public BaseIngredient.Serializer<PotionIngredient> getSerializer() {
		return INSTANCE;
	}

}
