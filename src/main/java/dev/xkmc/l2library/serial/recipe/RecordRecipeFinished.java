package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record RecordRecipeFinished<T extends Record>(
		ResourceLocation id, RecipeSerializer<?> serializer, T recipe) implements FinishedRecipe {

	@Override
	public void serializeRecipeData(JsonObject json) {
		JsonObject obj = JsonCodec.toJson(recipe).getAsJsonObject();
		for (var e : obj.entrySet()) json.add(e.getKey(), e.getValue());
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getType() {
		return serializer;
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return null;
	}
}
