package dev.xkmc.l2library.base.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSmithingRecipe<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe {

	public static final Ingredient TEMPLATE_PLACEHOLDER = Ingredient.of(Items.PAPER);

	public AbstractSmithingRecipe(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result) {
		super(rl, TEMPLATE_PLACEHOLDER, left, right, result);
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractSmithingRecipe<T>> {

		T create(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result);

	}

	public static class Serializer<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			SmithingTransformRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.base, r.addition, r.result);
		}


		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			SmithingTransformRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.base, r.addition, r.result);
		}


		public void toJson(T recipe, JsonObject obj) {
		}

	}

	public static class SerialSerializer<T extends AbstractSmithingRecipe<T>> extends Serializer<T> {

		private final Class<T> cls;

		public SerialSerializer(Class<T> cls, RecipeFactory<T> factory) {
			super(factory);
			this.cls = cls;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			return Objects.requireNonNull(JsonCodec.from(obj, cls, super.fromJson(id, obj)));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, SmithingTransformRecipe rec) {
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