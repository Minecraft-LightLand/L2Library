package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class CustomSmithingBuilder<T extends AbstractSmithingRecipe<T>> extends UpgradeRecipeBuilder {

	private final RegistryEntry<AbstractSmithingRecipe.Serializer<T>> serializer;

	public CustomSmithingBuilder(RegistryEntry<AbstractSmithingRecipe.Serializer<T>> serializer, Ingredient left, Ingredient right, Item result) {
		super(serializer.get(), left, right, result);
		this.serializer = serializer;
	}

	public void save(Consumer<FinishedRecipe> p_200485_1_, ResourceLocation p_200485_2_) {
		this.ensureValid(p_200485_2_);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_200485_2_)).rewards(AdvancementRewards.Builder.recipe(p_200485_2_)).requirements(RequirementsStrategy.OR);
		p_200485_1_.accept(new Result(p_200485_2_, new ResourceLocation(p_200485_2_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_200485_2_.getPath())));
	}

	public CustomSmithingBuilder<T> unlockedBy(RegistrateRecipeProvider pvd, ItemLike item) {
		this.advancement.addCriterion("has_" + pvd.safeName(item.asItem()),
				DataIngredient.items(item.asItem()).getCritereon(pvd));
		return this;
	}


	public void addAdditional(JsonObject obj) {
	}

	class Result extends UpgradeRecipeBuilder.Result {

		public Result(ResourceLocation id, ResourceLocation folder) {
			super(id, serializer.get(), base, addition, result, advancement, folder);
		}

		@Override
		public void serializeRecipeData(JsonObject obj) {
			super.serializeRecipeData(obj);
			addAdditional(obj);
		}

		@Override
		public RecipeSerializer<?> getType() {
			return serializer.get();
		}
	}


}
