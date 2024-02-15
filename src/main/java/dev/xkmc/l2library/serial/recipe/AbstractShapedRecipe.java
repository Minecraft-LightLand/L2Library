package dev.xkmc.l2library.serial.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapedRecipe<T extends AbstractShapedRecipe<T>> extends ShapedRecipe {

	public AbstractShapedRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
		super(group, CraftingBookCategory.MISC, pattern, result);
	}
	
	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapedRecipe<T>> {

		T create(String group, ShapedRecipePattern pattern, ItemStack result);

		default T map(ShapedRecipe r) {
			return create(r.getGroup(), r.pattern, r.result);
		}

	}

	public static class Serializer<T extends AbstractShapedRecipe<T>> extends ShapedRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		@Override
		public Codec<ShapedRecipe> codec() {
			return super.codec().xmap(factory::map, e -> e);
		}

		public T fromNetwork(FriendlyByteBuf obj) {
			return factory.map(super.fromNetwork(obj));
		}

	}

}