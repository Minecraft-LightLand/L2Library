package dev.xkmc.l2library.compat.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessPatchouliBuilder extends ShapelessRecipeBuilder {

	private final ResourceLocation book;

	public ShapelessPatchouliBuilder(ResourceLocation book) {
		super(PatchouliItems.BOOK, 1);
		this.book = book;
	}

	@Override
	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);
		ResourceLocation ans = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
		pvd.accept(new Shapeless(book, id, this.ingredients, this.advancement, ans));
	}

	public record Shapeless(ResourceLocation book, ResourceLocation id, List<Ingredient> ingredients,
							Advancement.Builder advancement, ResourceLocation advId) implements FinishedRecipe {

		public void serializeRecipeData(JsonObject json) {
			JsonArray jsonarray = new JsonArray();
			for (Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}
			json.add("ingredients", jsonarray);
			json.addProperty("book", book.toString());

		}

		public RecipeSerializer<?> getType() {
			return ShapelessBookRecipe.SERIALIZER;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advId;
		}

	}

}
