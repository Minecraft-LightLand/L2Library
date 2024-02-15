package dev.xkmc.l2library.serial.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class CustomShapelessBuilder<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipeBuilder {

	private final AbstractShapelessRecipe.RecipeFactory<T> factory;

	public CustomShapelessBuilder(AbstractShapelessRecipe.RecipeFactory<T> factory, ItemLike result, int count) {
		super(RecipeCategory.MISC, result, count);
		this.factory = factory;
	}

	@Override
	public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
		this.ensureValid(pId);
		Advancement.Builder advancement$builder = pRecipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
				.rewards(AdvancementRewards.Builder.recipe(pId))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		ShapelessRecipe shapelessrecipe = new ShapelessRecipe(
				Objects.requireNonNullElse(this.group, ""),
				RecipeBuilder.determineBookCategory(this.category),
				new ItemStack(this.result, this.count),
				this.ingredients
		);
		pRecipeOutput.accept(pId, factory.map(shapelessrecipe), advancement$builder.build(pId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

}
