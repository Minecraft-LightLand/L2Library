package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapedRecipe<T extends AbstractShapedRecipe<T>> extends ShapedRecipe {

	public AbstractShapedRecipe(ResourceLocation rl, String group, int w, int h, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(rl, group, CraftingBookCategory.MISC, w, h, ingredients, result);
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapedRecipe<T>> {

		T create(ResourceLocation rl, String group, int w, int h, NonNullList<Ingredient> ingredients, ItemStack result);

	}

	public static class Serializer<T extends AbstractShapedRecipe<T>> extends ShapedRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			ShapedRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.getGroup(), r.getRecipeWidth(), r.getRecipeHeight(), r.getIngredients(), r.result);
		}

		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			ShapedRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.getGroup(), r.getRecipeWidth(), r.getRecipeHeight(), r.getIngredients(), r.result);
		}

		public void toJson(T recipe, JsonObject obj) {

		}

	}

	public static class SerialSerializer<T extends AbstractShapedRecipe<T>> extends Serializer<T> {

		private final Class<T> cls;

		public SerialSerializer(Class<T> cls, RecipeFactory<T> factory) {
			super(factory);
			this.cls = cls;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			return Objects.requireNonNull(JsonCodec.from(obj, cls, super.fromJson(id, obj)));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapedRecipe rec) {
			super.toNetwork(buf, rec);
			PacketCodec.to(buf, rec);
		}

		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			return PacketCodec.from(obj, cls, super.fromNetwork(id, obj));
		}

		public void toJson(T recipe, JsonObject obj) {
			JsonCodec.toJsonObject(recipe, obj);
		}

	}

}