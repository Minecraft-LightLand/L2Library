package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.handler.StackHelper;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;

public class ResultTagShapedBuilder extends ShapedRecipeBuilder {

	private final ItemStack stack;

	public ResultTagShapedBuilder(ItemStack stack) {
		super(stack.getItem(), stack.getCount());
		this.stack = stack;
	}

	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		CreativeModeTab group = this.result.getItemCategory();
		String folder = (group == null ? "nil" : group.getRecipeFolderName()) + "/";
		pvd.accept(new Result(id, this.group == null ? "" : this.group, new ResourceLocation(id.getNamespace(),
				"recipes/" + folder + id.getPath())));
	}

	class Result extends ShapedRecipeBuilder.Result {

		public Result(ResourceLocation id, String group, ResourceLocation path) {
			super(id, result, count, group, rows, key, advancement, path);
		}

		@Override
		public void serializeRecipeData(JsonObject obj) {
			super.serializeRecipeData(obj);
			obj.add("result", StackHelper.serializeForgeItemStack(stack));
		}
	}


}
