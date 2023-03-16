package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Consumer;

public class CustomShapelessBuilder<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipeBuilder implements IExtendedRecipe {

	private final RegistryEntry<AbstractShapelessRecipe.Serializer<T>> serializer;

	public CustomShapelessBuilder(RegistryEntry<AbstractShapelessRecipe.Serializer<T>> serializer, ItemLike result, int count) {
		super(RecipeCategory.MISC, result, count);
		this.serializer = serializer;
	}

	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		pvd.accept(new ExtendedRecipeResult(new ShapelessRecipeBuilder.Result(id, this.result, this.count,
				this.group == null ? "" : this.group, CraftingBookCategory.MISC, this.ingredients, this.advancement,
				new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())), this));
	}

	public CustomShapelessBuilder<T> unlockedBy(RegistrateRecipeProvider pvd, ItemLike item) {
		this.advancement.addCriterion("has_" + pvd.safeName(item.asItem()),
				DataIngredient.items(item.asItem()).getCritereon(pvd));
		return this;
	}

	public void addAdditional(JsonObject obj) {

	}

	@Override
	public RecipeSerializer<?> getType() {
		return serializer.get();
	}

}
