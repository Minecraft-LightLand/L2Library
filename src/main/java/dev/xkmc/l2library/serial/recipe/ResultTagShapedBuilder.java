package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.custom_handler.StackHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.Objects;

public class ResultTagShapedBuilder extends ShapedRecipeBuilder implements IExtendedRecipe {

	private final ItemStack stack;

	public ResultTagShapedBuilder(ItemStack stack) {
		super(RecipeCategory.MISC, stack.getItem(), stack.getCount());
		this.stack = stack;
	}

	@Override
	public void save(RecipeOutput pvd, ResourceLocation id) {
		ShapedRecipePattern pattern = this.ensureValid(id);
		Advancement.Builder adv = pvd.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(adv::addCriterion);
		ShapedRecipe rec = new ShapedRecipe(
				Objects.requireNonNullElse(this.group, ""),
				RecipeBuilder.determineBookCategory(this.category),
				pattern,
				new ItemStack(this.result, this.count),
				this.showNotification
		);
		pvd.accept(id, rec, adv.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

	@Override
	public void addAdditional(JsonObject json) {
		json.add("result", StackHelper.serializeForgeItemStack(stack));
	}

	@Override
	public RecipeSerializer<?> getType() {
		return RecipeSerializer.SHAPED_RECIPE;
	}

}
