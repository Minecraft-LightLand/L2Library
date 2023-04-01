package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record ExtendedRecipeResult(FinishedRecipe impl, IExtendedRecipe parent) implements FinishedRecipe {

	@Override
	public void serializeRecipeData(JsonObject json) {
		impl.serializeRecipeData(json);
		parent.addAdditional(json);
	}

	@Override
	public JsonObject serializeRecipe() {
		return impl().serializeRecipe();
	}

	@Override
	public ResourceLocation getId() {
		return impl().getId();
	}

	@Override
	public RecipeSerializer<?> getType() {
		return parent().getType();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return impl().serializeAdvancement();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return impl().getAdvancementId();
	}
}
