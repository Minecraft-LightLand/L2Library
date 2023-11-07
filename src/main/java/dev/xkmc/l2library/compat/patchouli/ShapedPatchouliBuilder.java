package dev.xkmc.l2library.compat.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShapedPatchouliBuilder extends ShapedRecipeBuilder {

	private final ResourceLocation book;

	public ShapedPatchouliBuilder(ResourceLocation book) {
		super(RecipeCategory.MISC, PatchouliItems.BOOK, 1);
		this.book = book;
	}

	@Override
	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);
		pvd.accept(new Result(book, id, this.rows, this.key, this.advancement, id.withPrefix("recipes/")));
	}


	public record Result(ResourceLocation book, ResourceLocation id, List<String> pattern,
						 Map<Character, Ingredient> key, Advancement.Builder advancement,
						 ResourceLocation advancementId) implements FinishedRecipe {

		public void serializeRecipeData(JsonObject json) {
			JsonArray jsonarray = new JsonArray();
			for (String s : this.pattern) {
				jsonarray.add(s);
			}
			json.add("pattern", jsonarray);
			JsonObject jsonobject = new JsonObject();
			for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
				jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
			}
			json.add("key", jsonobject);
			json.addProperty("book", book.toString());
		}

		public RecipeSerializer<?> getType() {
			return ShapedBookRecipe.SERIALIZER;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}

	}

}
