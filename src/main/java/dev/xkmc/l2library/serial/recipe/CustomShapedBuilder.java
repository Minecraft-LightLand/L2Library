package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class CustomShapedBuilder<T extends AbstractShapedRecipe<T>> extends ShapedRecipeBuilder implements IExtendedRecipe {

	private final RegistryEntry<AbstractShapedRecipe.Serializer<T>> serializer;

	public CustomShapedBuilder(RegistryEntry<AbstractShapedRecipe.Serializer<T>> serializer, ItemLike result, int count) {
		super(RecipeCategory.MISC, result, count);
		this.serializer = serializer;
	}

	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		pvd.accept(new ExtendedRecipeResult(new ShapedRecipeBuilder.Result(id, result, count,
				this.group == null ? "" : this.group, CraftingBookCategory.MISC, rows, key, advancement,
				new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath()), false),
				this));
	}

	public CustomShapedBuilder<T> unlockedBy(RegistrateRecipeProvider pvd, ItemLike item) {
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
