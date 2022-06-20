package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractSmithingRecipe<T extends AbstractSmithingRecipe<T>> extends UpgradeRecipe {

	public AbstractSmithingRecipe(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result) {
		super(rl, left, right, result);
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractSmithingRecipe<T>> {

		T create(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result);

	}

	public static class Serializer<T extends AbstractSmithingRecipe<T>> extends UpgradeRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			UpgradeRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.base, r.addition, r.getResultItem());
		}

		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			UpgradeRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.base, r.addition, r.getResultItem());
		}

	}

}