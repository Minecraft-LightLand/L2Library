package dev.xkmc.l2library.serial.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record L2ConditionSerializer<T extends ICondition>(
		ResourceLocation id,
		Class<T> cls) implements IConditionSerializer<T> {

	@Override
	public void write(JsonObject json, T value) {
		JsonElement elem = JsonCodec.toJson(value, cls);
		assert elem != null;
		for (var e : elem.getAsJsonObject().entrySet()) {
			json.add(e.getKey(), e.getValue());
		}
	}

	@Override
	public T read(JsonObject json) {
		T ans = JsonCodec.from(json, cls, null);
		assert ans != null;
		return ans;
	}

	@Override
	public ResourceLocation getID() {
		return id;
	}

}
