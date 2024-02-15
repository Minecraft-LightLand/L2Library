package dev.xkmc.l2library.serial.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class CustomShapedBuilder<T extends AbstractShapedRecipe<T>> extends ShapedRecipeBuilder {

	private final AbstractShapedRecipe.RecipeFactory<T> factory;

	public CustomShapedBuilder(AbstractShapedRecipe.RecipeFactory<T> factory, ItemLike result, int count) {
		super(RecipeCategory.MISC, result, count);
		this.factory = factory;
	}

	@Override
	public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
		ShapedRecipePattern shapedrecipepattern = this.ensureValid(pId);
		Advancement.Builder advancement$builder = pRecipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
				.rewards(AdvancementRewards.Builder.recipe(pId))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		ShapedRecipe shapedrecipe = new ShapedRecipe(
				Objects.requireNonNullElse(this.group, ""),
				RecipeBuilder.determineBookCategory(this.category),
				shapedrecipepattern,
				new ItemStack(this.result, this.count),
				this.showNotification
		);
		T rec = factory.map(shapedrecipe);
		pRecipeOutput.accept(pId, rec, advancement$builder.build(pId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

}
