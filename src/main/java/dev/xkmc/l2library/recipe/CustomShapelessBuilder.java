package dev.xkmc.l2library.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Consumer;

public class CustomShapelessBuilder<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipeBuilder {

	private final RegistryEntry<AbstractShapelessRecipe.Serializer<T>> serializer;

	public CustomShapelessBuilder(RegistryEntry<AbstractShapelessRecipe.Serializer<T>> serializer, ItemLike result, int count) {
		super(result, count);
		this.serializer = serializer;
	}

	public void save(Consumer<FinishedRecipe> p_200485_1_, ResourceLocation p_200485_2_) {
		this.ensureValid(p_200485_2_);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_200485_2_)).rewards(AdvancementRewards.Builder.recipe(p_200485_2_)).requirements(RequirementsStrategy.OR);
		p_200485_1_.accept(new Result(p_200485_2_, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(p_200485_2_.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + p_200485_2_.getPath())));
	}


	public CustomShapelessBuilder<T> unlockedBy(RegistrateRecipeProvider pvd, ItemLike item) {
		this.advancement.addCriterion("has_" + pvd.safeName(item.asItem()),
				DataIngredient.items(item.asItem()).getCritereon(pvd));
		return this;
	}

	class Result extends ShapelessRecipeBuilder.Result {

		public Result(ResourceLocation id, Item out, int count, String group, List<Ingredient> in,
					  Advancement.Builder advancement, ResourceLocation folder) {
			super(id, out, count, group, in, advancement, folder);
		}

		@Override
		public RecipeSerializer<?> getType() {
			return serializer.get();
		}
	}


}
