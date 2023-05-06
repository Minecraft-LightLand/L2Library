package dev.xkmc.l2library.serial.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.stream.Stream;

@SerialClass
public abstract class BaseIngredient<T extends BaseIngredient<T>> extends AbstractIngredient {

	@Deprecated
	protected BaseIngredient() {

	}

	public BaseIngredient(ItemStack display) {
		super(Stream.of(new ItemValue(display)));
	}

	protected abstract T validate();

	public abstract boolean test(ItemStack stack);

	public boolean isSimple() {
		return false;
	}

	public abstract Serializer<T> getSerializer();

	public JsonElement toJson() {
		JsonObject obj = JsonCodec.toJson(this).getAsJsonObject();
		obj.addProperty("type", getSerializer().id().toString());
		return obj;
	}

	public record Serializer<T extends BaseIngredient<T>>(Class<T> cls, ResourceLocation id)
			implements IIngredientSerializer<T> {

		public T parse(FriendlyByteBuf buffer) {
			return PacketCodec.from(buffer, cls, null).validate();
		}

		public T parse(JsonObject json) {
			return JsonCodec.from(json, cls, null).validate();
		}

		public void write(FriendlyByteBuf buffer, T ingredient) {
			PacketCodec.to(buffer, ingredient, cls);
		}
	}
}
