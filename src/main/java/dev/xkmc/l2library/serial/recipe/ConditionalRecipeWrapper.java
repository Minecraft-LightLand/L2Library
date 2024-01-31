package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ConditionalRecipeWrapper(FinishedRecipe base, ICondition... conditions) implements FinishedRecipe {

	public static Consumer<FinishedRecipe> mod(RegistrateRecipeProvider pvd, String... modid) {
		ICondition[] ans = new ICondition[modid.length];
		for (int i = 0; i < ans.length; i++) ans[i] = new ModLoadedCondition(modid[i]);
		return (r) -> pvd.accept(new ConditionalRecipeWrapper(r, ans));
	}

	public static Consumer<FinishedRecipe> of(RegistrateRecipeProvider pvd, ICondition... cond) {
		return (r) -> pvd.accept(new ConditionalRecipeWrapper(r, cond));
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
		ans.add("conditions", CraftingHelper.serialize(conditions()));
	}

}
