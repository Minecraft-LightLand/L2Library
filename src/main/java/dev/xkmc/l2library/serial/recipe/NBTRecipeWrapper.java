package dev.xkmc.l2library.serial.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public record NBTRecipeWrapper(RecipeOutput pvd, ItemStack stack) implements RecipeOutput {

	@Override
	public Advancement.Builder advancement() {
		return pvd.advancement();
	}

	@Override
	public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		if (recipe instanceof ShapedRecipe r) {
			r.result.setTag(stack.getTag());
		}
		if (recipe instanceof ShapelessRecipe r) {
			r.result.setTag(stack.getTag());
		}
		pvd.accept(id, recipe, advancement, conditions);
	}

}
