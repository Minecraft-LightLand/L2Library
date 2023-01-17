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
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapelessRecipe<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe {

	public AbstractShapelessRecipe(ResourceLocation rl, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(rl, group, CraftingBookCategory.MISC, result, ingredients);
	}

	public List<ItemStack> getJEIResult() {
		return List.of(getResultItem());
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractShapelessRecipe<T>> {

		T create(ResourceLocation rl, String group, ItemStack result, NonNullList<Ingredient> ingredients);

	}

	public static class Serializer<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			ShapelessRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
		}

		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			ShapelessRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
		}

		public void toJson(T recipe, JsonObject obj) {
		}

	}

	public static class SerialSerializer<T extends AbstractShapelessRecipe<T>> extends Serializer<T> {

		private final Class<T> cls;

		public SerialSerializer(Class<T> cls, RecipeFactory<T> factory) {
			super(factory);
			this.cls = cls;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			return Objects.requireNonNull(JsonCodec.from(obj, cls, super.fromJson(id, obj)));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessRecipe rec) {
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

