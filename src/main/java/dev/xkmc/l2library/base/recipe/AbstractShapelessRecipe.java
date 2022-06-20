package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractShapelessRecipe<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe {

	public AbstractShapelessRecipe(ResourceLocation rl, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(rl, group, result, ingredients);
	}

	public List<ItemStack> getJEIResult() {
		return List.of(getResultItem());
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapelessRecipe<T>> {

		T create(ResourceLocation rl, String group, ItemStack result, NonNullList<Ingredient> ingredients);

	}

	public static class Serializer<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			ShapelessRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
		}

		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			ShapelessRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
		}

	}

}
