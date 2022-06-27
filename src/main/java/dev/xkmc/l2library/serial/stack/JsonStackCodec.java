package dev.xkmc.l2library.serial.stack;

import com.google.gson.JsonElement;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class JsonStackCodec {

	public abstract Optional<ItemStack> deserialize(JsonElement elem);

	public abstract Optional<JsonElement> serialize(ItemStack stack);

}
