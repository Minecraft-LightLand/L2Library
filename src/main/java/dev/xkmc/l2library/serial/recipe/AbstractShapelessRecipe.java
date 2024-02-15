package dev.xkmc.l2library.serial.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapelessRecipe<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe {

	public AbstractShapelessRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(group, CraftingBookCategory.MISC, result, ingredients);
	}

	public List<ItemStack> getJEIResult() {
		return List.of(result);
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapelessRecipe<T>> {

		T create(String group, ItemStack result, NonNullList<Ingredient> ingredients);

		default T map(ShapelessRecipe r) {
			return create(r.getGroup(), r.result, r.getIngredients());
		}

	}

	public static class Serializer<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		@Override
		public Codec<ShapelessRecipe> codec() {
			return super.codec().xmap(factory::map, e -> e);
		}

		public T fromNetwork(FriendlyByteBuf obj) {
			return factory.map(super.fromNetwork(obj));
		}

	}

}

