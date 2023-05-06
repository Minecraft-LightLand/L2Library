package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractOldSmithingRecipe<T extends AbstractOldSmithingRecipe<T>> extends LegacyUpgradeRecipe {

	public AbstractOldSmithingRecipe(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result) {
		super(rl, left, right, result);
	}

	@Override
	public abstract Serializer<T> getSerializer();

	@FunctionalInterface
	public interface RecipeFactory<T extends AbstractOldSmithingRecipe<T>> {

		T create(ResourceLocation rl, Ingredient left, Ingredient right, ItemStack result);

	}

	public static class Serializer<T extends AbstractOldSmithingRecipe<T>> extends LegacyUpgradeRecipe.Serializer {

		private final RecipeFactory<T> factory;

		public Serializer(RecipeFactory<T> factory) {
			this.factory = factory;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			LegacyUpgradeRecipe r = super.fromJson(id, obj);
			return factory.create(r.getId(), r.base, r.addition, r.result);
		}


		public T fromNetwork(ResourceLocation id, FriendlyByteBuf obj) {
			LegacyUpgradeRecipe r = super.fromNetwork(id, obj);
			if (r == null) {
				return null;
			}
			return factory.create(r.getId(), r.base, r.addition, r.result);
		}


		public void toJson(T recipe, JsonObject obj) {
		}

	}

	public static class SerialSerializer<T extends AbstractOldSmithingRecipe<T>> extends Serializer<T> {

		private final Class<T> cls;

		public SerialSerializer(Class<T> cls, RecipeFactory<T> factory) {
			super(factory);
			this.cls = cls;
		}

		public T fromJson(ResourceLocation id, JsonObject obj) {
			return Objects.requireNonNull(JsonCodec.from(obj, cls, super.fromJson(id, obj)));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, LegacyUpgradeRecipe rec) {
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