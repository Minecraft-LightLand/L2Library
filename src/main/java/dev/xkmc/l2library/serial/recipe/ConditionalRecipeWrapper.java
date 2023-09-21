package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ConditionalRecipeWrapper(FinishedRecipe base, String[] modid) implements FinishedRecipe {

	public static Consumer<FinishedRecipe> mod(RegistrateRecipeProvider pvd, String... modid) {
		return (r) -> pvd.accept(new ConditionalRecipeWrapper(r, modid));
	}

	@Override
	public void serializeRecipeData(JsonObject pJson) {
		base.serializeRecipeData(pJson);
	}

	@Override
	public ResourceLocation getId() {
		return base.getId();
	}

	@Override
	public RecipeSerializer<?> getType() {
		return base.getType();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		JsonObject ans = base.serializeAdvancement();
		if (ans == null) return null;
		addCondition(ans);
		return ans;
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return base.getAdvancementId();
	}

	@Override
	public JsonObject serializeRecipe() {
		JsonObject ans = base.serializeRecipe();
		addCondition(ans);
		return ans;
	}

	private void addCondition(JsonObject ans) {
		JsonArray conditions = new JsonArray();
		for (String str : modid) {
			JsonObject condition = new JsonObject();
			condition.addProperty("type", "forge:mod_loaded");
			condition.addProperty("modid", str);
			conditions.add(condition);
		}
		ans.add("conditions", conditions);
	}

}
